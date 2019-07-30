package io.choerodon.hap.task.service.impl;

import com.github.pagehelper.util.StringUtil;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.core.AppContextInitListener;
import io.choerodon.hap.message.TaskListenerContainer;
import io.choerodon.hap.task.TaskConstants;
import io.choerodon.hap.task.dto.TaskDetail;
import io.choerodon.hap.task.dto.TaskExecution;
import io.choerodon.hap.task.exception.TaskInterruptException;
import io.choerodon.hap.task.info.ExecutionInfo;
import io.choerodon.hap.task.info.ParameterInfo;
import io.choerodon.hap.task.info.TaskDataInfo;
import io.choerodon.hap.task.info.ThreadManageInfo;
import io.choerodon.hap.task.service.IExecuteListener;
import io.choerodon.hap.task.service.IExecuteService;
import io.choerodon.hap.task.service.ITask;
import io.choerodon.hap.task.service.ITaskDetailService;
import io.choerodon.hap.task.service.ITaskExecutionService;
import io.choerodon.web.core.impl.RequestHelper;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RejectedExecutionException;

/**
 * 执行任务service
 *
 * @author peng.jiang@hand-china.com
 * @since 2017/11/6.
 **/

@Service
@Dataset("TaskExecute")
public class ExecuteServiceImpl implements IExecuteService, AppContextInitListener, IDatasetService<TaskExecution> {

    private static final Logger logger = LoggerFactory.getLogger(ExecuteServiceImpl.class);

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ITaskDetailService taskDetailService;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private ITaskExecutionService taskExecutionService;

    private List<IExecuteListener> executeListeners = Collections.emptyList();

    @Override
    public void taskExecute(TaskDataInfo taskDataInfo) {
        TaskThread taskThread = new TaskThread(taskDataInfo);
        taskThread.setName(TaskConstants.TASK_QUEUE + "-" + taskDataInfo.getExecutionId());
        try {
            taskExecutor.execute(taskThread);
        } catch (RejectedExecutionException e) {
            // 任务被线程池拒绝，修改执行记录状态

            // 当前执行的任务组或任务状态改为未执行
            taskExecutionService.updateStatus(taskDataInfo.getExecutionId(), TaskConstants.EXECUTION_UN_EXECUTED);
            // 就绪的子任务状态改为未执行
            taskExecutionService.batchUpdateStatus(taskDataInfo.getExecutionId(), TaskConstants.EXECUTION_READY,
                    TaskConstants.EXECUTION_UN_EXECUTED);

            throw e;
        }
    }

    private class TaskThread extends Thread {

        private TaskDataInfo taskDataInfo = null;

        private boolean isSuccess = true;

        private TaskThread(TaskDataInfo taskDataInfo) {
            this.taskDataInfo = taskDataInfo;
        }

        @Override
        public void run() {

            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
            TransactionStatus status = transactionManager.getTransaction(def);

            Thread thread = Thread.currentThread();
            // 设置当前线程执行name
            thread.setName(TaskConstants.TASK_TYPE_TASK + "-" + taskDataInfo.getExecutionId());
            ThreadManageInfo.threadHashMap.put(thread.getName(), thread);

            execute(taskDataInfo);

            // tomcat shutdown 不再执行以下代码
            if (!TaskListenerContainer.running) {
                if (isSuccess && !thread.isInterrupted()) {
                    transactionManager.commit(status);
                } else if (!isSuccess) {
                    transactionManager.rollback(status);
                } else if (thread.isInterrupted()) {
                    transactionManager.rollback(status);
                    updateStatus(taskDataInfo);
                }
                ThreadManageInfo.threadHashMap.remove(thread.getName());
            }

        }

        /**
         * 执行任务
         *
         * @param taskDataInfo 任务/任务组
         */
        private void execute(TaskDataInfo taskDataInfo) {
            if (taskDataInfo.getType().equals(TaskConstants.TASK_TYPE_TASK)) {
                executeTask(taskDataInfo);
            } else {
                for (int i = 0; i < taskDataInfo.getTaskDatas().size(); i++) {
                    if (!isSuccess || Thread.currentThread().isInterrupted()) {
                        return;
                    }
                    // 设置当前执行的子任务序号
                    taskDataInfo.setCurrentExecution(i);
                    // 设置当前执行的子任务
                    taskDataInfo.setCurrentTask(taskDataInfo.getTaskDatas().get(i));
                    executeTask(taskDataInfo);
                }
            }
        }

