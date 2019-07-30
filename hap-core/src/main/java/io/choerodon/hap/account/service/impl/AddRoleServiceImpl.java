package io.choerodon.hap.account.service.impl;

import io.choerodon.hap.iam.app.service.ChoerodonRoleService;
import io.choerodon.hap.iam.infra.dto.MemberRoleDTO;
import io.choerodon.hap.iam.infra.dto.RoleDTO;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Dataset("AccountAddRole")
public class AddRoleServiceImpl implements IDatasetService<MemberRoleDTO> {
    @Autowired
    private ChoerodonRoleService roleService;

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            RoleDTO role = new RoleDTO();
            BeanUtils.populate(role, body);
            return roleService.selectRolesNotAssignToUser(role);
        } catch (Exception e) {
            throw new DatasetException("dataset.error", e);
        }
    }

    @Override
    public List<MemberRoleDTO> mutations(List<MemberRoleDTO> memberRoles) {
        return null;
    }
}
