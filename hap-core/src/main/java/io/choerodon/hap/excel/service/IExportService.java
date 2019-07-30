package io.choerodon.hap.excel.service;


import io.choerodon.hap.excel.dto.ExportConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author jialong.zuo@hand-china.com
 * @since 2016/11/30
 */
public interface IExportService {

    /**
     * @param sqlId               sqlId
     * @param exportConfig        导出信息
     * @param httpServletRequest  httpServletRequest
     * @param httpServletResponse httpServletResponse
     * @throws IOException IOException异常
     */
    void exportAndDownloadExcel(String sqlId, ExportConfig exportConfig, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException;

    /**
     * @param sqlId               sqlId
     * @param exportConfig        导出信息
     * @param httpServletRequest  httpServletRequest
     * @param httpServletResponse httpServletResponse
     * @param rowMaxNumber        sheet页最大导出数
     * @throws IOException IOException异常
     */
    void exportAndDownloadExcel(String sqlId, ExportConfig exportConfig, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, int rowMaxNumber) throws IOException;

}
