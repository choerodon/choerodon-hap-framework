package io.choerodon.hap.account.exception;

import io.choerodon.base.exception.BaseException;

/**
 * 删除管理员异常.
 *
 * @author njq.niu@hand-china.com
 * @since 2016/1/15
 */
public class AdminNotDeleteException extends BaseException {

    private static final long serialVersionUID = 9046687211507280533L;

    private static final String MSG_ERROR_ADMIN_NOT_DELETE = "msg.error.user.admin.not_delete";

    private static final String CODE_ADMIN_NOT_DELETE = "ADMIN_DELETE_ERROR";

    public AdminNotDeleteException() {
        super(CODE_ADMIN_NOT_DELETE, MSG_ERROR_ADMIN_NOT_DELETE, null);
    }
}
