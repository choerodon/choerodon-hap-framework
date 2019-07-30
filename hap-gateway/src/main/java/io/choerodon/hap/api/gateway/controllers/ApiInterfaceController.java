package io.choerodon.hap.api.gateway.controllers;

import io.choerodon.hap.api.gateway.dto.ApiInterface;
import io.choerodon.hap.api.gateway.service.IApiInterfaceService;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.core.IRequest;
import io.choerodon.web.dto.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 接口控制器.
 *
 * @author lijian.yin@hand-china.com
 **/

@RestController
@RequestMapping(value = {"/sys/gateway/interface", "/api/sys/gateway/interface"})
public class ApiInterfaceController extends BaseController {

    @Autowired
    private IApiInterfaceService service;

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/remove")
    public ResponseData remove(HttpServletRequest request, @RequestBody List<ApiInterface> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/getInterfacesByServerId")
    public ResponseData queryInterfacesByServerId(ApiInterface dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectByServerId(requestContext, dto));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/getInterfacesByServerCode/{clientId}/{serverId}")
    public ResponseData getInterfacesByServerCode(@PathVariable String clientId,
                                                  @PathVariable Long serverId,
                                                  HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectByServerIdWithLimit(requestContext, clientId, serverId));
    }

}