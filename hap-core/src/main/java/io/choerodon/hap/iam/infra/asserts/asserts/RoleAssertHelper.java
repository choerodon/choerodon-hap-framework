package io.choerodon.hap.iam.infra.asserts.asserts;

import io.choerodon.hap.iam.exception.ChoerodonRoleException;
import io.choerodon.hap.iam.infra.dto.RoleDTO;
import io.choerodon.hap.iam.infra.mapper.ChoerodonRoleMapper;
import org.springframework.stereotype.Component;

import static io.choerodon.hap.iam.exception.ChoerodonRoleException.ERROR_ROLE_CODE_EXISTED;
import static io.choerodon.hap.iam.exception.ChoerodonRoleException.ERROR_ROLE_NOT_ALLOW_TO_BE_DELETE;
import static io.choerodon.hap.iam.exception.ChoerodonRoleException.ERROR_ROLE_NOT_EXISTED;

/**
 * 角色断言帮助类.
 *
 * @author superlee
 * @since 2019-04-15
 */
@Component
public class RoleAssertHelper {

    private ChoerodonRoleMapper roleMapper;

    public RoleAssertHelper(ChoerodonRoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    public void codeExisted(String code) throws ChoerodonRoleException {
        RoleDTO dto = new RoleDTO();
        dto.setCode(code);
        if (!roleMapper.select(dto).isEmpty()) {
            throw new ChoerodonRoleException(ERROR_ROLE_CODE_EXISTED);
        }
    }

    public RoleDTO roleNotExisted(Long id) throws ChoerodonRoleException {
        RoleDTO dto = roleMapper.selectByPrimaryKey(id);
        if (dto == null) {
            throw new ChoerodonRoleException(ERROR_ROLE_NOT_EXISTED, id);
        }
        return dto;
    }

    public void roleIsBuiltIn(Boolean builtIn) throws ChoerodonRoleException {
        if (builtIn) {
            throw new ChoerodonRoleException(ERROR_ROLE_NOT_ALLOW_TO_BE_DELETE);
        }
    }
}
