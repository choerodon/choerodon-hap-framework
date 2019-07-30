package io.choerodon.hap.account.exception;


import io.choerodon.base.exception.BaseException;

/**
 * 用户异常.
 *
 * @author njq.niu@hand-china.com
 * @author xiawang.liu@hand-china.com
 * @since 2016/3/3
 */
public class UserException extends BaseException {

    private static final long serialVersionUID = -3250576758107608016L;

    /**
     * 验证码不正确
     */
    public static final String ERROR_INVALID_CAPTCHA = "error.login.verification_code_error";

    /**
     * 用户名密码不匹配
     */
    public static final String ERROR_USER_PASSWORD = "error.login.name_password_not_match";

    /**
     * 账户状态未激活
     */
    public static final String ERROR_USER_NOT_ACTIVE = "error.user.account_not_active";

    /**
     * 账户失效
     */
    public static final String ERROR_USER_EXPIRED = "error.user.account_expired";

    /**
     * 账户锁定
     */
    public static final String ERROR_USER_LOCKED = "error.user.account_locked";

    private static final String CODE_EXCEPTION = "SYS_USER";

    /**
     * 用户管理 - 登录名格式错误.
     */
    public static final String USER_FORMAT = "error.user.user_format";

    /**
     * 用户管理 - 手机格式错误.
     */
    public static final String PHONE_FORMAT = "error.user.phone";

    /**
     * 用户管理 - 邮箱格式错误.
     */
    public static final String EMAIL_FORMAT = "error.user.email";

    /**
     * 密码管理 - 忘记密码 - 验证码出错.
     */
    public static final String LOGIN_VERIFICATION_CODE_ERROR = "error.login.verification_code_error";

    /**
     * 密码管理 - 修改密码 - 两次密码不能一致.
     */
    public static final String USER_PASSWORD_NOT_SAME_TWICE = "error.user.two_password_not_match";
    /**
     * 密码管理 - 修改密码 - 当前密码校验.
     */
    public static final String USER_PASSWORD_WRONG = "error.password.current_password";

    /**
     * 密码需数字 字母
     */
    public static final String USER_PASSWORD_REQUIREMENT_DIGITS_AND_LETTERS = "error.user.password_format_error_digits_and_letters";

    /**
     * 密码需数字 大小写字母
     */
    public static final String USER_PASSWORD_REQUIREMENT_DIGITS_AND_CASE_LETTERS = "error.user.password_format_error_digits_and_case_letters";

    /**
     * 密码管理 - 修改密码 - 密码长度不能小于最小长度.
     */
    public static final String USER_PASSWORD_LENGTH_INSUFFICIENT = "error.user.password_length_insufficient";

    /**
     * 密码管理 - 修改密码 - 新密码不能与旧密码一致.
     */
    public static final String USER_PASSWORD_SAME = "error.system.not_allowed_same_with_old_password";

    /**
     * 用户管理 - 个人信息 - 用户名不存在.
     */
    public static final String USER_NOT_EXIST = "error.system.user_not_exist";

    /**
     * 用户管理 - 个人信息 - 用户已失效.
     */
    public static final String USER_EXPIRED = "error.system.user_expired";

    /**
     * 用户管理 - 个人信息 - 创建用户失败.
     */
    public static final String USER_INSERT_FAIL = "error.system.user_insert_fail";

    /**
     * 用户管理 - 密码不能为空.
     */
    public static final String PASSWORD_NOT_EMPTY = "error.user.password_not_empty";

    public UserException(String code, String message, Object... parameters) {
        super(code, message, parameters);
    }

    public UserException(String message, Object... parameters) {
        super(CODE_EXCEPTION, message, parameters);
    }

}
