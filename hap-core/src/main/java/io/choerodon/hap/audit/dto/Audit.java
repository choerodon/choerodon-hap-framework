package io.choerodon.hap.audit.dto;

import io.choerodon.mybatis.annotation.EnableExtensionAttribute;
import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.Table;

/**
 * 审计DTO.
 *
 * @author xiawang.liu
 */
@EnableExtensionAttribute
@Table(name = "sys_audit_entity")
public class Audit extends BaseDTO {

    private static final long serialVersionUID = 1L;

    private Long entityId;

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    /**
     * 审计实体Code.
     */
    private String entityCode;

    /**
     * 审计备注.
     */
    private String description;

    /**
     * 审计开关.
     */
    private String auditFlag;

    public String getEntityCode() {
        return entityCode;
    }

    public void setEntityCode(String entityCode) {
        this.entityCode = entityCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuditFlag() {
        return auditFlag;
    }

    public void setAuditFlag(String auditFlag) {
        this.auditFlag = auditFlag;
    }

}