package io.choerodon.hap.excel.service;


import io.choerodon.hap.excel.ExcelException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author jialong.zuo@hand-china.com
 * @since 2016/11/30
 */
public interface IHapExcelExportService {

    /**
     * @param responseData        导出数据
     * @param httpServletRequest  httpServletRequest
     * @param httpServletResponse httpServletResponse
     * @param columns             选中所需导出的列
     * @throws IOException    io异常
     * @throws ExcelException excel异常
     * @throws SQLException   sql异常
     */
    void exportAndDownloadExcel(Object responseData, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, List<String> columns) throws IOException, SQLException, ExcelException;


    void exportAndDownloadExcelByDataSet(Object responseData, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Map<String, String> columns) throws IOException, SQLException, ExcelException;
}
