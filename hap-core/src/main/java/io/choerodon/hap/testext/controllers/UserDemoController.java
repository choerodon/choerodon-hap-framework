package io.choerodon.hap.testext.controllers;

import io.choerodon.hap.testext.dto.UserDemo;
import io.choerodon.hap.testext.service.IUserDemoService;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.dto.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class UserDemoController extends BaseController {

    @Autowired
    private IUserDemoService service;

    @Permission(type = ResourceType.SITE)
    @RequestMapping(value = "/sys/demo/query")
    public ResponseData query(UserDemo dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        List<UserDemo> list = service.select(dto, page, pageSize);
        return new ResponseData(list);
    }

    @Permission(type = ResourceType.SITE)
    @RequestMapping(value = "/sys/demo/submit")
    public ResponseData update(HttpServletRequest request, @RequestBody List<UserDemo> dto) {
        return new ResponseData(service.batchUpdate(dto));
    }

    @Permission(type = ResourceType.SITE)
    @RequestMapping(value = "/sys/demo/remove")
    public ResponseData delete(HttpServletRequest request, @RequestBody List<UserDemo> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }
}