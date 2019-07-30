package io.choerodon.hap.iam.exception;

import io.choerodon.base.exception.BaseException;

/**
 * 角色异常.
 *
 * @author qiang.zeng
 * @since 2019/5/16
 */
public class ChoerodonRoleException extends BaseException {

    private static final long serialVersionUID = 1L;

    /**
     * 角色不存在.
     */
    public static final String ERROR_ROLE_NOT_EXISTED = "error.role.not.existed";
    /**
     * 角色更新异常.
     */
    public static final String ERROR_ROLE_UPDATE = "error.role.update";
    /**
     * 角色编码为空.
     */
    public static final String ERROR_ROLE_CODE_EMPTY = "error.role.code.empty";
    /**
     * 角色编码已存在.
     */
    public static final String ERROR_ROLE_CODE_EXISTED = " error.role.code.existed";
    /**
     * 角色编码长度不合法，请不要超过128个字符.
     */
    public static final String ERROR_ROLE_CODE_LENGTH = "error.role.code.length";
    /**
     * 该角色没有一个菜单，请至少选择一个菜单.
     */
    public static final String ERROR_ROLE_PERMISSION_EMPTY = "error.role.permission.empty";
    /**
     * 角色名称为空.
     */
    public static final String ERROR_ROLE_NAME_EMPTY = "error.role.name.empty";
    /**
     * 角色名称长度不合法，请不要超过64个字符.
     */
    public static final String ERROR_ROLE_NAME_LENGTH = "error.role.name.length";
    /**
     * 内置角色不允许被删除.
     */
    public static final String ERROR_ROLE_NOT_ALLOW_TO_BE_DELETE = "error.role.not.allow.to.be.delete";

    public ChoerodonRoleException(String code, Object... parameters) {
        super(code, code, parameters);
    }

    public ChoerodonRoleException(String code, String message, Object... parameters) {
        super(code, message, parameters);
    }

}
