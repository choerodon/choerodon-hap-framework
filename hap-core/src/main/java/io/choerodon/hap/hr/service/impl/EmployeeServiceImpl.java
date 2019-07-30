package io.choerodon.hap.hr.service.impl;

import com.github.pagehelper.PageHelper;
import io.choerodon.hap.account.dto.User;
import io.choerodon.hap.account.dto.UserRole;
import io.choerodon.hap.account.mapper.UserRoleMapper;
import io.choerodon.hap.account.service.IUserRoleService;
import io.choerodon.hap.account.service.IUserService;
import io.choerodon.hap.cache.impl.UserCache;
import io.choerodon.hap.hr.dto.Employee;
import io.choerodon.hap.hr.dto.UserAndRoles;
import io.choerodon.hap.hr.mapper.EmployeeAssignMapper;
import io.choerodon.hap.hr.mapper.EmployeeMapper;
import io.choerodon.hap.hr.service.IEmployeeService;
import io.choerodon.hap.iam.app.service.RoleMemberService;
import io.choerodon.hap.iam.infra.dto.MemberRoleDTO;
import io.choerodon.hap.iam.infra.dto.RoleDTO;
import io.choerodon.hap.iam.infra.enums.MemberType;
import io.choerodon.hap.system.dto.DTOStatus;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.message.IMessagePublisher;
import io.choerodon.mybatis.common.query.Comparison;
import io.choerodon.mybatis.common.query.WhereField;
import io.choerodon.mybatis.entity.Criteria;
import io.choerodon.mybatis.service.BaseServiceImpl;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.util.StringUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * 员工服务接口实现.
 *
 * @author yuliao.chen@hand-china.com
 */
@Service
@Dataset("Employee")
public class EmployeeServiceImpl extends BaseServiceImpl<Employee> implements IEmployeeService, IDatasetService<Employee> {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private IMessagePublisher messagePublisher;

    @Autowired
    private IUserRoleService userRoleService;

    @Autowired
    private IUserService userService;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private EmployeeAssignMapper employeeAssignMapper;

    @Autowired
    private RoleMemberService roleMemberService;

    @Autowired
    private UserCache userCache;

    @Override
    public List<Employee> submit(List<Employee> list) {
        self().batchUpdate(list);
        for (Employee e : list) {
            messagePublisher.publish("employee.change", e);
        }
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Employee> batchUpdate(List<Employee> list) {
        Criteria criteria = new Criteria();
        criteria.update(Employee.FIELD_BORN_DATE, Employee.FIELD_CERTIFICATE_ID, Employee.FIELD_CERTIFICATE_TYPE,
                Employee.FIELD_NAME, Employee.FIELD_EFFECTIVE_END_DATE, Employee.FIELD_EFFECTIVE_START_DATE,
                Employee.FIELD_EMAIL, Employee.FIELD_ENABLED_FLAG, Employee.FIELD_GENDER, Employee.FIELD_JOIN_DATE,
                Employee.FIELD_MOBIL, Employee.FIELD_STATUS);
        criteria.updateExtensionAttribute();
        for (Employee employee : list) {
            switch (employee.get__status()) {
                case DTOStatus.ADD:
                    self().insertSelective(employee);
                    break;
                case DTOStatus.UPDATE:
                    self().updateByPrimaryKeyOptions(employee, criteria);
                    break;
                case DTOStatus.DELETE:
                    self().deleteByPrimaryKey(employee);
                    break;
                default:
                    break;
            }
        }
        return list;
    }

    @Override
    public List<Employee> queryAll(Employee employee, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return employeeMapper.queryAll(employee);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createUserByEmployee(UserAndRoles roles) {
        User u = userService.insertSelective(roles.getUser());
        if (CollectionUtils.isNotEmpty(roles.getRoles())) {
            Long userId = u.getUserId();
            List<UserRole> roles1 = roles.getRoles();
            for (UserRole role : roles1) {
                role.setUserId(userId);
                userRoleService.insertSelective(role);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(UserAndRoles userAndRoles, String userName) {
        if (userAndRoles.getRoles() != null) {
            List<UserRole> newRoles = userAndRoles.getRoles();
            User user = userAndRoles.getUser();
            userRoleMapper.deleteByUserId(user.getUserId());
            for (UserRole role : newRoles) {
                role.setUserId(user.getUserId());
                userRoleService.insertSelective(role);
            }
            userCache.remove(userName.toLowerCase());
        }
    }

    @Override
    public Employee queryInfoByCode(String employeeCode) {
        return employeeMapper.queryInfoByCode(employeeCode);
    }

    @Override
    public List<Employee> selectByPostionCode(String code) {
        return employeeMapper.selectByPostionCode(code);
    }

    @Override
    public List<User> selectExistingUser(User user) {
        if (user != null && StringUtil.isNotEmpty(user.getUserName())) {
            user.setUserName(user.getUserName().toLowerCase());
        }
        Criteria criteria = new Criteria(user);
        criteria.where(User.FIELD_USER_NAME);
        criteria.select(User.FIELD_USER_ID, User.FIELD_USER_NAME, User.FIELD_EMPLOYEE_ID,
                User.FIELD_EMPLOYEE_NAME, User.FIELD_EMPLOYEE_CODE, User.FIELD_EMAIL, User.FIELD_PHONE,
                User.FIELD_STATUS, User.FIELD_START_ACTIVE_DATE, User.FIELD_END_ACTIVE_DATE, User.FIELD_DESCRIPTION);
        return userService.selectOptions(user, criteria);
    }

    @Override
    public void createUserByEmployee(User user) {
        user = userService.insertSelective(user);
        if (user != null && CollectionUtils.isNotEmpty(user.getUserRoles())) {
            insertUserRoles(user);
        }
    }

    @Override
    public void updateUserByEmployee(User user) {
        if (CollectionUtils.isNotEmpty(user.getUserRoles())) {
            insertUserRoles(user);
        }
    }

    @Override
    public List<Employee> getDirector(String employeeCode) {
        return employeeMapper.getDirector(employeeCode);
    }

    @Override
    public List<Employee> getDeptDirector(String employeeCode) {
        return employeeMapper.getDeptDirector(employeeCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByPrimaryKey(Employee employee) {
        int result = super.deleteByPrimaryKey(employee);
        employeeAssignMapper.deleteByEmployeeId(employee.getEmployeeId());
        return result;
    }

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            Employee employee = new Employee();
            BeanUtils.populate(employee, body);
            if (sortname == null) {
                employee.setSortname("employeeId");
                employee.setSortorder("desc");
            } else {
                employee.setSortname(sortname);
                employee.setSortorder(isDesc ? "desc" : "asc");
            }
            Criteria criteria = new Criteria(employee);
            criteria.where(new WhereField(Employee.FIELD_EMPLOYEE_CODE, Comparison.LIKE), Employee.FIELD_EMPLOYEE_ID, Employee.FIELD_NAME);
            return self().selectOptions(employee, criteria, page, pageSize);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new DatasetException("dataset.error", e);
        }
    }

    @Override
    public List<Employee> mutations(List<Employee> objs) {
        return self().submit(objs);
    }

    private void insertUserRoles(User user) {
        Long userId = user.getUserId();
        for (RoleDTO role : user.getUserRoles()) {
            MemberRoleDTO memberRole = new MemberRoleDTO();
            memberRole.setMemberId(userId);
            memberRole.setRoleId(role.getId());
            memberRole.setMemberType(MemberType.USER.value());
            memberRole.setSourceId(0L);
            memberRole.setSourceType("site");
            roleMemberService.insertSelective(memberRole);
        }
    }
}
