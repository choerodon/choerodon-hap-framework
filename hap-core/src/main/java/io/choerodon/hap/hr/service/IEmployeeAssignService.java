package io.choerodon.hap.hr.service;

import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.hr.dto.EmployeeAssign;
import io.choerodon.mybatis.service.IBaseService;

import java.util.List;

/**
 * 员工岗位分配服务接口.
 *
 * @author yuliao.chen@hand-china.com
 */
public interface IEmployeeAssignService extends IBaseService<EmployeeAssign>, ProxySelf<IEmployeeAssignService> {
    /**
     * 通过员工Id查询岗位分配
     *
     * @param employeeId 员工Id
     * @param page       页码
     * @param pageSize   页数
     * @return 员工岗位分配列表
     */
    List<EmployeeAssign> selectByEmployeeId(Long employeeId, int page, int pageSize);

    /**
     * 通过员工Id查询公司Id.
     *
     * @param employeeId 员工Id
     * @return 公司Id
     */
    Long getCompanyByEmployeeId(Long employeeId);
}
