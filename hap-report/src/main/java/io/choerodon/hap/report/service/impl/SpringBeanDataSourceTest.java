package io.choerodon.hap.report.service.impl;

import com.github.pagehelper.util.StringUtil;
import io.choerodon.hap.account.dto.User;
import io.choerodon.hap.account.service.IUserService;
import io.choerodon.hap.hr.dto.Employee;
import io.choerodon.hap.hr.service.IEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 报表 SpringBean 类型数据源.
 *
 * @author qiang.zeng@hand-china.com
 * @since 2018/1/17
 */
@Component
public class SpringBeanDataSourceTest {
    @Autowired
    private IUserService userService;

    @Autowired
    private IEmployeeService employeeService;

    public List<User> loadUserData(String dsName, String datasetName, Map<String, Object> parameters) {
        Object s = parameters.get("userId");
        List<User> userList = new ArrayList<>();
        if (s != null) {
            User user = new User();
            if (StringUtil.isNotEmpty((String) s)) {
                user.setUserId(Long.parseLong((String) s));
                userList.add(userService.selectByPrimaryKey(user));
            } else {
                userList.addAll(userService.selectAll());
            }
        }
        return userList;
    }


    public List<Employee> loadEmployeeData(String dsName, String datasetName, Map<String, Object> parameters) {
        Object s = parameters.get("employeeId");
        List<Employee> employeeList = new ArrayList<>();
        if (s != null) {
            Employee employee = new Employee();
            if (StringUtil.isNotEmpty((String) s)) {
                employee.setEmployeeId(Long.parseLong((String) s));
                employeeList.add(employeeService.selectByPrimaryKey(employee));
            } else {
                employeeList.addAll(employeeService.selectAll());
            }
        }
        return employeeList;
    }


    public List<Map<String, String>> loadData(String dsName, String datasetName, Map<String, Object> parameters) {
        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String> m = new HashMap<>();
        m.put("field1", "m1");
        Map<String, String> m2 = new HashMap<>();
        m2.put("field1", "m2");
        m2.put("field2", "m22");
        list.add(m2);
        list.add(m);
        return list;
    }
}