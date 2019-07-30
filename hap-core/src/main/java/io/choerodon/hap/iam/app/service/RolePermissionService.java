package io.choerodon.hap.iam.app.service;

import io.choerodon.hap.iam.infra.dto.RolePermissionDTO;
import io.choerodon.mybatis.service.IBaseService;

import java.util.List;

/**
 * 角色权限服务.
 *
 * @author qiang.zeng
 */
public interface RolePermissionService extends IBaseService<RolePermissionDTO> {
    /**
     * 根据角色Id删除角色权限.
     *
     * @param roleId 角色Id
     * @return 执行结果
     */
    int deleteByRoleId(Long roleId);

    List<Long> queryExistingPermissionIdsByRoleIds(List<Long> roles);

    RolePermissionDTO insert(RolePermissionDTO rolePermissionDTO);

    void delete(RolePermissionDTO rolePermissionDTO);

    List<RolePermissionDTO> select(RolePermissionDTO rolePermissionDTO);
}
