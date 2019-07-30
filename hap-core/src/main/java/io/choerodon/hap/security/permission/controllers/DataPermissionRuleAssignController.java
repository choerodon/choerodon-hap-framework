package io.choerodon.hap.security.permission.controllers;

import io.choerodon.hap.security.permission.dto.DataPermissionRuleAssign;
import io.choerodon.hap.security.permission.service.IDataPermissionRuleAssignService;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author jialong.zuo@hand-china.com
 * @since 2017/12/8
 */
@RestController
@RequestMapping(value = {"/sys/data/permission/rule/assign", "/api/sys/data/permission/rule/assign"})
public class DataPermissionRuleAssignController extends BaseController {

    @Autowired
    private IDataPermissionRuleAssignService service;

    @Permission(type = ResourceType.SITE)
    @RequestMapping(value = "/query", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseData query(DataPermissionRuleAssign dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) throws IllegalAccessException {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectRuleAssign(dto, page, pageSize, requestContext));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/submit")
    public ResponseData update(@RequestBody List<DataPermissionRuleAssign> dto, BindingResult result, HttpServletRequest request) {
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.updateDataMaskRuleAssign(requestCtx, dto));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/remove")
    public ResponseData delete(HttpServletRequest request, @RequestBody List<DataPermissionRuleAssign> dto) {
        service.removeDataMaskRuleAssign(dto);
        return new ResponseData();
    }
}