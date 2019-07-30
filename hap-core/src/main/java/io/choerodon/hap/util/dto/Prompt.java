package io.choerodon.hap.util.dto;

import io.choerodon.mybatis.common.query.Comparison;
import io.choerodon.mybatis.common.query.Where;
import io.choerodon.mybatis.entity.BaseDTO;
import org.hibernate.validator.constraints.Length;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

/**
 * 描述维护.
 *
 * @author wuyichu
 * @since 2016/6/9.
 */
@Table(name = "sys_prompts")
public class Prompt extends BaseDTO {

    private static final long serialVersionUID = 2856108923186548186L;

    public static final String FIELD_PROMPT_ID = "promptId";
    public static final String FIELD_PROMPT_CODE = "promptCode";
    public static final String FIELD_LANG = "lang";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_MODULE_CODE = "moduleCode";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @OrderBy("DESC")
    private Long promptId;

    @NotEmpty
    @Where(comparison = Comparison.LIKE)
    @Length(max = 255)
    @OrderBy("ASC")
    @Column
    private String promptCode;

    @NotEmpty
    @Length(max = 10)
    @Where
    @Column
    private String lang;

    @NotEmpty
    @Where(comparison = Comparison.LIKE)
    @Length(max = 240)
    @Column
    private String description;

    @NotEmpty
    @Where
    @Length(max = 50)
    @Column
    private String moduleCode;

    public Long getPromptId() {
        return promptId;
    }

    public void setPromptId(Long promptId) {
        this.promptId = promptId;
    }

    public String getPromptCode() {
        return promptCode;
    }

    public void setPromptCode(String promptCode) {
        this.promptCode = StringUtils.trimWhitespace(promptCode);
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = StringUtils.trimWhitespace(lang);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }
}