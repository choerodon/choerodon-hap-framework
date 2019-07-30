package io.choerodon.hap.hr.controllers;

import io.choerodon.hap.hr.dto.EmployeeAssign;
import io.choerodon.hap.hr.service.IEmployeeAssignService;
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
 * 对员工岗位分配的操作.
 *
 * @author yuliao.chen@hand-china.com
 */
@RestController
@RequestMapping(value = {"/hr/employee/assign", "/api/hr/employee/assign"})
public class EmployeeAssignController extends BaseController {
    @Autowired
    private IEmployeeAssignService employeeAssignService;

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/query")
    public ResponseData queryByEmployeeId(final Long employeeId, @RequestParam(defaultValue = DEFAULT_PAGE) final int page,
                                          @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) final int pagesize, final HttpServletRequest request) {
        return new ResponseData(employeeAssignService.selectByEmployeeId(employeeId, page, pagesize));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/submit")
    public ResponseData submitAssign(HttpServletRequest request, @RequestBody List<EmployeeAssign> assignList) {
        return new ResponseData(employeeAssignService.batchUpdate(assignList));
    }


}
