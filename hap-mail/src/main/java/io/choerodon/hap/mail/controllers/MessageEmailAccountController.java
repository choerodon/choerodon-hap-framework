package io.choerodon.hap.mail.controllers;

import io.choerodon.hap.mail.dto.MessageEmailAccount;
import io.choerodon.hap.mail.service.IMessageEmailAccountService;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.base.exception.BaseException;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.core.IRequest;
import io.choerodon.web.dto.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 对邮件账号的操作.
 *
 * @author Clerifen Li
 */
@RestController
@RequestMapping(value = {"/sys/messageEmailAccount", "/api/sys/messageEmailAccount"})
public class MessageEmailAccountController extends BaseController {

    @Autowired
    private IMessageEmailAccountService service;

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/query")
    public ResponseData getMessageEmailAccount(HttpServletRequest request, MessageEmailAccount example,
                                               @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                               @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectMessageEmailAccounts(requestContext, example, page, pagesize));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/queryAccount")
    public ResponseData getMessageEmailAccountPassword(HttpServletRequest request, MessageEmailAccount example) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectMessageEmailAccountWithPassword(requestContext, example, 1, 1));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/remove")
    public ResponseData deleteMessageEmailAccount(HttpServletRequest request, @RequestBody List<MessageEmailAccount> objs) throws BaseException {
        IRequest requestContext = createRequestContext(request);
        service.batchDelete(requestContext, objs);
        return new ResponseData();
    }

}
