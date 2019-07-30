package io.choerodon.hap.api.application.controllers;

import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.api.application.dto.ApiApplication;
import io.choerodon.hap.api.application.service.IApiApplicationService;
import io.choerodon.hap.api.gateway.dto.ApiServer;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.dto.ResponseData;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 服务控制器.
 *
 * @author lijian.yin@hand-china.com
 * @since 2017/11/15.
 **/

@RestController
@RequestMapping(value = {"/sys/application/app", "/api/sys/application/app"})
public class ApiApplicationController extends BaseController {

    @Autowired
    private IApiApplicationService service;

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/query")
    public ResponseData query(ApiApplication dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        return new ResponseData(service.selectApplications(dto, page, pageSize));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/{applicationId}/detail")
    public ResponseData queryDetail(HttpServletRequest request, @PathVariable Long applicationId) {
        return new ResponseData(Collections.singletonList(service.getById(applicationId)));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/detail")
    public ResponseData detailList(@RequestBody Map<String, Object> body, @RequestParam int page, @RequestParam int pageSize) {
        try {
            ApiApplication apiApplication = new ApiApplication();
            BeanUtils.populate(apiApplication, body);
            return new ResponseData(service.getService(apiApplication.getApplicationId(), page, pageSize));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new DatasetException("dataset.error.ApiApplication", e);
        }
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/fetchNotServer")
    public ResponseData fetchNotServer(final HttpServletRequest request, final String exitsCodes, final ApiServer server,
                                       @RequestParam(defaultValue = DEFAULT_PAGE) final int page,
                                       @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) final int pagesize) {
        List<ApiServer> apiServers = service.selectNotExistsServerByApp(exitsCodes, server, page, pagesize);
        return new ResponseData(apiServers);
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/fetchNotServerDS")
    public ResponseData fetchNotServerDS(@RequestBody Map<String, Object> body, int page, int pageSize) {
        List<ApiServer> apiServers = service.selectNotExistsServerByApp(body.get("exitsCodes").toString(), null, page, pageSize);
        return new ResponseData(apiServers);
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/submit")
    public ResponseData update(@RequestBody List<ApiApplication> dto, BindingResult result, HttpServletRequest request) {
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(dto));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/remove")
    public ResponseData remove(HttpServletRequest request, @RequestBody List<ApiApplication> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/generatorClientInfo")
    public Map generatorClientInfo() {
        Map<String, String> clientInfo = new HashMap<>(1);
        clientInfo.put("clientId", UUID.randomUUID().toString().replaceAll("\\-", ""));
        clientInfo.put("clientSecret", UUID.randomUUID().toString());
        return clientInfo;
    }
}