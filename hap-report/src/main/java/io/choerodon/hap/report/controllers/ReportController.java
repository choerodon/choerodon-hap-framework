package io.choerodon.hap.report.controllers;

import io.choerodon.hap.report.dto.Report;
import io.choerodon.hap.report.dto.ReportFiles;
import io.choerodon.hap.report.service.IReportFilesService;
import io.choerodon.hap.report.service.IReportService;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.hap.util.dto.CodeValue;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.dto.ResponseData;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 对报表的操作.
 *
 * @author qiang.zeng
 * @since 2017/9/21
 */
@RestController
@RequestMapping(value = {"/sys/report", "/api/sys/report"})
public class ReportController extends BaseController {

    @Autowired
    private IReportService reportService;
    @Autowired
    private IReportFilesService reportFilesService;

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/query")
    public ResponseData query(HttpServletRequest request, Report report, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        return new ResponseData(reportService.selectOptions(report, null, page, pageSize));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/queryByCode")
    public ResponseData queryByCode(HttpServletRequest request, @RequestParam(required = false) String reportCode) {
        return new ResponseData(reportService.selectByReportCode(reportCode));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/submit")
    public ResponseData update(@RequestBody List<Report> dto, BindingResult result, HttpServletRequest request) {
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(reportService.batchUpdate(dto));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/remove")
    public ResponseData delete(HttpServletRequest request, @RequestBody List<Report> dto) {
        reportService.batchDelete(dto);
        return new ResponseData();
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/queryReportFileParams")
    public ResponseData queryReportFileParams(HttpServletRequest request, @RequestParam(required = false) String reportCode) {
        ReportFiles reportFiles = reportFilesService.selectReportFileParams(reportCode);
        String[] params = {};
        if (reportFiles != null && StringUtils.isNotEmpty(reportFiles.getParams())) {
            params = StringUtils.split(reportFiles.getParams(), ";");
        }
        return new ResponseData(Arrays.asList(params));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/getReportFileParams")
    public List<CodeValue> getReportFileParams(HttpServletRequest request, @RequestParam(required = false) String reportCode) {
        ReportFiles reportFiles = reportFilesService.selectReportFileParams(reportCode);
        String[] params = {};
        if (reportFiles != null && StringUtils.isNotEmpty(reportFiles.getParams())) {
            params = StringUtils.split(reportFiles.getParams(), ";");
        }
        List<CodeValue> codeValues = new ArrayList<>();
        if (ArrayUtils.isNotEmpty(params)) {
            for (String param : params) {
                CodeValue codeValue = new CodeValue();
                codeValue.setMeaning(param);
                codeValue.setValue(param);
                codeValues.add(codeValue);
            }
        }
        return codeValues;
    }

}