package io.choerodon.hap.mail.controllers;

import io.choerodon.hap.core.exception.EmailException;
import io.choerodon.hap.mail.dto.MessageEmailConfig;
import io.choerodon.hap.mail.service.IMessageEmailConfigService;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.base.exception.BaseException;
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
 * 对邮件配置的操作.
 *
 * @author Clerifen Li
 */
@RestController
@RequestMapping(value = {"/sys/messageEmailConfig", "/api/sys/messageEmailConfig"})
public class MessageEmailConfigController extends BaseController {

    @Autowired
    private IMessageEmailConfigService service;

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/query")
    public ResponseData getMessageEmailConfig(HttpServletRequest request, MessageEmailConfig example,
                                              @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectMessageEmailConfigs(requestContext, example, page, pagesize));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/submit")
    public ResponseData submitLov(@RequestBody MessageEmailConfig obj, BindingResult result, HttpServletRequest request) throws EmailException {

        getValidator().validate(obj, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestContext = createRequestContext(request);
        service.batchUpdate(requestContext, obj);
        return new ResponseData();
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/remove")
    public ResponseData deleteMessageEmailConfig(HttpServletRequest request, @RequestBody List<MessageEmailConfig> objs) throws BaseException {
        IRequest requestContext = createRequestContext(request);
        service.batchDelete(requestContext, objs);
        return new ResponseData();
    }

}
