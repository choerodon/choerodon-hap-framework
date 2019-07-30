package io.choerodon.hap.task.service;

import io.choerodon.hap.task.exception.TaskInterruptException;
import io.choerodon.hap.task.info.ExecutionInfo;

/**
 * 并发请求接口.
 *
 * @author lijian.yin@hand-china.com on 2017/11/6.
 */
public interface ITask {

    /**
     * 并发请求接口.
     *
     * @param executionInfo 数据传输-任务执行记录
     * @throws Exception 执行异常
     */
    void execute(ExecutionInfo executionInfo) throws Exception;


    /**
     * 可在此方法中执行耗时较长的代码段
     * 执行前后会判断当前线程是否中断.
     *
     * @param callback 回调
     * @param <T>      类型
     * @return 返回值
     * @throws Exception
     */
    default <T> T safeExecute(ITaskCallback<T> callback) throws Exception {
        if (Thread.currentThread().isInterrupted()) {
            throw new TaskInterruptException(TaskInterruptException.CODE_INTERRUPT_ERROR, TaskInterruptException.MSG_INTERRUPT);
        }
        T t = callback.doInTask();
        if (Thread.currentThread().isInterrupted()) {
            throw new TaskInterruptException(TaskInterruptException.CODE_INTERRUPT_ERROR, TaskInterruptException.MSG_INTERRUPT);
        }
        return t;
    }


}
