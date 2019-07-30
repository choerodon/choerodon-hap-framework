package io.choerodon.hap.message.components;

import io.choerodon.hap.task.TaskConstants;
import io.choerodon.hap.task.dto.TaskExecution;
import io.choerodon.hap.task.info.ThreadManageInfo;
import io.choerodon.hap.task.service.ITaskExecutionService;
import io.choerodon.message.IMessageConsumer;
import io.choerodon.message.annotation.TopicMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;


/**
 * 取消任务.
 *
 * @author peng.jiang@hand-china.com
 * @since 2017/11/29
 **/
@Component
@TopicMonitor(channel = {TaskCancel.TASK_CANCEL})
public class TaskCancel implements IMessageConsumer<TaskExecution>, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(TaskCancel.class);

    public static final String TASK_CANCEL = "task_cancel";

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    private ITaskExecutionService taskExecutionService;

    @Override
    public void onMessage(TaskExecution message, String pattern) {

        boolean isReady = false;

        //就绪状态任务取消
        String readyKey = TaskConstants.TASK_QUEUE + "-" + message.getExecutionId();
        BlockingQueue<Runnable> taskQueue = taskExecutor.getThreadPoolExecutor().getQueue();
        Iterator<Runnable> iterator = taskQueue.iterator();
        while (iterator.hasNext()) {
            Thread thread = (Thread) iterator.next();
            if (thread.getName().equals(readyKey)) {
                taskQueue.remove(thread);
                // 当前执行的任务组或任务状态改为取消
                taskExecutionService.updateStatus(message.getExecutionId(), TaskConstants.EXECUTION_CANCEL);
                // 就绪的子任务状态改为未执行
                taskExecutionService.batchUpdateStatus(message.getExecutionId(), TaskConstants.EXECUTION_READY,
                        TaskConstants.EXECUTION_UN_EXECUTED);
                isReady = true;
                return;
            }
        }

        if (!isReady) {
            // 执行状态任务取消
            String key = TaskConstants.TASK_TYPE_TASK + "-" + message.getExecutionId();
            Thread thread = ThreadManageInfo.threadHashMap.get(key);
            if (thread != null) {
                logger.info("TaskThread {} interrupt", key);
                thread.interrupt();
                taskExecutionService.updateStatus(message.getExecutionId(), TaskConstants.EXECUTION_CANCELING);
                taskExecutionService.batchUpdateStatus(message.getExecutionId(), TaskConstants.EXECUTION_EXECUTING, TaskConstants.EXECUTION_CANCELING);
                ThreadManageInfo.threadHashMap.remove(key);
            }
        }

    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }

}
