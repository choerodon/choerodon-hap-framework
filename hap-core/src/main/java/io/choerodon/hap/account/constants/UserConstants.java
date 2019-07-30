package io.choerodon.hap.account.constants;

import io.choerodon.base.util.BaseConstants;

/**
 * 常量.
 *
 * @author chenjingxiong
 * @since 2016/6/10
 */
public class UserConstants implements BaseConstants {
    /**
     * 用户管理 - 用户状态(有效).
     */
    public static final String USER_STATUS_ACTIVE = "ACTV";

    /**
     * 调用userService的selectUsers时. 必须传入分页信息page
     */
    public static final Integer DEFAULT_PAGE = 1;

    /**
     * 调用userService的selectUsers时. 必须传入分页信息pageSize
     */
    public static final Integer DEFAULT_PAGE_SIZE = 10;

    /**
     * 发送限制 - 超过两次.
     */
    public static final String SENT_LIMIT_ERROR = "当天发送超过2次限制";

    /**
     * 用户类型 - INNER: 内部用户.
     */
    public static final String USER_TYPE_INNER = "INNER";

    /**
     * 消息模板-临时口令.
     */
    public static final String TEMP_PWD_MESSAGE_TEMPLATE = "EMAIL_USER_TEMP_PWD";

    public static final int NUMBER_2 = 2;

    public static final int NUMBER_9 = 9;

    public static final String SERVER_VARIABLE = "服务端变量";
}
