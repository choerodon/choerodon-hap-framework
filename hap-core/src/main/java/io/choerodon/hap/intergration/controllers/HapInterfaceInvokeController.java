package io.choerodon.hap.intergration.controllers;

import io.choerodon.hap.intergration.dto.HapInterfaceInbound;
import io.choerodon.hap.intergration.dto.HapInterfaceOutbound;
import io.choerodon.hap.intergration.service.IHapInterfaceInboundService;
import io.choerodon.hap.intergration.service.IHapInterfaceOutboundService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private IHapInterfaceInboundService inboundService;

    @Autowired
    private IHapInterfaceOutboundService outboundService;

    @ResponseBody
    @Permission(type = ResourceType.SITE)
    @GetMapping(value = "/querryInbound/{inboundId}")
    public HapInterfaceInbound queryInbound(@PathVariable Long inboundId) {
        HapInterfaceInbound inbound = new HapInterfaceInbound();
        inbound.setInboundId(inboundId);
        return inboundService.selectByPrimaryKey(inbound);
    }

    @ResponseBody
    @Permission(type = ResourceType.SITE)
    @GetMapping(value = "/querryOutbound/{outboundId}")
    public HapInterfaceOutbound queryOutbound(@PathVariable Long outboundId) {
        HapInterfaceOutbound outbound = new HapInterfaceOutbound();
        outbound.setOutboundId(outboundId);
        return outboundService.selectByPrimaryKey(outbound);
    }
}
