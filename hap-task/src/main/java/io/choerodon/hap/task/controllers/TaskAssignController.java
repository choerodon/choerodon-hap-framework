package io.choerodon.hap.task.controllers;

import io.choerodon.hap.task.dto.TaskAssign;
import io.choerodon.hap.task.service.ITaskAssignService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 任务权限控制器.
 *
 * @author lijian.yin@hand-china.com
 * @since 2017/11/15.
 **/

@RestController
@RequestMapping({"/sys/task/assign", "/api/sys/task/assign"})
public class TaskAssignController extends BaseController {

    @Autowired
    private ITaskAssignService service;

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/query")
    public ResponseData query(TaskAssign dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.query(requestContext, dto));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/submit")
    public ResponseData update(@RequestBody List<TaskAssign> dto, BindingResult result, HttpServletRequest request) {
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
    public ResponseData remove(HttpServletRequest request, @RequestBody List<TaskAssign> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/selectUnbound")
    public ResponseData queryUnbound(@RequestParam(required = false) String ids, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        List<String> idList = new ArrayList<>();
        if (ids != null && !ids.isEmpty()) {
            idList = Arrays.asList(ids.split(","));
        }
        return new ResponseData(service.queryUnbound(iRequest, idList));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/selectUnbound/v2")
    public ResponseData queryUnbound2(@RequestBody(required = false) Map<String, String> ids, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        List<String> idList = new ArrayList<>();
        String roleIds = ids.get("ids");
        if (roleIds != null && !roleIds.isEmpty()) {
            idList = Arrays.asList(roleIds.split(","));
        }
        return new ResponseData(service.queryUnbound(iRequest, idList));
    }
}