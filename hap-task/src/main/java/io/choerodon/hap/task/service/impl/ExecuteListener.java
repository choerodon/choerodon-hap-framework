package io.choerodon.hap.task.service.impl;

import com.google.common.base.Throwables;
import io.choerodon.hap.message.components.TaskExecuteLogManager;
import io.choerodon.hap.task.TaskConstants;
import io.choerodon.hap.task.dto.TaskExecutionDetail;
import io.choerodon.hap.task.info.TaskDataInfo;
import io.choerodon.hap.task.info.TaskExecuteInfo;
import io.choerodon.hap.task.service.IExecuteListener;
import io.choerodon.hap.task.service.ITaskExecutionDetailService;
import io.choerodon.hap.task.service.ITaskExecutionService;
import io.choerodon.message.IMessagePublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 任务执行处理-实现接口.
 *
 * @author peng.jiang@hand-china.com
 * @since 2017/11/13.
 **/

@Component
public class ExecuteListener implements IExecuteListener {

    private static final Logger logger = LoggerFactory.getLogger(ExecuteListener.class);

    public static final int ORDER = 1;

    @Autowired
    private IMessagePublisher messagePublisher;

    @Autowired
    private ITaskExecutionDetailService iTaskExecutionDetailService;

    @Autowired
    private ITaskExecutionService taskExecutionService;

    @Override
    public void before(TaskDataInfo taskDataInfo) {
        /**
         * 拦截任务调用入口方法，设置日志记录标志
         */
        MDC.put(TaskConstants.LOG_KEY, TaskConstants.LOG_VALUE);
        TaskExecuteInfo.TASK_LOG.set(new StringBuffer());

        if (taskDataInfo.getType().equals(TaskConstants.TASK_TYPE_TASK)) {
            //任务，修改状态
            taskExecutionService.updateStatus(taskDataInfo.getExecutionId(), TaskConstants.EXECUTION_EXECUTING);
        } else if (taskDataInfo.getCurrentExecution() == 0) {
            //任务组，执行第一个任务，修改子任务与任务组状态
            taskExecutionService.updateStatus(taskDataInfo.getExecutionId(), TaskConstants.EXECUTION_EXECUTING);
            taskExecutionService.updateStatus(taskDataInfo.getCurrentTask().getExecutionId(), TaskConstants.EXECUTION_EXECUTING);
        } else {
            //任务组，执行非第一个任务，修改子任务状态
            taskExecutionService.updateStatus(taskDataInfo.getCurrentTask().getExecutionId(), TaskConstants.EXECUTION_EXECUTING);
        }

    }

    @Override
    public void after(TaskDataInfo taskDataInfo) {

        if (taskDataInfo.getType().equals(TaskConstants.TASK_TYPE_TASK)) {
            //任务，修改状态
            taskExecutionService.updateStatus(taskDataInfo.getExecutionId(), TaskConstants.EXECUTION_SUCCESS);
        } else if (taskDataInfo.getCurrentExecution() == (taskDataInfo.getTaskDatas().size() - 1)) {
            //任务组，执行最后一个任务，修改任务组与子任务状态
            taskExecutionService.updateStatus(taskDataInfo.getExecutionId(), TaskConstants.EXECUTION_SUCCESS);
            taskExecutionService.updateStatus(taskDataInfo.getCurrentTask().getExecutionId(), TaskConstants.EXECUTION_SUCCESS);
        } else {
            //任务组，执行非最后一个任务，修改子任务状态
            taskExecutionService.updateStatus(taskDataInfo.getCurrentTask().getExecutionId(), TaskConstants.EXECUTION_SUCCESS);
        }

    }

    @Override
    public void doException(Exception e, TaskDataInfo taskDataInfo) {
        if (taskDataInfo.getType().equals(TaskConstants.TASK_TYPE_TASK)) {
            //任务，处理异常
            taskExecutionService.updateStatus(taskDataInfo.getExecutionId(), TaskConstants.EXECUTION_FAILURE);
            updateExecutionDetailStacktrace(e, taskDataInfo);

        } else {
            //任务组，处理异常
            taskExecutionService.updateStatus(taskDataInfo.getExecutionId(), TaskConstants.EXECUTION_FAILURE);
            //当前执行的子任务状态改为失败
            taskExecutionService.updateStatus(taskDataInfo.getCurrentTask().getExecutionId(), TaskConstants.EXECUTION_FAILURE);
            updateExecutionDetailStacktrace(e, taskDataInfo.getCurrentTask());
            //已成功的子任务状态改为回滚
            taskExecutionService.batchUpdateStatus(taskDataInfo.getExecutionId(), TaskConstants.EXECUTION_SUCCESS, TaskConstants.EXECUTION_ROLLBACK);
            //就绪的子任务状态改为未执行
            taskExecutionService.batchUpdateStatus(taskDataInfo.getExecutionId(), TaskConstants.EXECUTION_READY, TaskConstants.EXECUTION_UN_EXECUTED);

        }
    }

    @Override
    public void doFinally(TaskDataInfo taskDataInfo) {

        TaskExecutionDetail taskExecutionDetail = new TaskExecutionDetail();
        taskExecutionDetail.setExecutionLog(TaskExecuteInfo.TASK_LOG.get().toString());
        //清除MDC标志，记录执行日志信息
        MDC.remove(TaskConstants.LOG_KEY);
        TaskExecuteInfo.clear();

        if (taskDataInfo.getType().equals(TaskConstants.TASK_TYPE_TASK)) {
            taskExecutionDetail.setExecutionId(taskDataInfo.getExecutionId());
        } else {
            taskExecutionDetail.setExecutionId(taskDataInfo.getCurrentTask().getExecutionId());
        }

        messagePublisher.publish(TaskExecuteLogManager.TASK_EXECUTE_LOG, taskExecutionDetail);
    }

    /**
     * 执行失败时，保存失败信息.
     *
     * @param e        执行异常
     * @param taskData 任务/任务组封装类
     */
    private void updateExecutionDetailStacktrace(Exception e, TaskDataInfo taskData) {
        // 保存执行失败错误信息
        TaskExecutionDetail taskExecutionDetail = iTaskExecutionDetailService.selectByExecutionId(taskData.getExecutionId());
        if (taskExecutionDetail == null) {
            logger.error("Task execution detail for {} does not exist!", taskData.getExecutionId());
            return;
        }
        taskExecutionDetail.setStacktrace(Throwables.getStackTraceAsString(Throwables.getRootCause(e)));
        iTaskExecutionDetailService.updateStacktrace(taskExecutionDetail);
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}
