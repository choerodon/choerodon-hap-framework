package io.choerodon.hap.flexfield.controllers;

import io.choerodon.hap.flexfield.dto.FlexRule;
import io.choerodon.hap.flexfield.dto.WarpFlexRuleField;
import io.choerodon.hap.flexfield.service.IFlexRuleService;
import io.choerodon.hap.flexfield.service.impl.FlexRuleServiceImpl;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.core.IRequest;
import io.choerodon.web.dto.ResponseData;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping(value = {"/fnd/flex/rule", "/api/fnd/flex/rule"})
public class FlexRuleController extends BaseController {

    @Autowired
    private IFlexRuleService service;

    @Autowired
    private MessageSource messageSource;

    @Permission(type = ResourceType.SITE)
    @RequestMapping(value = "/query", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseData query(FlexRule dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {

        return new ResponseData(service.select(dto, page, pageSize));
    }

    @Permission(type = ResourceType.SITE)
    @RequestMapping(value = "/matching", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseData matchingRule(String ruleSetCode, @RequestBody JSONObject viewModel, HttpServletRequest request) {
        Set<Map.Entry<String, String>> entrySet = viewModel.entrySet();
        IRequest requestContext = createRequestContext(request);
        ResponseData responseData = service.matchingRule(ruleSetCode, entrySet, requestContext);
        if (null == responseData.getRows()) {
            return responseData;
        }

        List<WarpFlexRuleField> warpFlexRuleFields = (List<WarpFlexRuleField>) responseData.getRows();
        Locale locale = RequestContextUtils.getLocale(request);

        warpFlexRuleFields.forEach(v -> {
            FlexRuleServiceImpl.setPrompt(v, locale, messageSource);
        });
        return responseData;
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/submit")
    public ResponseData update(@RequestBody List<FlexRule> dto, HttpServletRequest request, BindingResult result) {
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
    public ResponseData delete(HttpServletRequest request, @RequestBody List<FlexRule> dto) {
        service.deleteRule(dto);
        return new ResponseData();
    }
}