package io.choerodon.hap.function.dto;

import io.choerodon.mybatis.entity.BaseDTO;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 权限组件分配DTO.
 *
 * @author qiang.zeng@hand-china.com
 */
@Table(name = "sys_resource_item_assign")
public class ResourceItemAssign extends BaseDTO {

    private static final long serialVersionUID = 1L;
    public static final String FIELD_ASSIGN_ID = "assignId";
    public static final String FIELD_ASSIGN_TYPE = "assignType";
    public static final String FIELD_TYPE_ID = "typeId";
    public static final String FIELD_ELEMENT_ID = "elementId";
    public static final String FIELD_FUNCTION_ID = "functionId";
    public static final String FIELD_ENABLE = "enable";

    public static final String ASSIGN_TYPE_USER = "user";
    public static final String ASSIGN_TYPE_ROLE = "role";

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long assignId;

    @Column
    @Length(max = 10)
    private String assignType;

    @Column
    private Long typeId;

    @Column
    private Long elementId;

    @Column
    private Long functionId;

    @Column
    @Length(max = 1)
    private String enable;

    @Transient
    private Long roleId;

    public Long getAssignId() {
        return assignId;
    }

    public void setAssignId(Long assignId) {
        this.assignId = assignId;
    }

    public String getAssignType() {
        return assignType;
    }

    public void setAssignType(String assignType) {
        this.assignType = assignType;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public Long getElementId() {
        return elementId;
    }

    public void setElementId(Long elementId) {
        this.elementId = elementId;
    }

    public Long getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}
