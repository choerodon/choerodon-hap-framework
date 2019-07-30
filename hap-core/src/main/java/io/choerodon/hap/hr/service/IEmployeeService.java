package io.choerodon.hap.hr.service;

import io.choerodon.hap.account.dto.User;
import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.hr.dto.Employee;
import io.choerodon.hap.hr.dto.UserAndRoles;
import io.choerodon.mybatis.service.IBaseService;

import java.util.List;

/**
 * 员工服务接口.
 *
 * @author yuliao.chen@hand-china.com
 */
public interface IEmployeeService extends IBaseService<Employee>, ProxySelf<IEmployeeService> {
    /**
     * 条件查询员工.
     *
     * @param employee 员工
     * @param page     页码
     * @param pageSize 页数
     * @return 员工列表
     */
    List<Employee> queryAll(Employee employee, int page, int pageSize);

    /**
     * 通过员工创建用户.
     *
     * @param roles 用户角色
     */
    void createUserByEmployee(UserAndRoles roles);

    /**
     * 通过员工编码查找上级主管.
     *
     * @param employeeCode 员工编码
     * @return 员工列表
     */
    List<Employee> getDirector(String employeeCode);

    /**
     * 通过员工编码查找部门领导.
     *
     * @param employeeCode 员工编码
     * @return 员工列表
     */
    List<Employee> getDeptDirector(String employeeCode);

    /**
     * 添加为用户功能更新用户角色.
     *
     * @param userAndRoles 用户角色
     * @param userName     用户名
     */
    void updateUser(UserAndRoles userAndRoles, String userName);

    /**
     * 员工批量更新.
     *
     * @param list 员工列表
     * @return 员工列表
     */
    List<Employee> submit(List<Employee> list);

    /**
     * 通过员工编码查询员工信息.
     *
     * @param employeeCode 员工编码
     * @return 员工
     */
    Employee queryInfoByCode(String employeeCode);

    /**
     * 通过岗位编码查询员工.
     *
     * @param positionCode 岗位编码
     * @return 员工列表
     */
    List<Employee> selectByPostionCode(String positionCode);

    /**
     * 添加为用户功能 查询已经创建过的用户信息.
     *
     * @param user 用户
     * @return 用户信息
     */
    List<User> selectExistingUser(User user);

    /**
     * 添加为用户功能 创建用户以及用户角色.
     *
     * @param user 用户信息
     */
    void createUserByEmployee(User user);

    /**
     * 添加为用户功能 更新用户角色.
     *
     * @param user 用户信息
     */
    void updateUserByEmployee(User user);
}
