package io.choerodon.hap.security;

import io.choerodon.hap.account.dto.User;
import io.choerodon.hap.account.exception.UserException;
import org.springframework.security.authentication.LockedException;

/**
 * 校验用户有效性工具类.
 *
 * @author qiang.zeng@hand-china.com
 * @since 2018/6/6
 */
public final class CheckUserUtil {

    /**
     * 校验用户有效性.
     *
     * @param user 用户
     */
    public static void checkUserException(User user) {
        UserException userException = null;
        if (User.STATUS_LOCK.equalsIgnoreCase(user.getStatus())) {
            userException = new UserException(UserException.ERROR_USER_LOCKED, null);
        } else if (User.STATUS_EXPR.equalsIgnoreCase(user.getStatus())) {
            userException = new UserException(UserException.ERROR_USER_EXPIRED, null);
        } else if (user.getStartActiveDate() != null
                && user.getStartActiveDate().getTime() > System.currentTimeMillis()) {
            userException = new UserException(UserException.ERROR_USER_NOT_ACTIVE, null);
        } else if (user.getEndActiveDate() != null && user.getEndActiveDate().getTime() < System.currentTimeMillis()) {
            userException = new UserException(UserException.ERROR_USER_EXPIRED, null);
        }
        if (userException != null) {
            throw new LockedException(userException.getMessage(), userException);
        }
    }
}
