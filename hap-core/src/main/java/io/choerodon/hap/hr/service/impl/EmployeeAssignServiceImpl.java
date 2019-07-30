package io.choerodon.hap.hr.service.impl;

import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.hr.dto.EmployeeAssign;
import io.choerodon.hap.hr.mapper.EmployeeAssignMapper;
import io.choerodon.hap.hr.service.IEmployeeAssignService;
import io.choerodon.mybatis.service.BaseServiceImpl;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * 员工岗位分配服务接口实现.
 *
 * @author yuliao.chen@hand-china.com
 */
@Service
@Dataset("EmployeeAssign")
public class EmployeeAssignServiceImpl extends BaseServiceImpl<EmployeeAssign> implements IEmployeeAssignService, IDatasetService<EmployeeAssign> {

    @Autowired
    private EmployeeAssignMapper employeeAssignMapper;

    @Override
    public List<EmployeeAssign> selectByEmployeeId(Long employeeId, int page, int pagesize) {
        return employeeAssignMapper.selectByEmployeeId(employeeId);
    }

    @Override
    public Long getCompanyByEmployeeId(Long employeeId) {
        return employeeAssignMapper.getCompanyByEmployeeId(employeeId);
    }

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        EmployeeAssign employeeAssign = new EmployeeAssign();
        try {
            BeanUtils.populate(employeeAssign, body);
            return employeeAssignMapper.selectByEmployeeId(employeeAssign.getEmployeeId());
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new DatasetException("dataset.error", e);
        }
    }

    @Override
    public List<EmployeeAssign> mutations(List<EmployeeAssign> employeeAssignList) {

        return self().batchUpdate(employeeAssignList);
    }
}
