package io.choerodon.hap.mail.controllers;

import io.choerodon.hap.mail.dto.MessageTemplate;
import io.choerodon.hap.mail.service.IMessageTemplateService;
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
 * 对消息模板的操作.
 *
 * @author Clerifen Li
 */
@RestController
@RequestMapping(value = {"/sys/messageTemplate", "/api/sys/messageTemplate"})
public class MessageTemplateController extends BaseController {

    @Autowired
    private IMessageTemplateService service;

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/query")
    public ResponseData getMessageTemplate(HttpServletRequest request, MessageTemplate example,
                                           @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                           @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectMessageTemplates(requestContext, example, page, pagesize));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/add")
    public ResponseData addMessageTemplate(HttpServletRequest request, @RequestBody MessageTemplate obj, BindingResult result) throws BaseException {

        getValidator().validate(obj, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestContext = createRequestContext(request);
        service.createMessageTemplate(requestContext, obj);
        return new ResponseData();
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/update")
    public ResponseData updateMessageTemplate(HttpServletRequest request, @RequestBody MessageTemplate obj, BindingResult result) throws BaseException {

        getValidator().validate(obj, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestContext = createRequestContext(request);
        service.updateMessageTemplate(requestContext, obj);
        return new ResponseData();
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/remove")
    public ResponseData deleteMessageTemplate(HttpServletRequest request, @RequestBody List<MessageTemplate> objs) throws BaseException {
        IRequest requestContext = createRequestContext(request);
        service.batchDelete(requestContext, objs);
        return new ResponseData();
    }

}
