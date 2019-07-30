package io.choerodon.hap.excel.impl;

import io.choerodon.hap.excel.ExcelException;
import io.choerodon.hap.excel.util.TableUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DataSetExcelExportImpl extends HapExcelExportImpl {
    private static final Logger logger = LoggerFactory.getLogger(DataSetExcelExportImpl.class);

    public DataSetExcelExportImpl(SXSSFWorkbook workbook, DataSource dataSource, Map<String, String> table) throws SQLException {
        super(workbook, dataSource, new ArrayList<>(table.keySet()));
    }

    public void createExcelTemplate(Map<String, String> table) {
        SXSSFSheet sheet = createSheet();
        sheet.setDefaultColumnWidth(20);
        List<String> column = containColumns;
        List<String> title = new ArrayList<>(table.values());
        //设置title
        SXSSFRow titleRow = sheet.createRow(0);
        SXSSFRow columnRow = sheet.createRow(1);
        createTemplateRow(title, titleRow);
        //设置column
        createTemplateRow(column, columnRow);
        for (int i = 0; i < titleRow.getLastCellNum(); i++) {
            SXSSFCell cell = titleRow.getCell(i);
            SXSSFCell columnCell = columnRow.getCell(i);
            columnCell.setCellStyle(super.columnNameCellStyle);
            cell.setCellStyle(super.titleCellStyle);
        }
    }

    @Override
    public void fillSheet(List<Object> lists, Class dto) throws ClassNotFoundException, ExcelException {
        if (CollectionUtils.isEmpty(lists)) {
            return;
        }

        String tableName = TableUtils.getTable(dto).getName();
        SXSSFSheet sheet = wb.getSheetAt(wb.getNumberOfSheets() - 1);
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

    @Override
    public void createRow(SXSSFRow row, Object object, List<String> columns, ExportStrategy exportStrategy) throws ExcelException {

        createCell(row.createCell(0), "*", "String");
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
                throw new ExcelException("cell内容替换失败");
            }

            createCell(row.createCell(i), cellValue, exportStrategy.getCellType(column));
        }
    }

}
