package io.choerodon.hap.excel.impl;

import io.choerodon.base.annotation.ExcelJoinColumn;
import io.choerodon.hap.excel.ExcelException;
import io.choerodon.hap.excel.util.TableUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.EntityTable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * @author jialong.zuo@hand-china.com
 * @since 2017/11/20.
 */
public class HapExcelExportImpl {

    protected CellStyle dateCellStyle;

    protected CellStyle titleCellStyle;
    protected CellStyle columnNameCellStyle;

    protected SXSSFWorkbook wb;

    Connection connection;

    protected List<String> containColumns;

    private Logger logger = LoggerFactory.getLogger(getClass());

    protected Map<String, List<String>> columnMapping = new HashMap<>();

    protected SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);

    private static final String CELL_TYPE_STRING = "String";
    private static final String ENCODING = "UTF-8";

    /**
     * @param workbook   SXSSFWorkbook
     * @param dataSource DataSource
     * @param columns    字段列 列表
     * @throws SQLException SQL异常
     */
    public HapExcelExportImpl(SXSSFWorkbook workbook, DataSource dataSource, List<String> columns) throws SQLException {
        CellStyle dateFormat = workbook.createCellStyle();
        dateFormat.setDataFormat(workbook.createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss"));
        dateCellStyle = dateFormat;
        titleCellStyle = workbook.createCellStyle();
        columnNameCellStyle = workbook.createCellStyle();
        short GREY_40_PERCENT = 23;
        titleCellStyle.setFillForegroundColor(GREY_40_PERCENT);
        titleCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);//填暗红色
        Font font = workbook.createFont();
        font.setColor(font.getIndex());
        titleCellStyle.setFont(font);

        columnNameCellStyle.setFillForegroundColor(GREY_40_PERCENT);
        columnNameCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);//填暗红色
        columnNameCellStyle.setFont(font);
        this.wb = workbook;
        this.containColumns = columns;
        connection = dataSource.getConnection();

    }

    /**
     * @param lists 数据对象列表
     * @param dto   Class
     * @throws ClassNotFoundException 类找不到异常
     * @throws ExcelException         Excel导出异常
     */
    public void fillSheet(List<Object> lists, Class dto) throws ClassNotFoundException, ExcelException {
        if (CollectionUtils.isEmpty(lists)) {
            return;
        }
        String tableName = TableUtils.getTable(dto).getName();
        SXSSFSheet sheet = wb.getSheet(tableName);
        ExportStrategy exportStrategy = new ExportStrategy(connection, tableName);
        sheet.setColumnWidth(0, 7 * 256);
        List<String> columns = Optional.ofNullable(columnMapping.get(tableName)).orElseGet(() -> {
            List<String> column = new ArrayList<>();
            SXSSFRow columnName = sheet.getRow(1);
            for (int i = 0; i < columnName.getLastCellNum(); i++) {
                column.add(columnName.getCell(i).getStringCellValue());
                String type = exportStrategy.getCellType(columnName.getCell(i).getStringCellValue());
                setCellWidth(sheet, type, i);
            }
            columnMapping.put(tableName, column);
            return column;
        });
        int rowNum = sheet.getLastRowNum();
        //处理每行数据
        for (int i = 0; i < lists.size(); i++) {
            SXSSFRow row = sheet.createRow(rowNum + i + 1);
            createRow(row, lists.get(i), columns, exportStrategy);
        }

    }


    /**
     * 生成行数据.
     *
     * @param row            行
     * @param object         数据对象
     * @param columns        字段类
     * @param exportStrategy 导出策略
     * @throws ExcelException Excel导出异常
     */
    public void createRow(SXSSFRow row, Object object, List<String> columns, ExportStrategy exportStrategy) throws ExcelException {

        createCell(row.createCell(0), "*", CELL_TYPE_STRING);
        for (int i = 1; i < columns.size(); i++) {
            String column = columns.get(i);
            String cell = null;
            try {
                cell = BeanUtils.getProperty(object, column);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            String cellValue = null;
            try {
                Object v = exportStrategy.translateCellValue(column, cell, ExcelSheetStrategy.TranslateType.joinColumnToAlterCloumn, object);
                if (null != v) {
                    cellValue = v.toString();
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new ExcelException("cell内容替换失败");
            }

            createCell(row.createCell(i), cellValue, exportStrategy.getCellType(column));
        }
        getChildrenData(object);
    }

    @SuppressWarnings("unchecked")
    public void getChildrenData(Object object) throws ExcelException {
        try {
            Class parentClass = object.getClass();
            List<Class> childClasses = TableUtils.getChildren(parentClass);
            for (Class childClass : childClasses) {
                Object child = childClass.newInstance();

                List<Field> fields = TableUtils.getExcelJoinColumn(childClass);
                for (Field field : fields) {
                    ExcelJoinColumn ann = field.getAnnotationsByType(ExcelJoinColumn.class)[0];
                    if (ann.JoinTable() == parentClass) {
                        String value = BeanUtils.getProperty(object, ann.JoinColumn());
                        BeanUtils.setProperty(child, field.getName(), value);
                    }
                }
                List<Object> responseData = TableUtils.getBaseMapperByType(childClass, ExcelSheetStrategy.MapperType.Select).select(child);
                fillSheet(responseData, childClass);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ExcelException("获取子表数据失败");
        }
    }


    /**
     * 根据dto生成对应sheet导出模板.
     *
     * @param dto           Class
     * @param containStdWho 是否包含标准WHO字段
     */
    private void createSheetTemplate(Class dto, boolean containStdWho) {
        EntityTable tableEntity = TableUtils.getTable(dto);
        SXSSFSheet sheet = createSheet(tableEntity.getName());
        sheet.setDefaultColumnWidth(20);
        List<String> column = TableUtils.getColumn(tableEntity, containColumns, containStdWho);
        List<String> title = TableUtils.getTitle(column, dto);
        //设置title
        SXSSFRow titleRow = sheet.createRow(0);
        SXSSFRow columnRow = sheet.createRow(1);
        createTemplateRow(title, titleRow);
        //设置column
        createTemplateRow(column, columnRow);
        for (int i = 0; i < titleRow.getLastCellNum(); i++) {
            SXSSFCell cell = titleRow.getCell(i);
            SXSSFCell columnCell = columnRow.getCell(i);
            columnCell.setCellStyle(columnNameCellStyle);
            cell.setCellStyle(titleCellStyle);
        }

    }

    /**
     * 解析dto包含子表创建sheet.
     *
     * @param tableName     表名
     * @param containStdWho 是否包含标准WHO字段
     * @throws IOException IO异常
     */
    public void createExcelTemplate(String tableName, boolean containStdWho) throws IOException {
        List<Class> allTables = new ArrayList<>();
        TableUtils.getAllChildrenTable(TableUtils.getTableClass(tableName), allTables);
        for (Class tableDto : allTables) {
            createSheetTemplate(tableDto, containStdWho);
        }
    }

    /**
     * 解析dto包含子表创建sheet.
     *
     * @param tableName 表名
     * @throws IOException IO异常
     */
    public void createExcelTemplate(String tableName) throws IOException {
        createExcelTemplate(tableName, true);
    }


    /**
     * 设置http请求报文为下载文件.
     *
     * @param httpServletResponse HttpServletResponse
     * @param httpServletRequest  HttpServletRequest
     * @param fileName            文件名称
     * @throws UnsupportedEncodingException 不支持的编码异常
     **/
    public static void setExcelHeader(HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest, String fileName) throws UnsupportedEncodingException {

        String name = fileName + ".xlsx";
        String userAgent = httpServletRequest.getHeader("User-Agent");
        if (userAgent.contains("Firefox")) {
            name = new String(name.getBytes(ENCODING), "ISO8859-1");
        } else {
            name = URLEncoder.encode(name, ENCODING);
        }
        httpServletResponse.addHeader("Content-Disposition",
                "attachment; filename=\"" + name + "\"");
        httpServletResponse.setContentType("application/vnd.ms-excel" + ";charset=" + ENCODING);
        httpServletResponse.setHeader("Accept-Ranges", "bytes");

    }


    /**
     * 创建cell.
     *
     * @param cell        SXSSFCell
     * @param fieldObject 字段
     * @param type        类型
     */
    public void createCell(SXSSFCell cell, String fieldObject, String type) {
        if (null == fieldObject) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue((String) null);
        } else {
            if (null == type) {
                type = fieldObject.getClass().getSimpleName();
            }

            switch (type.toUpperCase()) {
                case "NUMBER":
                case "FLOAT":
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue(Float.valueOf(fieldObject));
                    break;
                case "DOUBLE":
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue(Double.valueOf(fieldObject));
                    break;
                case "INT":
                case "INTEGER":
                case "LONG":
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue(Long.valueOf(fieldObject));
                    break;
                case "DATE":
                    Date date;
                    try {
                        date = sdf.parse(fieldObject);
                    } catch (ParseException e) {
                        logger.error("can not parse date value,skip...");
                        break;
                    }
                    cell.setCellStyle(dateCellStyle);
                    cell.setCellValue(date);
                    break;
                case "BOOLEAN":
                    cell.setCellType(CellType.BOOLEAN);
                    cell.setCellValue(Boolean.valueOf(fieldObject));
                    break;
                default:
                    cell.setCellType(CellType.STRING);
                    cell.setCellValue(fieldObject);
                    break;
            }
        }

    }

    public void setCellWidth(SXSSFSheet sheet, String type, int columnIndex) {
        if (null == type) {
            return;
        }
        switch (type.toUpperCase()) {
            case "STRING":
                sheet.setColumnWidth(columnIndex, 22 * 256);
                break;
            case "NUMBER":
            case "FLOAT":
            case "DOUBLE":
            case "INT":
            case "INTEGER":
            case "LONG":
            case "BOOLEAN":
                sheet.setColumnWidth(columnIndex, 15 * 256);
                break;
            case "DATE":
                sheet.setColumnWidth(columnIndex, 25 * 256);
                break;
            default:
                sheet.setColumnWidth(columnIndex, 22 * 256);
                break;
        }

    }

    /**
     * 生产excel title和column
     *
     * @param title    标题
     * @param titleRow 标题行
     */
    public void createTemplateRow(List<String> title, SXSSFRow titleRow) {
        createCell(titleRow.createCell(0), "*", CELL_TYPE_STRING);

        for (int i = 0; i < title.size(); i++) {

            createCell(titleRow.createCell(i + 1), title.get(i), CELL_TYPE_STRING);
        }
    }


    public SXSSFSheet createSheet(String sheetName) {
        return wb.createSheet(sheetName);
    }

    public SXSSFSheet createSheet() {
        return wb.createSheet();
    }

    public void write(OutputStream outputStream) throws IOException {
        wb.write(outputStream);
    }

    public void close() throws IOException {
        wb.close();
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException("关闭数据库连接失败");
        }

    }

}
