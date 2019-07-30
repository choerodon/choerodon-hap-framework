package io.choerodon.hap.message;

import io.choerodon.hap.task.TaskConstants;
import io.choerodon.hap.task.info.ThreadManageInfo;
import io.choerodon.hap.task.service.ITaskExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * 监听Spring容器关闭时
 * 关闭队列中的任务
 * 关闭正在执行的任务
 *
 * @author peng.jiang@hand-china.com
 * @since 2017/12/1
 **/
public class TaskListenerContainer implements SmartLifecycle {

    private static Logger logger = LoggerFactory.getLogger(TaskListenerContainer.class);

    private static final int PHASE = 99999;

    public static boolean running = false;

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    private ITaskExecutionService taskExecutionService;

    @Override
    public void start() {
    }

    @Override
    public int getPhase() {
        return PHASE;
    }

    @Override
    public boolean isAutoStartup() {
        // 不执行start
        return running;
    }

    @Override
    public boolean isRunning() {
        // 执行stop(runnable)
        running = true;
        return running;
    }

    @Override
    public void stop(Runnable callback) {
        //就绪状态任务取消
        BlockingQueue<Runnable> taskQueue = taskExecutor.getThreadPoolExecutor().getQueue();
        Iterator<Runnable> iterator = taskQueue.iterator();
        while (iterator.hasNext()) {
            Thread thread = (Thread) iterator.next();
            taskQueue.remove(thread);
            Long executionId = Long.valueOf(thread.getName().split("-")[1]);
            // 当前执行的任务组或任务状态改为取消
            taskExecutionService.updateStatus(executionId, TaskConstants.EXECUTION_CANCEL);
            // 就绪的子任务状态改为未执行
            taskExecutionService.batchUpdateStatus(executionId, TaskConstants.EXECUTION_READY,
                    TaskConstants.EXECUTION_UN_EXECUTED);
        }

        //执行状态任务取消
        Iterator iter = ThreadManageInfo.threadHashMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Thread thread = (Thread) entry.getValue();
            logger.info("{} is shutdown", entry.getKey());
            Long executionId = Long.valueOf(thread.getName().split("-")[1]);
            // 当前执行的任务组或任务状态改为取消
            taskExecutionService.updateStatus(executionId, TaskConstants.EXECUTION_CANCEL);
            // 成功的子任务状态改为回滚
            taskExecutionService.batchUpdateStatus(executionId, TaskConstants.EXECUTION_SUCCESS,
                    TaskConstants.EXECUTION_ROLLBACK);
            // 执行中的子任务状态改为取消
            taskExecutionService.batchUpdateStatus(executionId, TaskConstants.EXECUTION_EXECUTING,
                    TaskConstants.EXECUTION_CANCEL);
            // 就绪的子任务状态改为未执行
            taskExecutionService.batchUpdateStatus(executionId, TaskConstants.EXECUTION_READY,
                    TaskConstants.EXECUTION_UN_EXECUTED);

        }

        if(callback != null){
            callback.run();
        }
    }

    @Override
    public void stop() {}
}
