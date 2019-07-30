package io.choerodon.hap.task.service;

import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.iam.infra.dto.RoleDTO;
import io.choerodon.hap.task.dto.TaskAssign;
import io.choerodon.mybatis.service.IBaseService;
import io.choerodon.web.core.IRequest;

import java.util.List;

/**
 * 任务授权Service.
 *
 * @author lijian.yin@hand-china.com
 * @since 2017/11/6.
 **/

public interface ITaskAssignService extends IBaseService<TaskAssign>, ProxySelf<ITaskAssignService> {

    /**
     * 查询角色列表.
     *
     * @param request   IRequest
     * @param condition 任务分配条件
     * @return 任务
     */
    List<TaskAssign> query(IRequest request, TaskAssign condition);

    /**
     * 查询未绑定的角色列表.
     *
     * @param request IRequest
     * @param idList  角色ID集合
     * @return 角色集合
     */
    List<RoleDTO> queryUnbound(IRequest request, List<String> idList);

    /**
     * 查询该用户下权限下的任务Id.
     *
     * @param iRequest IRequest
     * @param isAdmin  是否是管理员
     * @return 任务ID
     */
    List<Long> queryTaskId(IRequest iRequest, boolean isAdmin);

}