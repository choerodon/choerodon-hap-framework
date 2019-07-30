package io.choerodon.hap.mail.controllers;

import io.choerodon.hap.mail.dto.MessageAccount;
import io.choerodon.hap.mail.service.IMessageAccountService;
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
 * 对消息账号的操作.
 *
 * @author Clerifen Li
 */
@RestController
@RequestMapping(value = {"/sys/messageAccount", "/api/sys/messageAccount"})
public class MessageAccountController extends BaseController {

    @Autowired
    private IMessageAccountService service;

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/query")
    public ResponseData getMessageAccount(HttpServletRequest request, MessageAccount example,
                                          @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                          @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectMessageAccounts(requestContext, example, page, pagesize));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/queryAccount")
    public ResponseData getMessageAccountPassword(HttpServletRequest request, MessageAccount example) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectMessageAccountPassword(requestContext, example, 1, 1));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/add")
    public ResponseData addMessageAccount(HttpServletRequest request, @RequestBody MessageAccount obj, BindingResult result) throws BaseException {
        obj.setObjectVersionNumber(0L);

        getValidator().validate(obj, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestContext = createRequestContext(request);
        service.createMessageAccount(requestContext, obj);
        return new ResponseData();
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/update")
    public ResponseData updateMessageAccount(HttpServletRequest request, @RequestBody MessageAccount obj, BindingResult result) throws BaseException {
        obj.setObjectVersionNumber(0L);

        getValidator().validate(obj, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestContext = createRequestContext(request);
        service.updateMessageAccount(requestContext, obj);
        return new ResponseData();
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/updatePasswordOnly")
    public ResponseData updateMessageAccountPasswordOnly(HttpServletRequest request, @RequestBody MessageAccount obj, BindingResult result) throws BaseException {
        obj.setObjectVersionNumber(0L);

        getValidator().validate(obj, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestContext = createRequestContext(request);
        service.updateMessageAccountPasswordOnly(requestContext, obj);
        return new ResponseData();
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/remove")
    public ResponseData deleteMessageAccount(HttpServletRequest request, @RequestBody List<MessageAccount> objs) throws BaseException {
        IRequest requestContext = createRequestContext(request);
        service.batchDelete(requestContext, objs);
        return new ResponseData();
    }

}
