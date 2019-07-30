package io.choerodon.hap.security.permission.controllers;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.hap.security.permission.dto.DataPermissionTable;
import io.choerodon.hap.security.permission.service.IDataPermissionTableService;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.dto.ResponseData;

/**
 * @author jialong.zuo@hand-china.com
 * @since 2017/12/8
 */
@RestController
@RequestMapping(value = {"/sys/data/permission/table", "/api/sys/data/permission/table"})
public class DataPermissionTableController extends BaseController {

    @Autowired
    private IDataPermissionTableService service;

    @Permission(type = ResourceType.SITE)
    @RequestMapping(value = "/query")
    public ResponseData query(DataPermissionTable dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        return new ResponseData(service.select(dto, page, pageSize));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/submit")
    public ResponseData update(@RequestBody List<DataPermissionTable> dto, BindingResult result, HttpServletRequest request) {
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
    public ResponseData delete(HttpServletRequest request, @RequestBody List<DataPermissionTable> dto) {
        service.removeTableWithRule(dto);
        return new ResponseData();
    }
}