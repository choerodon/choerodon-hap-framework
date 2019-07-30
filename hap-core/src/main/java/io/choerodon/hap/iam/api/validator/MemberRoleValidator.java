package io.choerodon.hap.iam.api.validator;

import io.choerodon.hap.iam.exception.ChoerodonRoleException;
import io.choerodon.hap.iam.exception.MemberRoleException;
import io.choerodon.hap.iam.infra.dto.MemberRoleDTO;
import io.choerodon.hap.iam.infra.dto.RoleDTO;
import io.choerodon.hap.iam.infra.mapper.ChoerodonRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static io.choerodon.hap.iam.exception.ChoerodonRoleException.ERROR_ROLE_NOT_EXISTED;
import static io.choerodon.hap.iam.exception.MemberRoleException.ERROR_ROLE_ID_EMPTY;

/**
 * @author wuguokai
 */
@Component
public class MemberRoleValidator {
    @Autowired
    private ChoerodonRoleMapper roleMapper;

    public MemberRoleValidator(ChoerodonRoleMapper roleMapper) {
        this.roleMapper = roleMapper;

    }

    public void distributionRoleValidator(List<MemberRoleDTO> memberRoleDTOS) throws MemberRoleException, ChoerodonRoleException {
        for (MemberRoleDTO memberRoleDTO : memberRoleDTOS) {
            if (memberRoleDTO.getRoleId() == null) {
                throw new MemberRoleException(ERROR_ROLE_ID_EMPTY);
            }
            RoleDTO roleDTO = roleMapper.selectByPrimaryKey(memberRoleDTO.getRoleId());
            if (roleDTO == null) {
                throw new ChoerodonRoleException(ERROR_ROLE_NOT_EXISTED);
            }
        }
    }
}
