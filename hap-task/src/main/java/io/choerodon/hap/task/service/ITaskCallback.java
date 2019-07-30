package io.choerodon.hap.task.service;

/**
 * @author peng.jiang@hand-china.com
 * @since 2017/12/1
 **/
public interface ITaskCallback<T> {
    /**
     * 关键代码执行.
     *
     * @return 返回值
     * @throws Exception 执行异常
     */
    T doInTask() throws Exception;
}
