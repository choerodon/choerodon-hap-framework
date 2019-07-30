package io.choerodon.hap.task.service;

import io.choerodon.hap.task.info.TaskDataInfo;

/**
 * 任务执行处理-接口.
 *
 * @author lijian.yin@hand-china.com
 * @since 2017/11/6.
 **/

public interface IExecuteService {

    /**
     * 任务执行.
     *
     * @param taskData  数据传输类-任务/任务组
     */
    void taskExecute(TaskDataInfo taskData);

}
