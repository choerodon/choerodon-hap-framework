package io.choerodon.hap.task.mapper;

import io.choerodon.hap.task.dto.TaskExecutionDetail;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 任务/任务组执行记录详情 mapper.
 *
 * @author peng.jiang@hand-china.com
 * @since 2017/11/16.
 **/

public interface TaskExecutionDetailMapper extends Mapper<TaskExecutionDetail> {

    /**
     * 更新错误堆栈
     *
     * @param taskExecutionDetail 执行记录详情
     */
    void updateStacktrace(TaskExecutionDetail taskExecutionDetail);

    /**
     * 更新操作日志
     *
     * @param taskExecutionDetail 执行记录详情
     */
    void updateExecuteLog(TaskExecutionDetail taskExecutionDetail);

    /**
     * 获取操作日志.
     *
     * @param taskExecutionDetail 执行记录详情
     * @return 执行记录详情
     */
    TaskExecutionDetail getExecutionLog(TaskExecutionDetail taskExecutionDetail);

    /**
     * 通过执行记录ID删除执行记录详情.
     *
     * @param executionId 执行记录ID
     * @return 删除记录数量
     */
    int deleteByExecutionId(@Param("executionId") Long executionId);

    /**
     * 根据执行Id查询执行详情.
     *
     * @param executionId 执行Id
     * @return 执行详情
     */
    TaskExecutionDetail selectByExecutionId(@Param("executionId") Long executionId);
}