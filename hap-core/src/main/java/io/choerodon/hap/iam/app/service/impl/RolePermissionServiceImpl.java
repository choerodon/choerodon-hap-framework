package io.choerodon.hap.iam.app.service.impl;

import io.choerodon.hap.iam.app.service.RolePermissionService;
import io.choerodon.hap.iam.infra.dto.RolePermissionDTO;
import io.choerodon.hap.iam.infra.mapper.RolePermissionMapper;
import io.choerodon.mybatis.service.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 角色权限服务实现.
 *
 * @author qiang.zeng
 */
@Service
public class RolePermissionServiceImpl extends BaseServiceImpl<RolePermissionDTO> implements RolePermissionService {

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByRoleId(Long roleId) {
        RolePermissionDTO rolePermissionDTO = new RolePermissionDTO();
        rolePermissionDTO.setRoleId(roleId);
        return rolePermissionMapper.delete(rolePermissionDTO);
    }

    @Override
    public List<Long> queryExistingPermissionIdsByRoleIds(List<Long> roles) {
        return rolePermissionMapper.queryExistingPermissionIdsByRoleIds(roles);
    }

    @Override
    public void delete(RolePermissionDTO rolePermissionDTO) {
        rolePermissionMapper.delete(rolePermissionDTO);
    }

    @Override
    public List<RolePermissionDTO> select(RolePermissionDTO rolePermissionDTO) {
        return rolePermissionMapper.select(rolePermissionDTO);
    }
}
