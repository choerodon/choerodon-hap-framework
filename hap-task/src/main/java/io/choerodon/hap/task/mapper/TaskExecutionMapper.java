package io.choerodon.hap.task.mapper;

import io.choerodon.mybatis.common.Mapper;
import io.choerodon.hap.task.dto.TaskExecution;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 任务/任务组执行记录 Mapper.
 *
 * @author peng.jiang@hand-china.com
 * @since 2017/11/16.
 **/

public interface TaskExecutionMapper extends Mapper<TaskExecution> {

    /**
     * 查询执行记录列表.
     *
     * @param taskExecution 任务执行记录
     * @param isAdmin       是否是管理员
     * @return 任务执行记录集合
     */
    List<TaskExecution> queryExecutions(@Param("taskExecution") TaskExecution taskExecution,
                                        @Param("isAdmin") boolean isAdmin);

    List<TaskExecution> testQueryExecutions(@Param("taskExecution") TaskExecution taskExecution,
                                            @Param("isAdmin") boolean isAdmin);
    /**
     * 查询任务组执行详情.
     *
     * @param taskExecution 任务执行记录
     * @return 任务组执行记录
     */
    List<TaskExecution> queryExecutionGroup(TaskExecution taskExecution);

    /**
     * 查看任务执行详情
     *
     * @param taskExecution 任务执行记录
     * @return 任务执行记录集合
     */
    List<TaskExecution> queryExecutionDetail(TaskExecution taskExecution);

    /**
     * 修改任务执行记录状态.
     *
     * @param executionId 任务执行记录ID
     * @param status      执行状态
     */
    void updateStatus(@Param("executionId") Long executionId, @Param("status") String status);

    /**
     * 查找任务/任务组的执行记录ID.
     *
     * @param taskId 任务/任务组Id
     * @return 任务执行记录集合
     */
    List<TaskExecution> selectByTaskId(@Param("taskId") Long taskId);

    /**
     * 查找执行记录ID及查找子记录ID.
     *
     * @param executionId 执行记录ID
     * @return 任务执行记录集合
     */
    List<TaskExecution> selectByExeId(@Param("executionId") Long executionId);

    /**
     * 修改任务执行记录状态.
     *
     * @param executionId 执行记录ID
     * @param befStatus   执行记录之前的状态
     * @param aftStatus   修改的执行记录状态
     */
    void batchUpdateStatus(@Param("executionId") Long executionId, @Param("befStatus") String befStatus,
                           @Param("aftStatus") String aftStatus);

    /**
     * 获取任务或任务组的上次执行时间.
     *
     * @param taskId 任务/任务组ID
     * @return 执行记录
     */
    TaskExecution getLastExecuteDate(Long taskId);

}