package io.choerodon.hap.task;

import io.choerodon.hap.task.dto.TaskExecution;

/**
 * @author peng.jiang@hand-china.com
 * @since 2018/1/15
 **/
public interface ExecuteLogConvertStrategy {


    /**
     * 执行日志输出.
     *
     * @param taskExecution 任务执行信息
     * @return 输出执行日志
     */
    String convertLog(TaskExecution taskExecution);
}
