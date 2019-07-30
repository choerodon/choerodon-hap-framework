package io.choerodon.hap.hr.controllers;

import io.choerodon.hap.account.dto.User;
import io.choerodon.hap.hr.dto.Employee;
import io.choerodon.hap.hr.dto.UserAndRoles;
import io.choerodon.hap.hr.service.IEmployeeService;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.mybatis.common.query.Comparison;
import io.choerodon.mybatis.common.query.WhereField;
import io.choerodon.mybatis.entity.Criteria;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.dto.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * 对员工的操作.
 *
 * @author yuliao.chen@hand-china.com
 */
@RestController
@RequestMapping(value = {"/hr/employee", "/api/hr/employee"})
public class EmployeeController extends BaseController {

    @Autowired
    private IEmployeeService employeeService;

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/query")
    public ResponseData query(final Employee employee, @RequestParam(defaultValue = DEFAULT_PAGE) final int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) final int pagesize, final HttpServletRequest request) {
        Criteria criteria = new Criteria(employee);
        criteria.where(new WhereField(Employee.FIELD_EMPLOYEE_CODE, Comparison.LIKE), Employee.FIELD_EMPLOYEE_ID, Employee.FIELD_NAME);
        return new ResponseData(employeeService.selectOptions(employee, criteria, page, pagesize));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/queryAll")
    public ResponseData queryAll(final Employee employee, @RequestParam(defaultValue = DEFAULT_PAGE) final int page,
                                 @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) final int pagesize, final HttpServletRequest request) {
        return new ResponseData(employeeService.queryAll(employee, page, pagesize));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/queryAllUP")
    public ResponseData queryAllUP(@RequestBody final Employee employee, @RequestParam(defaultValue = DEFAULT_PAGE) final int page,
                                   @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) final int pagesize, final HttpServletRequest request) {
        return new ResponseData(employeeService.queryAll(employee, page, pagesize));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/submit")
    public ResponseData submit(@RequestBody final List<Employee> employees, final BindingResult result,
                               final HttpServletRequest request) {
        getValidator().validate(employees, result);
        if (result.hasErrors()) {
            ResponseData rd = new ResponseData(false);
            rd.setMessage(getErrorMessage(result, request));
            return rd;
        }
        return new ResponseData(employeeService.submit(employees));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/create_user")
    public ResponseData createUserByEmployee(@RequestBody UserAndRoles userAndRoles, HttpServletRequest request) {
        employeeService.createUserByEmployee(userAndRoles);
        return new ResponseData();
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/update_user")
    public ResponseData updateUser(@RequestBody UserAndRoles userAndRoles, HttpServletRequest request, @RequestParam(required = false) String userName) {
        employeeService.updateUser(userAndRoles, userName);
        return new ResponseData();
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/queryExistingUser")
    public ResponseData queryUsers(HttpServletRequest request, @ModelAttribute User user) {
        return new ResponseData(employeeService.selectExistingUser(user));
    }

}
