package io.choerodon.hap.account.mapper;

import io.choerodon.hap.account.dto.Role;
import io.choerodon.hap.account.dto.UserRole;
import io.choerodon.mybatis.common.Mapper;

import java.util.List;

/**
 * 用户角色Mapper.
 *
 * @author xiawang.liu@hand-china.com
 */
public interface UserRoleMapper extends Mapper<UserRole> {

    /**
     * 根据用户ID删除用户角色分配.
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteByUserId(Long userId);

    /**
     * 根据用户ID和角色ID删除用户角色分配.
     *
     * @param record 用户角色
     * @return 影响行数
     */
    int deleteByRecord(UserRole record);

    /**
     * 根据角色ID删除用户角色.
     *
     * @param roleId 角色ID
     * @return 影响行数
     */
    int deleteByRoleId(Long roleId);

    List<Role> selectUserRoles(UserRole userRole);
}