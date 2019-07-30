package io.choerodon.hap.core.util;

import com.google.common.base.Throwables;


/**
 * @author peng.jiang@hand-china.com
 * @since 2018/3/6.
 */
public class ExceptionUtil {

    /**
     * 获取root异常堆栈.
     *
     * @param throwable Throwable
     * @return root异常堆栈
     */
    public static String getRootCauseStackTrace(Throwable throwable) {
        Throwable t = Throwables.getRootCause(throwable);
        return Throwables.getStackTraceAsString(t);
    }
}
