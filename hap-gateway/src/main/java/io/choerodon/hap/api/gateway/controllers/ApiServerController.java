package io.choerodon.hap.api.gateway.controllers;

import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.hap.api.gateway.dto.ApiServer;
import io.choerodon.hap.api.gateway.service.IApiServerService;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.dto.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

/**
 * 服务控制器.
 *
 * @author lijian.yin@hand-china.com
 **/

@Controller
@RequestMapping(value = {"/sys/gateway/server", "/api/sys/gateway/server"})
public class ApiServerController extends BaseController {

    @Autowired
    private IApiServerService service;

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/query")
    @ResponseBody
    public ResponseData query(ApiServer dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        return new ResponseData(service.select(dto, page, pageSize));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/remove")
    @ResponseBody
    public ResponseData remove(HttpServletRequest request, @RequestBody List<ApiServer> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/import")
    @ResponseBody
    public ResponseData importServer(HttpServletRequest request, @RequestBody ApiServer dto) {
        return new ResponseData(Collections.singletonList(service.importServer(dto)));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/getById")
    @ResponseBody
    public ResponseData queryById(HttpServletRequest request, @RequestBody ApiServer dto) {
        return new ResponseData(Collections.singletonList(service.selectByPrimaryKey(dto)));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/submit")
    public ResponseData submit(@RequestBody List<ApiServer> dto, BindingResult result, HttpServletRequest request) {
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(dto));
    }


}