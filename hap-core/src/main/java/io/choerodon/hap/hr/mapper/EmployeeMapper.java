package io.choerodon.hap.hr.mapper;

import io.choerodon.hap.hr.dto.Employee;
import io.choerodon.mybatis.common.Mapper;

import java.util.List;

/**
 * 员工Mapper.
 *
 * @author yuliao.chen@hand-china.com
 */
public interface EmployeeMapper extends Mapper<Employee> {

    /**
     * 通过员工编码查询员工.
     *
     * @param employeeCode 员工编码
     * @return 员工
     */
    Employee queryByCode(String employeeCode);

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
     * 条件查询员工.
     *
     * @param employee 员工
     * @return 员工列表
     */
    List<Employee> queryAll(Employee employee);

    /**
     * 通过岗位编码查询员工.
     *
     * @param positionCode 岗位编码
     * @return 员工列表
     */
    List<Employee> selectByPostionCode(String positionCode);

    /**
     * 通过员工编码查询员工信息.
     *
     * @param employeeCode 员工编码
     * @return 员工
     */
    Employee queryInfoByCode(String employeeCode);

    /**
     * 通过角色编码查询员工.
     *
     * @param roleCode 角色编码
     * @return 员工列表
     */
    List<Employee> selectByRoleCode(String roleCode);
}
