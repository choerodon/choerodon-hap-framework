package io.choerodon.hap.account.dto;

import io.choerodon.hap.iam.infra.dto.RoleDTO;

import java.util.List;

/**
 * 当前登录用户的角色信息.
 *
 * @author qiang.zeng
 */
public class UserRoleInfo {
    private List<RoleDTO> activeUserRoles;
    private Long currentUserRoleId;
    private Boolean roleMergeFlag;

    public List<RoleDTO> getActiveUserRoles() {
        return activeUserRoles;
    }

    public void setActiveUserRoles(List<RoleDTO> activeUserRoles) {
        this.activeUserRoles = activeUserRoles;
    }

    public Long getCurrentUserRoleId() {
        return currentUserRoleId;
    }

    public void setCurrentUserRoleId(Long currentUserRoleId) {
        this.currentUserRoleId = currentUserRoleId;
    }

    public Boolean getRoleMergeFlag() {
        return roleMergeFlag;
    }

    public void setRoleMergeFlag(Boolean roleMergeFlag) {
        this.roleMergeFlag = roleMergeFlag;
    }
}
