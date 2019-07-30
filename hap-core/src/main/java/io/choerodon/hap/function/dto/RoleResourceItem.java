package io.choerodon.hap.function.dto;

import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 角色权限组件DTO.
 *
 * @author njq.niu@hand-china.com
 * @since 2016年4月8日
 */
@Table(name = "sys_role_resource_item")
public class RoleResourceItem extends BaseDTO {

    private static final long serialVersionUID = 1L;
    public static final String FIELD_RSI_ID = "rsiId";
    public static final String FIELD_ROLE_ID = "roleId";
    public static final String FIELD_RESOURCE_ITEM_ID = "resourceItemId";

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rsiId;
    @Column
    private Long roleId;
    @Column
    private Long resourceItemId;

    public Long getRsiId() {
        return rsiId;
    }

    public void setRsiId(Long rsiId) {
        this.rsiId = rsiId;
    }

    public Long getRoleId() {
        return roleId;
    }


    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }


    public Long getResourceItemId() {
        return resourceItemId;
    }

    public void setResourceItemId(Long resourceItemId) {
        this.resourceItemId = resourceItemId;
    }
}