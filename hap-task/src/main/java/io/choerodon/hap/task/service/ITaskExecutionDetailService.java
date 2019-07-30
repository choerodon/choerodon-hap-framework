package io.choerodon.hap.task.service;

import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.task.dto.TaskExecutionDetail;
import io.choerodon.mybatis.service.IBaseService;
import org.apache.ibatis.annotations.Param;

/**
 * 任务执行记录-接口.
 *
 * @author lijian.yin@hand-china.com
 * @since 2017/11/6.
 **/

public interface ITaskExecutionDetailService extends IBaseService<TaskExecutionDetail>,
        ProxySelf<ITaskExecutionDetailService> {

    /**
     * 写入执行异常信息.
     *
     * @param taskExecutionDetail 任务/任务组执行 执行记录详情
     */
    void updateStacktrace(TaskExecutionDetail taskExecutionDetail);

    /**
     * 写入执行日志.
     *
     * @param taskExecutionDetail 任务/任务组执行 执行记录详情
     */
    void updateExecuteLog(TaskExecutionDetail taskExecutionDetail);

    /**
     * 获取执行日志.
     *
     * @param taskExecutionDetail 任务/任务组执行 执行记录详情
     * @return 任务/任务组执行 执行记录详情
     */
    TaskExecutionDetail getExecutionLog(TaskExecutionDetail taskExecutionDetail);

    /**
     * 根据执行Id查询执行详情.
     *
     * @param executionId 执行Id
     * @return 执行详情
     */
    TaskExecutionDetail selectByExecutionId(@Param("executionId") Long executionId);
}