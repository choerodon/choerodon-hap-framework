package io.choerodon.hap.security.oauth.controllers;

import io.choerodon.hap.security.oauth.dto.Oauth2ClientDetails;
import io.choerodon.hap.security.oauth.service.IOauth2ClientDetailsService;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.dto.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qixiangyu
 */
@RestController
@RequestMapping(value = "/sys/client/details")
public class Oauth2ClientDetailsController extends BaseController {

    @Autowired
    private IOauth2ClientDetailsService service;

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/query")
    public ResponseData query(Oauth2ClientDetails dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        return new ResponseData(service.selectOptions(dto, null, page, pageSize));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/submit")
    public ResponseData update(HttpServletRequest request, @RequestBody List<Oauth2ClientDetails> dto, BindingResult result) {
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData rd = new ResponseData(false);
            rd.setMessage(getErrorMessage(result, request));
            return rd;
        }
        return new ResponseData(service.batchUpdate(dto));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/remove")
    public ResponseData delete(HttpServletRequest request, @RequestBody List<Oauth2ClientDetails> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/updatePassword")
    public ResponseData updatePassword(HttpServletRequest request, Long id) {
        String pwd = service.updatePassword(id);
        Map<String, String> result = new HashMap<>(1);
        result.put("clientSecret", pwd);
        return new ResponseData(Collections.singletonList(result));
    }
}