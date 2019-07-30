package io.choerodon.hap.account.exception;

import io.choerodon.base.exception.BaseException;

/**
 * 角色异常.
 *
 * @author njq.niu@hand-china.com
 * @since 2016/4/25
 */
public class RoleException extends BaseException {

    private static final long serialVersionUID = 1L;

    /**
     * 账号角色不存在.
     */
    public static final String MSG_INVALID_USER_ROLE = "error.account_role_invalid";

    /**
     * 账号无有效角色.
     */
    public static final String MSG_NO_USER_ROLE = "error.account_no_role";

    public RoleException(String code, String message, Object[] parameters) {
        super(code, message, parameters);
    }

}
