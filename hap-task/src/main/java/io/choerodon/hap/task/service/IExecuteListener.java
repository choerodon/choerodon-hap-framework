package io.choerodon.hap.task.service;

import io.choerodon.hap.task.info.TaskDataInfo;

/**
 * 任务执行处理-接口.
 *
 * @author peng.jiang@hand-china.com
 * @since 2017/11/13.
 **/

public interface IExecuteListener {

    /**
     * 执行前.
     *
     * @param taskDataInfo 数据传输类-任务/任务组
     */
    void before(TaskDataInfo taskDataInfo);

    /**
     * 执行后.
     *
     * @param taskDataInfo 数据传输类-任务/任务组
     */
    void after(TaskDataInfo taskDataInfo);

    /**
     * 异常.
     *
     * @param e            异常
     * @param taskDataInfo 数据传输类-任务/任务组
     */
    void doException(Exception e, TaskDataInfo taskDataInfo);

    /**
     * Finally.
     *
     * @param taskDataInfo 数据传输类-任务/任务组
     */
    void doFinally(TaskDataInfo taskDataInfo);


    /**
     * 执行排序,用户实现类默认执行顺序为99
     * 系统的默认实现类返回值为1，用户实现类返回值必须大于1.
     *
     * @return {@link #before} {@link #after} {@link #doException} 按order大小正序执行
     * {@link #doFinally} 按order大小逆序执行
     */
    default int getOrder() {
        return 99;
    }

}
