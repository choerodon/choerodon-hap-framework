package io.choerodon.hap.task.service;

import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.core.annotation.StdWho;
import io.choerodon.hap.task.dto.TaskExecution;
import io.choerodon.mybatis.service.IBaseService;
import io.choerodon.web.core.IRequest;

import java.util.List;

/**
 * 任务执行处理-接口.
 *
 * @author peng.jiang@hand-china.com
 * @since 2017/11/8.
 **/
public interface ITaskExecutionService extends IBaseService<TaskExecution>, ProxySelf<ITaskExecutionService> {

    /**
     * 查询执行记录列表.
     *
     * @param iRequest IRequest
     * @param dto      执行记录
     * @param isAdmin  是否是管理员
     * @param page     页码
     * @param pageSize 每页显示数量
     * @return 执行记录集合
     */
    List<TaskExecution> queryExecutions(IRequest iRequest, TaskExecution dto, boolean isAdmin, int page, int pageSize);

    /**
     * 查看任务组执行详情.
     *
     * @param iRequest      IRequest
     * @param taskExecution 任务执行记录
     * @return 任务执行记录详情
     */
    List<TaskExecution> queryExecutionGroup(IRequest iRequest, TaskExecution taskExecution);

    /**
     * 查看任务执行详情.
     *
     * @param iRequest      IRequest
     * @param taskExecution 任务操作异常
     * @return 执行记录集合
     */
    List<TaskExecution> queryExecutionDetail(IRequest iRequest, TaskExecution taskExecution);

    /**
     * 插入执行记录.
     *
     * @param iRequest      IRequest
     * @param taskExecution 任务操作异常
     */
    void insertExecution(IRequest iRequest, TaskExecution taskExecution);

    /**
     * 修改任务执行记录状态.
     *
     * @param executionId 执行记录Id
     * @param status      执行状态
     */
    void updateStatus(Long executionId, @StdWho String status);

    /**
     * 取消任务.
     *
     * @param dto 执行记录
     * @return 取消结果
     */
    boolean cancelExecute(TaskExecution dto);

    /**
     * 修改任务组中子任务执行记录状态
     * 子任务执行记录状态为 befStatus 全部改为 aftStatus
     *
     * @param executionId 任务组执行Id
     * @param befStatus   子任务修改之前状态
     * @param aftStatus   子任务修改之后状态
     */
    void batchUpdateStatus(Long executionId, String befStatus, String aftStatus);

    /**
     * 生成文件内容.
     *
     * @param taskExecution 任务执行信息
     * @return 输出执行日志
     */
    String generateString(TaskExecution taskExecution);
}