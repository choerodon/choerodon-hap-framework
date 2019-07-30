package io.choerodon.hap.excel.impl;

import io.choerodon.hap.excel.dto.ColumnInfo;
import io.choerodon.hap.excel.dto.ExportConfig;
import io.choerodon.hap.excel.service.IExportService;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jialong.zuo@hand-china.com
 * @since 2016/11/30.
 */
@Service
public class ExportServiceImpl implements IExportService {

    private static final String ENCODING = "UTF-8";

    private Logger logger = LoggerFactory.getLogger(ExportServiceImpl.class);

    @Autowired
    @Qualifier("sqlSessionFactory")
    private SqlSessionFactory sqlSessionFactory;

    @SuppressWarnings("unchecked")
    private void exportExcel(String sqlId, ExportConfig gridInfo, OutputStream outputStream, int rowMaxNumber) throws IOException {
        try (SXSSFWorkbook wb = new SXSSFWorkbook(50)) {
            CellStyle dateFormat = wb.createCellStyle();
            dateFormat.setDataFormat(wb.createDataFormat().getFormat("yyyy-MM-DD HH:mm:ss"));
            // row计数器
            final AtomicInteger count = new AtomicInteger(1);
            // sheet页row索引
            final AtomicInteger rowIndex = new AtomicInteger(1);

            initColumnType(gridInfo.getColumnsInfo(), gridInfo.getParam());

            final SXSSFSheet[] sheet = {wb.createSheet()};

            createHeaderRow(gridInfo.getColumnsInfo(), wb, sheet[0]);

            try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
                sqlSession.select(sqlId, gridInfo.getParam(), (resultContext -> {
                    Object object = resultContext.getResultObject();
                    sheet[0] = createSheet(wb, sheet[0], object, count, rowIndex, rowMaxNumber, gridInfo, dateFormat);
                }));
                wb.write(outputStream);
            } finally {
                wb.close();
                wb.dispose();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private SXSSFSheet createSheet(SXSSFWorkbook wb, SXSSFSheet sheet, Object object, AtomicInteger count, AtomicInteger rowIndex, int rowMaxNumber, ExportConfig gridInfo, CellStyle dateFormat) {
        if (count.get() % rowMaxNumber == 0) {
            sheet = wb.createSheet();
            createHeaderRow(gridInfo.getColumnsInfo(), wb, sheet);
            rowIndex.set(0);
        }
        count.getAndIncrement();
        SXSSFRow row = sheet.createRow(rowIndex.getAndIncrement());
        createRow(gridInfo.getColumnsInfo(), object, row, dateFormat);
        return sheet;
    }


    private void createRow(List<ColumnInfo> columnInfos, Object object, SXSSFRow row, CellStyle dateFormat) {
        for (int ii = 0; ii < columnInfos.size(); ii++) {
            Object fieldObject = null;
            try {
                fieldObject = PropertyUtils.getProperty(object, columnInfos.get(ii).getName());
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            String type = columnInfos.get(ii).getType();

            SXSSFCell cell = row.createCell(ii);

            if (null == fieldObject) {
                cell.setCellType(CellType.STRING);
                cell.setCellValue((String) null);
            } else {
                switch (type.toUpperCase()) {
                    case "NUMBER":
                    case "FLOAT":
                        cell.setCellType(CellType.NUMERIC);
                        cell.setCellValue((Float) fieldObject);
                        break;
                    case "DOUBLE":
                        cell.setCellType(CellType.NUMERIC);
                        cell.setCellValue((Double) fieldObject);
                        break;
                    case "INT":
                    case "INTEGER":
                    case "LONG":
                        cell.setCellType(CellType.NUMERIC);
                        cell.setCellValue((Long) fieldObject);
                        break;
                    case "DATE":
                        cell.setCellStyle(dateFormat);
                        cell.setCellValue((Date) fieldObject);
                        break;
                    case "BOOLEAN":
                        cell.setCellType(CellType.BOOLEAN);
                        cell.setCellValue((Boolean) fieldObject);
                        break;
                    default:
                        cell.setCellType(CellType.STRING);
                        cell.setCellValue((String) fieldObject);
                        break;
                }
            }
        }
    }

    private void createHeaderRow(List<ColumnInfo> columnInfos, SXSSFWorkbook wb, SXSSFSheet sheet) {
        SXSSFRow firstRow = sheet.createRow(0);
        // 设置列字体align
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        for (int i = 0; i < columnInfos.size(); i++) {
            SXSSFCell firstCell = firstRow.createCell(i);
            firstCell.setCellValue(columnInfos.get(i).getTitle());
            // 设置列宽度
            sheet.setColumnWidth(i, columnInfos.get(i).getWidth() * 80);

            firstCell.setCellStyle(cellStyle);
        }
    }

    private void initColumnType(List<ColumnInfo> columnInfos, Object object) {
        for (ColumnInfo columnInfo : columnInfos) {
            columnInfo.setType(ReflectionUtils.findField(object.getClass(), columnInfo.getName()).getType().getSimpleName());
        }

    }

    @Override
    public void exportAndDownloadExcel(String sqlId, ExportConfig exportConfig, HttpServletRequest httpServletRequest,
                                       HttpServletResponse httpServletResponse, int rowMaxNumber) throws IOException {

        String name = exportConfig.getFileName() + ".xlsx";
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
        try (OutputStream outputStream = httpServletResponse.getOutputStream()) {
            exportExcel(sqlId, exportConfig, outputStream, rowMaxNumber);
        }
    }

    @Override
    public void exportAndDownloadExcel(String sqlId, ExportConfig exportConfig, HttpServletRequest httpServletRequest,
                                       HttpServletResponse httpServletResponse) throws IOException {
        exportAndDownloadExcel(sqlId, exportConfig, httpServletRequest, httpServletResponse, 1000000);
    }
}
