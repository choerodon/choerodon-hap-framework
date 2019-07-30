package io.choerodon.hap.mail.controllers;

import io.choerodon.hap.mail.dto.MessageEmailProperty;
import io.choerodon.hap.mail.service.IMessageEmailPropertyService;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.web.controller.BaseController;
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
 * 对邮件服务器属性的操作.
 *
 * @author qiang.zeng@hand-china.com
 */
@RestController
@RequestMapping(value = {"/sys/message/email/property", "/api/sys/message/email/property"})
public class MessageEmailPropertyController extends BaseController {

    @Autowired
    private IMessageEmailPropertyService service;

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "query")
    public ResponseData query(MessageEmailProperty dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        return new ResponseData(service.select(dto, page, pageSize));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "remove")
    public ResponseData delete(HttpServletRequest request, @RequestBody List<MessageEmailProperty> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }
}