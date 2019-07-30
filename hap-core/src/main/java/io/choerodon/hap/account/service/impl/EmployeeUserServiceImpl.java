package io.choerodon.hap.account.service.impl;

import io.choerodon.hap.account.dto.User;
import io.choerodon.hap.hr.service.IEmployeeService;
import io.choerodon.hap.system.dto.DTOStatus;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.mybatis.service.BaseServiceImpl;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * @author vista
 * @time 18-11-29 下午2:44
 */
@Service
@Dataset("EmployeeUser")
public class EmployeeUserServiceImpl extends BaseServiceImpl<User> implements IDatasetService<User> {

    @Autowired
    private IEmployeeService employeeService;

    @Override
    public List<User> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            User user = new User();
            BeanUtils.populate(user, body);
            return employeeService.selectExistingUser(user);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new DatasetException("dataset.error", e);
        }
    }

    @Override
    public List<User> mutations(List<User> objs) {
        for (User user : objs) {
            switch (user.get__status()) {
                case DTOStatus.ADD:
                    employeeService.createUserByEmployee(user);
                    break;
                default:
                    employeeService.updateUserByEmployee(user);
                    break;
            }
        }
        return objs;
    }

}
