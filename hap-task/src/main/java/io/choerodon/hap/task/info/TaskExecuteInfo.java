package io.choerodon.hap.task.info;

/**
 * 数据传出类 - 任务/任务组执行.
 *
 * @author peng.jiang@hand-china.com
 * @since 2017/11/16.
 **/

public class TaskExecuteInfo {

    public static final ThreadLocal<StringBuffer> TASK_LOG = new ThreadLocal<>();

    public static void clear() {
        TASK_LOG.remove();
    }

}
