package io.choerodon.hap.iam.app.service;

import io.choerodon.hap.account.exception.RoleException;
import io.choerodon.hap.account.exception.UserException;
import io.choerodon.hap.iam.api.dto.RoleAssignmentDeleteDTO;
import io.choerodon.hap.iam.exception.MemberRoleException;
import io.choerodon.hap.iam.infra.dto.MemberRoleDTO;
import io.choerodon.mybatis.service.IBaseService;

import java.util.List;

/**
 * @author superlee
 * @author wuguokai
 * @author zmf
 */
public interface RoleMemberService extends IBaseService<MemberRoleDTO> {

    /**
     * 批量分配用户角色.
     *
     * @param memberIds         用户Id集合
     * @param memberRoleDTOList 用户角色列表
     * @return 用户角色列表
     */
    List<MemberRoleDTO> saveUserRolesByMemberIds(List<Long> memberIds, List<MemberRoleDTO> memberRoleDTOList) throws MemberRoleException, UserException;

    /**
     * 批量删除用户所有角色.
     *
     * @param roleAssignmentDeleteDTO 角色分配删除DTO
     * @throws MemberRoleException 用户角色删除异常
     */
    void deleteAllUserRoles(RoleAssignmentDeleteDTO roleAssignmentDeleteDTO) throws MemberRoleException;

    /**
     * 根据用户Id和角色Id查询用户角色是否存在.
     *
     * @param userId 用户Id
     * @param roleId 角色Id
     * @throws RoleException 账号角色不存在异常
     */
    void checkUserRoleExists(Long userId, Long roleId) throws RoleException;
}
