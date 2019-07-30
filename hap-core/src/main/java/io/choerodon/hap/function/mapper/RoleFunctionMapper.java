package io.choerodon.hap.function.mapper;

import io.choerodon.hap.function.dto.RoleFunction;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 角色功能mapper.
 *
 * @author wuyichu
 */
public interface RoleFunctionMapper extends Mapper<RoleFunction> {
    /**
     * 查询角色功能.
     *
     * @param roleFunction 角色功能
     * @return 角色功能集合
     */
    List<RoleFunction> selectRoleFunctions(RoleFunction roleFunction);

    /**
     * 查询所有的角色资源.
     *
     * @return
     */
    List<Map<String, Object>> selectAllRoleResources();

    /**
     * 根据功能Id删除角色功能.
     *
     * @param functionId 功能Id
     * @return int
     */
    int deleteByFunctionId(Long functionId);

    /**
     * 根据角色Id删除角色功能.
     *
     * @param roleId 角色Id
     * @return int
     */
    int deleteByRoleId(Long roleId);

    /**
     * 根据角色Id和功能编码查询角色功能数量.
     *
     * @param roleId       角色Id
     * @param functionCode 功能编码
     * @return 角色功能数量
     */
    int selectCountByFunctionCode(@Param("roleId") Long roleId, @Param("functionCode") String functionCode);

    /**
     * 通过角色ID获取角色资源.
     *
     * @param roleId 角色Id
     * @return 角色资源
     */
    List<Map<String, Object>> selectRoleResources(Long roleId);
}