package io.choerodon.hap.intergration.controllers;

import io.choerodon.hap.intergration.dto.HapInterfaceInbound;
import io.choerodon.hap.intergration.dto.HapInterfaceOutbound;
import io.choerodon.hap.message.components.InvokeLogManager;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.core.IRequest;
import io.choerodon.web.dto.ResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;


/**
 * @author xiangyu.qi@hand-china.com
 * @since 2016/11/22
 */

@Controller
@RequestMapping(value = {"/sys/invoke", "/api/sys/invoke"})
public class HapInterfaceInvokeController extends BaseController {

    private final Logger logger = LoggerFactory.getLogger(HapInterfaceInvokeController.class);

    @Autowired
    private InvokeLogManager logManager;

    @Permission(type = ResourceType.SITE)
    @RequestMapping(value = "/querryInbound", produces = "application/json")
    @ResponseBody
    public ResponseData queryInbound(@RequestBody HapInterfaceInbound inbound, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(logManager.getInvokeLogStrategy().queryInbound(requestContext, inbound, inbound.getPage(), inbound.getPagesize()));
    }

    @Permission(type = ResourceType.SITE)
    @RequestMapping(value = "/querryOutbound")
    @ResponseBody
    public ResponseData queryOutbound(@RequestBody HapInterfaceOutbound outbound, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(logManager.getInvokeLogStrategy().queryOutbound(requestContext, outbound, outbound.getPage(), outbound.getPagesize()));
    }

}
