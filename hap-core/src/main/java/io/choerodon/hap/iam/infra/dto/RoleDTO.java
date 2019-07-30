package io.choerodon.hap.iam.infra.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.choerodon.hap.account.dto.User;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

import static io.choerodon.hap.iam.exception.ChoerodonRoleException.ERROR_ROLE_CODE_EMPTY;
import static io.choerodon.hap.iam.exception.ChoerodonRoleException.ERROR_ROLE_NAME_EMPTY;

/**
 * @author superlee
 * @since 2019-04-15
 */
@Table(name = "iam_role")
@MultiLanguage
public class RoleDTO extends BaseDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = ERROR_ROLE_NAME_EMPTY)
    @Size(min = 1, max = 64)
    @MultiLanguageField
    private String name;

    @NotEmpty(message = ERROR_ROLE_CODE_EMPTY)
    @Size(min = 1, max = 128)
    private String code;

    private String description;

    @JsonProperty(value = "level")
    private String resourceLevel;

    @Column(name = "is_enabled")
    private Boolean enabled;

    @Column(name = "is_modified")
    private Boolean modified;

    @Column(name = "is_enable_forbidden")
    private Boolean enableForbidden;

    @Column(name = "is_built_in")
    private Boolean builtIn;

    @Transient
    private List<PermissionDTO> permissions;

    @Transient
    private List<User> users;

    @Transient
    private Integer userCount;

    @Transient
    private List<Long> roleIds;
    /**
     * 用于员工管理 添加为用户 删除已分配的角色.
     */
    @Transient
    private Long memberRoleId;
    /**
     * 用于员工管理 添加为用户 查询未分配的角色.
     */
    @Transient
    private Long userId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getResourceLevel() {
        return resourceLevel;
    }

    public void setResourceLevel(String resourceLevel) {
        this.resourceLevel = resourceLevel;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getModified() {
        return modified;
    }

    public void setModified(Boolean modified) {
        this.modified = modified;
    }

    public Boolean getEnableForbidden() {
        return enableForbidden;
    }

    public void setEnableForbidden(Boolean enableForbidden) {
        this.enableForbidden = enableForbidden;
    }

    public Boolean getBuiltIn() {
        return builtIn;
    }

    public void setBuiltIn(Boolean builtIn) {
        this.builtIn = builtIn;
    }

    public List<PermissionDTO> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<PermissionDTO> permissions) {
        this.permissions = permissions;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public Integer getUserCount() {
        return userCount;
    }

    public void setUserCount(Integer userCount) {
        this.userCount = userCount;
    }

    public List<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }

    public Long getMemberRoleId() {
        return memberRoleId;
    }

    public void setMemberRoleId(Long memberRoleId) {
        this.memberRoleId = memberRoleId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
