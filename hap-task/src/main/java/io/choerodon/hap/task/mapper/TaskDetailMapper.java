package io.choerodon.hap.task.mapper;

import io.choerodon.mybatis.common.Mapper;
import io.choerodon.hap.task.dto.TaskDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 任务/任务组 mapper.
 *
 * @author lijian.yin@hand-china.com
 * @since 2017/11/15.
 **/

public interface TaskDetailMapper extends Mapper<TaskDetail> {

    /**
     * 查找子任务列表.
     *
     * @param idList 任务组ID
     * @return 任务集合
     */
    List<TaskDetail> queryChildrenByPrimaryKey(@Param("idList") List<String> idList);

    /**
     * 查找未绑定的任务.
     *
     * @param taskDetail 查询条件
     * @param idList     已绑定的任务ID列表
     * @return 任务集合
     */
    List<TaskDetail> queryUnboundTasks(@Param("taskDetail") TaskDetail taskDetail, @Param("idList") List<String> idList);

    /**
     * 查询任务组信息（包括任务参数）.
     *
     * @param taskDetail 任务
     * @param idList     任务ID集合
     * @return 任务集合
     */
    List<TaskDetail> queryTask(@Param("taskDetail") TaskDetail taskDetail, @Param("idList") List<String> idList);

    /**
     * 条件查询任务/任务组.
     *
     * @param taskDetail 任务
     * @param taskIds    任务ID集合
     * @return 任务集合
     */
    List<TaskDetail> queryByTask(@Param("taskDetail") TaskDetail taskDetail, @Param("taskIds") List<Long> taskIds);

}