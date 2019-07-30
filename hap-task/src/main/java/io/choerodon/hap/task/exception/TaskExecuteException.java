package io.choerodon.hap.task.exception;

import io.choerodon.base.exception.BaseException;

/**
 * 任务执行记录异常.
 *
 * @author lijian.yin@hand-china.com
 * @since 2017-11-13
 **/

public class TaskExecuteException extends BaseException {

    public static final String CODE_EXECUTE_ERROR = "TASK_EXECUTE_FAIL";

    public static final String MSG_SERVER_BUSY = "The server resource is busy. Please try again later";

    protected TaskExecuteException(String code, String descriptionKey, Object... parameters) {
        super(code, descriptionKey, parameters);
    }

    public TaskExecuteException(String code, String descriptionKey) {
        super(code, descriptionKey);
    }
}
