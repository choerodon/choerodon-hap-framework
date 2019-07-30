package io.choerodon.hap.system.dto;

import io.choerodon.mybatis.entity.BaseDTO;
import org.hibernate.validator.constraints.Length;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

@Table(name = "sys_forms")
public class Form extends BaseDTO {

    public static final String FIELD_FORM_ID = "formId";
    public static final String FIELD_CODE = "code";
    public static final String FIELD_CONTENT = "content";
    public static final String IS_PUBLISH = "isPublish";

    public Form() {
    }

    ;

    public Form(String code) {
        this.code = code;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long formId;

    @NotEmpty
    @Length(max = 255)
    private String code;

    @Length(max = 65535)
    private String content;

    @Length(max = 255)
    private String description;

    private String isPublish;

    public String getIsPublish() {
        return isPublish;
    }

    public void setIsPublish(String isPublish) {
        this.isPublish = isPublish;
    }

    public void setFormId(Long formId) {
        this.formId = formId;
    }

    public Long getFormId() {
        return formId;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
