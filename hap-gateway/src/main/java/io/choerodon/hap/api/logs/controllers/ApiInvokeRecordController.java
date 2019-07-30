package io.choerodon.hap.api.logs.controllers;

import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.hap.api.logs.dto.ApiInvokeRecord;
import io.choerodon.hap.message.components.InvokeApiManager;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.core.IRequest;
import io.choerodon.web.dto.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * api调用记录.
 *
 * @author peng.jiang@hand-china.com
 * @since 2017/9/23.
 */

@RestController
@RequestMapping(value = {"/sys/logs/invokeRecord", "/api/sys/logs/invokeRecord"})
public class ApiInvokeRecordController extends BaseController {

    @Autowired
    private InvokeApiManager invokeApiManager;

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/query")
    public ResponseData query(@RequestBody ApiInvokeRecord dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(invokeApiManager.getInvokeApiStrategy().queryInvokeRecord(requestContext, dto, dto.getPage(), dto.getPagesize()));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/getById")
    public ResponseData queryById(HttpServletRequest request, @RequestBody ApiInvokeRecord dto) {
        return new ResponseData(invokeApiManager.getInvokeApiStrategy().selectById(dto.getRecordId()));
    }


}