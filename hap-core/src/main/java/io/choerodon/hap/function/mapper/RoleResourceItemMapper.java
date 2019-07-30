package io.choerodon.hap.function.mapper;

import io.choerodon.hap.function.dto.RoleResourceItem;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色权限组件Mapper.
 *
 * @author njq.niu@hand-china.com
 * @since 2016年4月8日
 */
public interface RoleResourceItemMapper extends Mapper<RoleResourceItem> {
    /**
     * 根据权限组件Id删除角色权限组件.
     *
     * @param resourceItemId 权限组件Id
     * @return int
     */
    int deleteByResourceItemId(@Param("resourceItemId") Long resourceItemId);

    /**
     * 根据角色Id和功能Id删除角色权限组件.
     *
     * @param roleId     角色Id
     * @param functionId 功能Id
     * @return int
     */
    int deleteByRoleIdAndFunctionId(@Param("roleId") Long roleId, @Param("functionId") Long functionId);

    /**
     * 插入角色权限组件.
     *
     * @param roleResourceItem
     * @return int
     */
    @Override
    int insert(RoleResourceItem roleResourceItem);

    /**
     * 根据角色Id查询角色权限组件.
     *
     * @param roleId 角色Id
     * @return 角色权限组件集合
     */
    List<RoleResourceItem> selectResourceItemsByRole(Long roleId);

    /**
     * 根据角色Id和权限组件Id查询角色权限组件.
     *
     * @param roleId         角色Id
     * @param resourceItemId 权限组件Id
     * @return 角色权限组件
     */
    RoleResourceItem selectByRoleIdAndResourceItemId(@Param("roleId") Long roleId, @Param("resourceItemId") Long resourceItemId);

    /**
     * 缓存查询全部角色权限组件.
     *
     * @return 角色权限组件集合
     */
    List<RoleResourceItem> selectForCache();
}