        /**
         * 执行任务 捕获执行异常
         *
         * @param taskDataInfo 任务执行详情
         */
        private void executeTask(TaskDataInfo taskDataInfo) {
            String executeResultPath = "";
            try {
                executeListeners.forEach(executeListener -> {
                    executeListener.before(taskDataInfo);
                });
                executeResultPath = operate(taskDataInfo);
                if (!Thread.currentThread().isInterrupted()) {
                    executeListeners.forEach(executeListener -> {
                        executeListener.after(taskDataInfo);
                    });
                }

                // 写入执行结果路径
                if (StringUtil.isNotEmpty(executeResultPath)) {
                    TaskExecution taskExecution = new TaskExecution();
                    if (TaskConstants.TASK_TYPE_TASK.equals(taskDataInfo.getType())) {
                        taskExecution.setExecutionId(taskDataInfo.getExecutionId());
                    } else {
                        taskExecution.setExecutionId(taskDataInfo.getCurrentTask().getExecutionId());
                    }
                    TaskExecution updateTaskExecution = taskExecutionService.selectByPrimaryKey(taskExecution);
                    if (updateTaskExecution == null) {
                        logger.error("Task execution for {} does not exist!", taskExecution.getExecutionId());
                    } else {
                        updateTaskExecution.setExecuteResultPath(executeResultPath);
                        taskExecutionService.updateByPrimaryKeySelective(updateTaskExecution);
                    }
                }

            } catch (TaskInterruptException e) {
                // 线程在非阻塞时被中断
            } catch (InterruptedException e) {
                // 线程在阻塞时被中断，中断标志重置，这里需要恢复中断状态
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                isSuccess = false;
                executeListeners.forEach(executeListener -> {
                    executeListener.doException(e, taskDataInfo);
                });
            } finally {
                // 执行实现类重新排序，用户实现类先执行，系统默认实现最后执行
                executeListeners.sort((a, b) -> b.getOrder() - a.getOrder());
                executeListeners.forEach(executeListener -> {
                    executeListener.doFinally(taskDataInfo);
                });
            }
        }

        /**
         * 调用接口 执行任务
         *
         * @param taskDataInfo 数据传输类-任务/任务组
         */
        private String operate(TaskDataInfo taskDataInfo) throws Exception {
            String taskClass = "";
            if (taskDataInfo.getType().equals(TaskConstants.TASK_TYPE_TASK)) {
                taskClass = taskDataInfo.getTaskClass();
            } else {
                taskClass = taskDataInfo.getCurrentTask().getTaskClass();
            }

            ITask task = (ITask) Class.forName(taskClass.trim()).newInstance();
            applicationContext.getAutowireCapableBeanFactory().autowireBean(task);
            ExecutionInfo executionInfo = translateExecutionInfo(taskDataInfo);
            task.execute(executionInfo);
            return executionInfo.getExecuteResultPath();
        }

        /**
         * 封装执行接口参数
         *
         * @param taskDataInfo 数据传输类-任务/任务组
         * @return 任务执行记录
         */
        private ExecutionInfo translateExecutionInfo(TaskDataInfo taskDataInfo) {
            ExecutionInfo executionInfo = new ExecutionInfo();
            executionInfo.setUsername(taskDataInfo.getUsername());

            Map<String, Object> map = transformMap(taskDataInfo);
            executionInfo.setParam(map);

            TaskDetail taskDetail = new TaskDetail();
            taskDetail.setTaskId(taskDataInfo.getTaskId());
            taskDetail = taskDetailService.selectByPrimaryKey(taskDetail);

            executionInfo.setTaskDetail(taskDetail);
            return executionInfo;
        }

        /**
         * 执行参数转为map
         *
         * @param taskDataInfo 数据传输类-任务/任务组
         * @return 执行参数
         */
        private Map<String, Object> transformMap(TaskDataInfo taskDataInfo) {
            Map<String, Object> parameterMap = new HashMap<>(16);
            List<ParameterInfo> parameterInfoList;
            if (taskDataInfo.getType().equals(TaskConstants.TASK_TYPE_TASK)) {
                parameterInfoList = taskDataInfo.getParam();
            } else {
                parameterInfoList = taskDataInfo.getCurrentTask().getParam();
            }
            for (ParameterInfo parameterInfo : parameterInfoList) {
                parameterMap.put(parameterInfo.getKey(), parameterInfo.getValue());
            }
            return parameterMap;
        }

        /**
         * 取消任务时修改执行记录状态
         *
         * @param taskDataInfo 数据传输类-任务/任务组
         */
        private void updateStatus(TaskDataInfo taskDataInfo) {

            // 当前执行的任务组或任务状态改为取消
            taskExecutionService.updateStatus(taskDataInfo.getExecutionId(), TaskConstants.EXECUTION_CANCEL);

            if (taskDataInfo.getType().equals(TaskConstants.TASK_TYPE_GROUP)) {
                // 成功的子任务状态改为回滚
                taskExecutionService.batchUpdateStatus(taskDataInfo.getExecutionId(), TaskConstants.EXECUTION_SUCCESS,
                        TaskConstants.EXECUTION_ROLLBACK);

                // 就绪的子任务状态改为未执行
                taskExecutionService.batchUpdateStatus(taskDataInfo.getExecutionId(), TaskConstants.EXECUTION_READY,
                        TaskConstants.EXECUTION_UN_EXECUTED);

                // 当前执行的子任务改为取消
                taskExecutionService.updateStatus(taskDataInfo.getCurrentTask().getExecutionId(),
                        TaskConstants.EXECUTION_CANCEL);
            }
        }
    }

    @Override
    public void contextInitialized(ApplicationContext applicationContext) {
        executeListeners = new ArrayList<>(applicationContext.getBeansOfType(IExecuteListener.class).values());
        executeListeners.sort(Comparator.comparingInt(IExecuteListener::getOrder));
    }


    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            TaskDetail taskDetail = new TaskDetail();
            BeanUtils.populate(taskDetail, body);
            return taskDetailService.executeQuery(RequestHelper.getCurrentRequest(), taskDetail, page, pageSize);
        } catch (Exception e) {
            throw new DatasetException("dataset:error", e);
        }
    }

    @Override
    public List<TaskExecution> mutations(List<TaskExecution> objs) {
        return null;
    }

}
