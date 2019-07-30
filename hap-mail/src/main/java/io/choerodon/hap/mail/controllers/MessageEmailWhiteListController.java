package io.choerodon.hap.mail.controllers;

import io.choerodon.hap.mail.dto.MessageEmailWhiteList;
import io.choerodon.hap.mail.service.IMessageEmailWhiteListService;
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
 * 对邮件白名单的操作.
 *
 * @author Clerifen Li
 */
@RestController
@RequestMapping(value = {"/sys/messageEmailWhiteList", "/api/sys/messageEmailWhiteList"})
public class MessageEmailWhiteListController extends BaseController {

    @Autowired
    private IMessageEmailWhiteListService service;

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/query")
    public ResponseData getMessageEmailWhiteList(HttpServletRequest request, MessageEmailWhiteList example,
                                                 @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                 @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectMessageEmailWhiteLists(requestContext, example, page, pagesize));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/remove")
    public ResponseData deleteMessageEmailWhiteList(HttpServletRequest request, @RequestBody List<MessageEmailWhiteList> objs) throws BaseException {
        IRequest requestContext = createRequestContext(request);
        service.batchDelete(requestContext, objs);
        return new ResponseData();
    }

}
