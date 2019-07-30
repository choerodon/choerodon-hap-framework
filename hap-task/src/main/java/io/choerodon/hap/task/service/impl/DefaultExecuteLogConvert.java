package io.choerodon.hap.task.service.impl;

import io.choerodon.hap.task.ExecuteLogConvertStrategy;
import io.choerodon.hap.task.dto.TaskExecution;

import java.text.SimpleDateFormat;

/**
 * @author peng.jiang@hand-china.com
 * @since 2018/1/15
 **/
public class DefaultExecuteLogConvert implements ExecuteLogConvertStrategy {

    @Override
    public String convertLog(TaskExecution taskExecution) {
        StringBuffer log = new StringBuffer();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        log.append("任务执行基本信息:\r\n");
        log.append("执行编号:").append(taskExecution.getExecutionNumber()).append("\r\n");
        log.append("执行状态:").append(taskExecution.getStatus()).append("\r\n");
        log.append("任务代码:").append(taskExecution.getTaskDetail().getCode()).append("\r\n");
        log.append("任务名称:").append(taskExecution.getTaskDetail().getName()).append("\r\n");
        log.append("任务类名:").append(taskExecution.getTaskDetail().getTaskClass()).append("\r\n");
        log.append("任务描述:").append(taskExecution.getTaskDetail().getDescription()).append("\r\n");
        if (taskExecution.getStartTime() != null) {
            log.append("开始时间:").append(formatter.format(taskExecution.getStartTime())).append("\r\n");
        } else {
            log.append("开始时间:\r\n");
        }
        if (taskExecution.getEndTime() != null) {
            log.append("结束时间:").append(formatter.format(taskExecution.getEndTime())).append("\r\n");
        } else {
            log.append("结束时间:\r\n");
        }
        log.append("提交人:").append(taskExecution.getUserId()).append("\r\n");
        if (taskExecution.getLastExecuteDate() != null) {
            log.append("上次执行时间:").append(formatter.format(taskExecution.getLastExecuteDate())).append("\r\n");
        } else {
            log.append("上次执行时间:\r\n");
        }
        if (taskExecution.getExecutionDescription() != null) {
            log.append("执行描述:").append(taskExecution.getExecutionDescription()).append("\r\n");
        } else {
            log.append("执行描述:\r\n");
        }

        if (taskExecution.getTaskExecutionDetail().getParameter() != null) {
            log.append("\r\n参数:\r\n");
            log.append(taskExecution.getTaskExecutionDetail().getParameter());
        }

        if (taskExecution.getTaskExecutionDetail().getStacktrace() != null) {
            log.append("\r\n\r\n异常信息:\r\n");
            log.append(taskExecution.getTaskExecutionDetail().getStacktrace());
        }

        log.append("\r\n\r\n======================================================================");
        if (taskExecution.getTaskExecutionDetail().getExecutionLog() != null) {
            log.append("\r\n\r\n执行日志:\r\n");
            log.append(taskExecution.getTaskExecutionDetail().getExecutionLog());
        }
        return log.toString();
    }
}
