package io.choerodon.hap.security.permission.controllers;

import io.choerodon.hap.security.permission.dto.DataPermissionTableRule;
import io.choerodon.hap.security.permission.service.IDataPermissionTableRuleService;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.core.IRequest;
import io.choerodon.web.dto.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author jialong.zuo@hand-china.com
 * @since 2017/12/8
 */
@RestController
@RequestMapping(value = {"/sys/data/permission/table/rule", "/api/sys/data/permission/table/rule"})
public class DataPermissionTableRuleController extends BaseController {

    @Autowired
    private IDataPermissionTableRuleService service;

    @Permission(type = ResourceType.SITE)
    @RequestMapping(value = "/query")
    public ResponseData query(DataPermissionTableRule dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        return new ResponseData(service.selectOptions(dto, null, page, pageSize));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/submit")
    public ResponseData update(@RequestBody List<DataPermissionTableRule> dto, BindingResult result, HttpServletRequest request) {
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.updateRule(requestCtx, dto));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/remove")
    public ResponseData delete(HttpServletRequest request, @RequestBody List<DataPermissionTableRule> dto) {
        service.removeRule(dto);
        return new ResponseData();
    }
}