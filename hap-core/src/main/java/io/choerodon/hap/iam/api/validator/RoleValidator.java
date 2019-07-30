package io.choerodon.hap.iam.api.validator;

import io.choerodon.hap.iam.exception.ChoerodonRoleException;
import io.choerodon.hap.iam.infra.dto.PermissionDTO;
import io.choerodon.hap.iam.infra.dto.RoleDTO;
import org.springframework.util.StringUtils;

import java.util.List;

import static io.choerodon.hap.iam.exception.ChoerodonRoleException.ERROR_ROLE_CODE_EMPTY;
import static io.choerodon.hap.iam.exception.ChoerodonRoleException.ERROR_ROLE_CODE_LENGTH;
import static io.choerodon.hap.iam.exception.ChoerodonRoleException.ERROR_ROLE_NAME_EMPTY;
import static io.choerodon.hap.iam.exception.ChoerodonRoleException.ERROR_ROLE_NAME_LENGTH;
import static io.choerodon.hap.iam.exception.ChoerodonRoleException.ERROR_ROLE_PERMISSION_EMPTY;

/**
 * 角色校验器.
 *
 * @author qiang.zeng
 */
public class RoleValidator {

    /**
     * 创建或更新角色前校验.
     * 校验资源层级
     * 校验角色编码
     * 校验角色名称
     * 校验角色权限
     *
     * @param roleDTO 角色
     * @throws ChoerodonRoleException 创建角色异常
     */
    public static void insertOrUpdateCheck(RoleDTO roleDTO) throws ChoerodonRoleException {
        validateCode(roleDTO.getCode());
        validateName(roleDTO.getName());
        validatePermissions(roleDTO.getPermissions());
    }

    /**
     * 创建或更新角色时，校验角色编码.
     * 校验编码不能为空
     * 校验编码长度不能超过128
     *
     * @param code 角色编码
     * @throws ChoerodonRoleException 角色编码异常
     */
    public static void validateCode(String code) throws ChoerodonRoleException {
        if (StringUtils.isEmpty(code)) {
            throw new ChoerodonRoleException(ERROR_ROLE_CODE_EMPTY);
        }
        if (code.length() > 128) {
            throw new ChoerodonRoleException(ERROR_ROLE_CODE_LENGTH);
        }
    }

    /**
     * 创建或更新角色时，校验角色名称.
     * 校验名称不能为空
     * 校验名称长度不能超过64
     *
     * @param name 角色名称
     */
    public static void validateName(String name) throws ChoerodonRoleException {
        if (StringUtils.isEmpty(name)) {
            throw new ChoerodonRoleException(ERROR_ROLE_NAME_EMPTY);
        }
        if (name.length() > 64) {
            throw new ChoerodonRoleException(ERROR_ROLE_NAME_LENGTH);
        }
    }

    /**
     * 校验角色至少选择一个权限(即至少选择一个菜单).
     *
     * @param permissions 权限列表
     * @throws ChoerodonRoleException 角色权限为空异常
     */
    public static void validatePermissions(List<PermissionDTO> permissions) throws ChoerodonRoleException {
        if (permissions == null || permissions.isEmpty()) {
            throw new ChoerodonRoleException(ERROR_ROLE_PERMISSION_EMPTY);
        }
    }

}
