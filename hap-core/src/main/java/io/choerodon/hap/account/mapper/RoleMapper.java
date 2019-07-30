package io.choerodon.hap.account.mapper;

import io.choerodon.hap.account.dto.Role;
import io.choerodon.hap.account.dto.RoleExt;
import io.choerodon.hap.account.dto.User;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色Mapper.
 *
 * @author shengyang.zhou@hand-china.com
 */
public interface RoleMapper extends Mapper<Role> {

    /**
     * 根据用户Id查询角色.
     *
     * @param userId 用户ID
     * @return 角色集合
     */
    List<Role> selectUserRolesByUserId(Long userId);

    /**
     * 根据用户Id获取角色.
     *
     * @param userId 用户ID
     * @return 角色集合
     */
    List<Role> selectUserRoles(Long userId);


    /**
     * 查询用户不具有的角色.
     *
     * @param roleExt 角色扩展对象
     * @return 角色集合
     */
    List<Role> selectRoleNotUserRoles(RoleExt roleExt);

    /**
     * 查询用户拥有的角色.
     *
     * @param user 用户
     * @return 角色集合
     */
    List<Role> selectByUser(User user);

    /**
     * 根据用户查询角色.
     *
     * @param user 用户
     * @return 角色集合
     */
    List<Role> selectRolesByUserWithoutLang(User user);

    /**
     * 根据用户ID和角色ID查询用户角色分配数量.
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 用户角色分配数量
     */
    int selectUserRoleCount(@Param("userId") Long userId, @Param("roleId") Long roleId);

    /**
     * 根据用户ID查询用户启用状态的角色.
     *
     * @param userId 用户ID
     * @return 角色集合
     */
    List<Role> selectUserActiveRoles(Long userId);

    /**
     * 查询所有启用状态的角色.
     *
     * @return 角色集合
     */
    List<Role> selectActiveRoles();
}
