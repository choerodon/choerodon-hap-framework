package io.choerodon.hap.function.dto;

import io.choerodon.mybatis.entity.BaseDTO;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 资源合并配置项DTO.
 *
 * @author zhizheng.yang@hand-china.com
 */
@Table(name = "sys_resource_customization")
public class ResourceCustomization extends BaseDTO {

    public static final String FIELD_RESOURCE_CUSTOMIZATION_ID = "resourceCustomizationId";
    public static final String FIELD_RESOURCE_ID = "resourceId";
    public static final String FIELD_URL = "url";
    public static final String FIELD_SEQUENCE = "sequence";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_ENABLE_FLAG = "enableFlag";

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resourceCustomizationId;

    @Column
    private Long resourceId;

    @Column
    @Length(max = 255)
    private String url;

    @Column
    private Integer sequence;

    @Column
    @Length(max = 240)
    private String description;

    @Column
    @Length(max = 1)
    private String enableFlag;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEnableFlag() {
        return enableFlag;
    }

    public void setEnableFlag(String enableFlag) {
        this.enableFlag = enableFlag;
    }

    public Long getResourceCustomizationId() {
        return resourceCustomizationId;
    }

    public void setResourceCustomizationId(Long resourceCustomizationId) {
        this.resourceCustomizationId = resourceCustomizationId;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }
}
