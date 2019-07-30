package io.choerodon.hap.task;

/**
 * 常量.
 *
 * @author lijian.yin@hand-china.com
 **/

public class TaskConstants {

    /**
     * 就绪
     */
    public static final String EXECUTION_READY = "READY";

    /**
     * 执行中
     */
    public static final String EXECUTION_EXECUTING = "EXECUTING";

    /**
     * 完成
     */
    public static final String EXECUTION_SUCCESS = "SUCCESS";

    /**
     * 失败
     */
    public static final String EXECUTION_FAILURE = "FAILURE";

    /**
     * 未执行
     */
    public static final String EXECUTION_UN_EXECUTED = "UNEXECUTED";

    /**
     * 已回滚
     */
    public static final String EXECUTION_ROLLBACK = "ROLLBACK";

    /**
     * 已取消
     */
    public static final String EXECUTION_CANCEL = "CANCEL";

    /**
     * 正在取消
     */
    public static final String EXECUTION_CANCELING = "CANCELING";

    /**
     * 任务组
     */
    public static final String TASK_TYPE_GROUP = "GROUP";

    /**
     * 任务
     */
    public static final String TASK_TYPE_TASK = "TASK";

    public static final String TASK_QUEUE = "TASKQUEUE";

    public static final String LOG_KEY = "TYPE";

    public static final String LOG_VALUE = "TASK";
}
