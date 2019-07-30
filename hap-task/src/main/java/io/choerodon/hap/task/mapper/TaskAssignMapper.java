package io.choerodon.hap.task.mapper;

import io.choerodon.hap.iam.infra.dto.RoleDTO;
import io.choerodon.hap.task.dto.TaskAssign;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 任务权限 Mapper.
 *
 * @author lijian.yin@hand-china.com
 * @since 2017/11/15.
 **/

public interface TaskAssignMapper extends Mapper<TaskAssign> {

    /**
     * 根据任务ID查询授权信息.
     *
     * @param taskAssign 任务授权类（含任务ID）
     * @return 任务授权集合
     */
    List<TaskAssign> query(TaskAssign taskAssign);

    /**
     * 查询没有绑定的角色.
     *
     * @param idList 角色ID集合
     * @return 角色集合
     */
    List<RoleDTO> queryUnbound(@Param("idList") List<String> idList);

    /**
     * 查询用户角色下的任务ID.
     *
     * @param ruleIds     角色ID集合
     * @param currentDate 当前日期
     * @param isAdmin     是否是管理员
     * @return 任务ID集合
     */
    List<Long> queryTaskId(@Param("array") Long[] ruleIds, @Param("currentDate") Date currentDate, @Param("isAdmin") boolean isAdmin);

    /**
     * 删除任务时删除授权.
     *
     * @param taskId 任务Id
     * @return 影响行数
     */
    int deleteByTaskId(Long taskId);

}