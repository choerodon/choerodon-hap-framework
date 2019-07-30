package io.choerodon.hap.iam.exception;

import io.choerodon.base.exception.BaseException;

/**
 * 菜单异常.
 *
 * @author qiang.zeng
 * @since 2019/5/20
 */
public class MemberRoleException extends BaseException {

    private static final long serialVersionUID = 1L;

    /**
     * 角色id不能为空.
     */
    public static final String ERROR_ROLE_ID_EMPTY = "error.role.id.empty";
    /**
     * 用户角色不存在.
     */
    public static final String ERROR_MEMBER_ROLE_NOT_EXISTED = "error.member.role.not.existed";


    public MemberRoleException(String code, Object... parameters) {
        super(code, code, parameters);
    }

    public MemberRoleException(String code, String message, Object... parameters) {
        super(code, message, parameters);
    }

}
