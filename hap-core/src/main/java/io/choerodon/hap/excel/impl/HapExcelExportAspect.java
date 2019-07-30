package io.choerodon.hap.excel.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.choerodon.hap.excel.ExcelException;
import io.choerodon.hap.excel.annotation.ExcelExport;
import io.choerodon.hap.excel.service.IHapExcelExportService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jialong.zuo@hand-china.com
 * @since 2017/11/20.
 */
@Aspect
@Component(value = "excelExportAspect")
public class HapExcelExportAspect {
    private Logger logger = LoggerFactory.getLogger(HapExcelExportAspect.class);
    @Autowired
    private IHapExcelExportService exportService;

    @AfterReturning(value = "@annotation(excelExport)", returning = "responseData")
    public void afterController(JoinPoint joinpoint, ExcelExport excelExport, Object responseData) throws IOException, SQLException, ExcelException {

        HttpServletRequest httpServletRequest = null;
        HttpServletResponse httpServletResponse = null;

        for (int i = 0; i < joinpoint.getArgs().length; i++) {
            if (joinpoint.getArgs()[i] instanceof HttpServletRequest) {
                httpServletRequest = (HttpServletRequest) joinpoint.getArgs()[i];
            } else if (joinpoint.getArgs()[i] instanceof HttpServletResponse) {
                httpServletResponse = (HttpServletResponse) joinpoint.getArgs()[i];
            }
        }
        if (httpServletRequest == null) {
            logger.error("httpServletRequest is null!");
            return;
        }
        if (httpServletRequest.getParameterMap().containsKey("_HAP_EXCEL_EXPORT_COLUMNS")) {
            String columnsJson = httpServletRequest.getParameterMap().get("_HAP_EXCEL_EXPORT_COLUMNS")[0];
            ObjectMapper mapper = new ObjectMapper();
            ArrayNode node = mapper.readValue(columnsJson, ArrayNode.class);
            List<String> columns = new ArrayList<>();
            node.forEach(v -> columns.add(v.get("column").asText()));
            exportService.exportAndDownloadExcel(responseData, httpServletRequest, httpServletResponse, columns);
        }
    }


}
