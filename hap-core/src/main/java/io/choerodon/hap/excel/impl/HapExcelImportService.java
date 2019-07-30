package io.choerodon.hap.excel.impl;


import io.choerodon.hap.excel.ExcelException;
import io.choerodon.hap.excel.service.IHapExcelImportService;
import org.apache.ibatis.session.SqlSession;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

/**
 * @author jialong.zuo@hand-china.com
 * @since 2017/1/22
 */
@Service
@Transactional
public class HapExcelImportService implements IHapExcelImportService {

    @Autowired
    @Qualifier(value = "dataSource")
    private DataSource dataSource;

    @Autowired
    private SqlSession sqlSession;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void loadExcel(InputStream inputStream, String tableName) throws ExcelException {

        HapExcelImportImpl excelUtil = new HapExcelImportImpl(tableName, sqlSession);
        excelUtil.process(inputStream);
    }

    @Override
    public void exportExcelTemplate(String tableName, HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest) throws IOException, SQLException, ExcelException {
        SXSSFWorkbook wb = new SXSSFWorkbook();
        HapExcelExportImpl defaultHapExcelExport = new HapExcelExportImpl(wb, dataSource, null);
        HapExcelExportImpl.setExcelHeader(httpServletResponse, httpServletRequest, tableName);
        defaultHapExcelExport.createExcelTemplate(tableName, false);
        try {
            defaultHapExcelExport.write(httpServletResponse.getOutputStream());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new ExcelException("模板生成失败");
        } finally {
            defaultHapExcelExport.close();
        }
    }

}
