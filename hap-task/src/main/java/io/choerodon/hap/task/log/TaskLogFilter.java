package io.choerodon.hap.task.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import io.choerodon.hap.task.TaskConstants;
import org.slf4j.MDC;

/**
 * 任务日志拦截器.
 *
 * @author peng.jiang@hand-china.com
 * @since 2017/11/19.
 **/

public class TaskLogFilter extends Filter<ILoggingEvent> {
    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (event.getMessage() != null && MDC.get(TaskConstants.LOG_KEY) != null
                && MDC.get(TaskConstants.LOG_KEY).equals(TaskConstants.LOG_VALUE)) {
            return FilterReply.NEUTRAL;
        } else {
            return FilterReply.DENY;
        }

    }
}
