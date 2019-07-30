package io.choerodon.hap.excel.impl;

import io.choerodon.hap.excel.ExcelException;
import io.choerodon.hap.excel.util.TableUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jialong.zuo@hand-china.com
 * @since 2017/4/17
 */
public class HapExcelImportImpl extends DefaultHandler {

    private SharedStringsTable sst;
    private String lastContents;
    private boolean nextIsString;

    private Connection connection;

    private ImportStrategy rowStrategy;

    private int sheetIndex = -1;
    private List<String> rowlist = new ArrayList<>();
    private int curRow = 0;
    private int curCol = 0;

    private Logger logger = LoggerFactory.getLogger(getClass());

    //定义前一个元素和当前元素的位置，用来计算其中空的单元格数量，如A6和A8等
    private String preRef = null;
    private String ref = null;
    //定义该文档一行最大的单元格数，用来补全一行最后可能缺失的单元格
    private String maxRef = null;

    private List<String> tables;

    public HapExcelImportImpl(String tableName, SqlSession sqlSession) {

        this.connection = sqlSession.getConnection();

        List<Class> allTables = new ArrayList<>();
        TableUtils.getAllChildrenTable(TableUtils.getTableClass(tableName), allTables);
        this.tables = allTables.stream().map(v -> TableUtils.getTable(v).getName()).collect(Collectors.toList());
    }

    /**
     * 读取所有工作簿的入口方法.
     *
     * @param inputStream InputStream
     * @throws ExcelException excel处理异常
     */
    public void process(InputStream inputStream) throws ExcelException {
        try {
            OPCPackage pkg = OPCPackage.open(inputStream);
            XSSFReader r = new XSSFReader(pkg);
            SharedStringsTable sharedStringsTable = r.getSharedStringsTable();

            XMLReader parser = fetchSheetParser(sharedStringsTable);

            XSSFReader.SheetIterator sheets = (XSSFReader.SheetIterator) r.getSheetsData();

            while (sheets.hasNext()) {
                curRow = 0;
                sheetIndex++;
                InputStream sheet = sheets.next();
                InputSource sheetSource = new InputSource(sheet);
                String sheetName = sheets.getSheetName();
                if (!tables.contains(sheetName)) {
                    throw new ExcelException("缺少表" + sheetName + "的导入权限");
                }
                rowStrategy = new ImportStrategy(connection, sheetName);
                parser.parse(sheetSource);
                sheet.close();
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ExcelException(e.getMessage());
        } finally {
            cleanData();
        }

    }

    public void cleanData() {
        this.sheetIndex = -1;
        this.rowlist = new ArrayList<>();
        this.curRow = 0;
        this.curCol = 0;
    }

    public XMLReader fetchSheetParser(SharedStringsTable sst) throws SAXException {
        XMLReader parser = XMLReaderFactory
                .createXMLReader("org.apache.xerces.parsers.SAXParser");
        this.sst = sst;
        parser.setContentHandler(this);
        return parser;
    }

    /**
     * 解析一个element的开始时触发事件
     */
    @Override
    public void startElement(String uri, String localName, String name,
                             Attributes attributes) throws SAXException {

        // c => cell
        if (name.equals("c")) {
            //前一个单元格的位置
            if (preRef == null) {
                preRef = attributes.getValue("r");
            } else {
                preRef = ref;
            }
            //当前单元格的位置
            ref = attributes.getValue("r");

            // Figure out if the value is an index in the SST
            String cellType = attributes.getValue("t");
            if (cellType != null && cellType.equals("s")) {
                nextIsString = true;
            } else {
                nextIsString = false;
            }

        }
        // Clear contents cache
        lastContents = "";
    }


    /**
     * 解析一个element元素结束时触发事件
     */
    @Override
    public void endElement(String uri, String localName, String name)
            throws SAXException {
        // Process the last contents as required.
        // Do now, as characters() may be called more than once
        if (nextIsString) {
            int idx = Integer.parseInt(lastContents);
            lastContents = new XSSFRichTextString(sst.getEntryAt(idx)).toString();
            nextIsString = false;
        }

        // v => contents of a cell
        // Output after we've seen the string contents
        if (name.equals("v")) {
            String value = lastContents.trim();
            value = value.equals("") ? " " : value;
            //补全单元格之间的空单元格
            if (!ref.equals(preRef)) {
                int len = countNullCell(ref, preRef);
                for (int i = 0; i < len; i++) {
                    rowlist.add(curCol, "");
                    curCol++;
                }
            }
            rowlist.add(curCol, value);
            curCol++;
        } else {
            //如果标签名称为 row，这说明已到行尾，调用 optRows() 方法
            if (name.equals("row")) {
                //默认第一行为表头，以该行单元格数目为最大数目
                if (curRow == 1 && sheetIndex == 0) {
                    maxRef = ref;
                }
                //补全一行尾部可能缺失的单元格
                if (maxRef != null) {
                    int len = countNullCell(maxRef, ref);
                    for (int i = 0; i <= len; i++) {
                        rowlist.add(curCol, "");
                        curCol++;
                    }
                }
                try {
                    if (!rowlist.isEmpty()) {
                        rowStrategy.optRow(curRow, rowlist);
                    }
                } catch (ExcelException e) {
                    logger.error(e.getMessage(), e);
                    throw new SAXException("导入失败！Sheet页：" + rowStrategy.tableName + "，第" + (curRow + 1) + "行");
                }
                curRow++;
                //一行的末尾重置一些数据
                rowlist.clear();
                curCol = 0;
                preRef = null;
                ref = null;
            }
        }
    }

    public int countNullCell(String ref, String preRef) {
        //excel2007最大行数是1048576，最大列数是16384，最后一列列名是XFD
        String xfd = ref.replaceAll("\\d+", "");
        String xfd_1 = preRef.replaceAll("\\d+", "");

        xfd = fillChar(xfd, 3, '@', true);
        xfd_1 = fillChar(xfd_1, 3, '@', true);

        char[] letter = xfd.toCharArray();
        char[] letter_1 = xfd_1.toCharArray();
        int res = (letter[0] - letter_1[0]) * 26 * 26 + (letter[1] - letter_1[1]) * 26 + (letter[2] - letter_1[2]);
        return res - 1;
    }

    /**
     * 字符串的填充
     *
     * @param str
     * @param len
     * @param let
     * @param isPre
     * @return
     */
    String fillChar(String str, int len, char let, boolean isPre) {
        int len_1 = str.length();
        if (len_1 < len) {
            if (isPre) {
                StringBuilder strBuilder = new StringBuilder(str);
                for (int i = 0; i < (len - len_1); i++) {
                    strBuilder.insert(0, let);
                }
                str = strBuilder.toString();
            } else {
                StringBuilder strBuilder = new StringBuilder(str);
                for (int i = 0; i < (len - len_1); i++) {
                    strBuilder.append(let);
                }
                str = strBuilder.toString();
            }
        }
        return str;
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        // 得到单元格内容的值
        lastContents += new String(ch, start, length);
    }

    /**
     * 在excel解析完成后执行
     */
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException("关闭数据库连接失败");
        }
    }

}
