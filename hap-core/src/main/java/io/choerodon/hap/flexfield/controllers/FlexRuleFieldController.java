package io.choerodon.hap.flexfield.controllers;

import io.choerodon.hap.flexfield.dto.FlexRuleField;
import io.choerodon.hap.flexfield.service.IFlexRuleFieldService;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author jialongzuo@hand-china.com on 2017-12-8
 */
@RestController
@RequestMapping(value = {"/fnd/flex/rule/field", "/api/fnd/flex/rule/field"})
public class FlexRuleFieldController extends BaseController {

    @Autowired
    private IFlexRuleFieldService service;

    @Permission(type = ResourceType.SITE)
    @RequestMapping(value = "/query", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResponseData query(FlexRuleField dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryFlexRuleField(dto));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/submit")
    public ResponseData update(@RequestBody List<FlexRuleField> dto, HttpServletRequest request, BindingResult result) {
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
    public ResponseData delete(HttpServletRequest request, @RequestBody List<FlexRuleField> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }
}