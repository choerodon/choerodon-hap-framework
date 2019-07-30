package io.choerodon.hap.api.application.controllers;

import io.choerodon.hap.api.application.dto.ApiAccessLimit;
import io.choerodon.hap.api.application.service.IApiAccessLimitService;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.dto.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 访问限制控制器.
 *
 * @author lijian.yin@hand-china.com
 * @since 2017/11/15.
 **/

@RestController
@RequestMapping(value = {"/sys/application/accessLimit", "/api/sys/application/accessLimit"})
public class ApiAccessLimitController extends BaseController {

    @Autowired
    private IApiAccessLimitService apiAccessLimitService;

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/submit")
    public ResponseData submit(@RequestBody List<ApiAccessLimit> apiAccessLimiteList, HttpServletRequest request) {
        apiAccessLimitService.batchUpdate(apiAccessLimiteList);
        return new ResponseData(apiAccessLimiteList);
    }
}