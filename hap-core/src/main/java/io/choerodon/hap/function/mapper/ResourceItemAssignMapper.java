package io.choerodon.hap.function.mapper;

import io.choerodon.hap.function.dto.ResourceItemAssign;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 权限组件分配Mapper.
 *
 * @author qiang.zeng@hand-china.com
 */
public interface ResourceItemAssignMapper extends Mapper<ResourceItemAssign> {
    /**
     * 根据角色Id和功能Id删除权限组件分配.
     *
     * @param roleId     角色Id
     * @param functionId 功能Id
     * @return int
     */
    int deleteByRoleIdAndFunctionId(@Param("roleId") Long roleId, @Param("functionId") Long functionId);

    /**
     * 根据用户Id和功能Id删除权限组件分配.
     *
     * @param userId     用户Id
     * @param functionId 功能Id
     * @return int
     */
    int deleteByUserIdAndFunctionId(@Param("userId") Long userId, @Param("functionId") Long functionId);

    /**
     * 根据权限组件元素Id删除权限组件分配.
     *
     * @param elementId 权限组件元素Id
     * @return int
     */
    int deleteByElementId(@Param("elementId") Long elementId);

    /**
     * 根据权限组件Id删除权限组件分配.
     *
     * @param resourceItemId 权限组件Id
     * @return int
     */
    int deleteByResourceItemId(@Param("resourceItemId") Long resourceItemId);
}