package io.choerodon.hap.account.service.impl;

import io.choerodon.hap.account.dto.User;
import io.choerodon.hap.iam.app.service.ChoerodonRoleService;
import io.choerodon.hap.iam.app.service.RoleMemberService;
import io.choerodon.hap.iam.infra.dto.MemberRoleDTO;
import io.choerodon.hap.iam.infra.dto.RoleDTO;
import io.choerodon.hap.system.dto.DTOStatus;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Dataset("AccountSelfRole")
public class SelfRoleServiceImpl implements IDatasetService<MemberRoleDTO> {
    @Autowired
    private ChoerodonRoleService roleService;
    @Autowired
    private RoleMemberService roleMemberService;

    @Override
    public List<RoleDTO> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            User user = new User();
            BeanUtils.populate(user, body);
            return roleService.selectRolesAssignToUserByUserId(user.getUserId());
        } catch (Exception e) {
            throw new DatasetException("dataset.error", e);
        }
    }

    @Override
    public List<MemberRoleDTO> mutations(List<MemberRoleDTO> memberRoles) {
        if (CollectionUtils.isNotEmpty(memberRoles)) {
            for (MemberRoleDTO memberRole : memberRoles) {
                switch (memberRole.get__status()) {
                    case DTOStatus.DELETE:
                        roleMemberService.deleteByPrimaryKey(memberRole);
                        break;
                }
            }
        }
        return memberRoles;
    }
}
