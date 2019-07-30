package io.choerodon.hap.util.dto;

import io.choerodon.base.annotation.ExcelJoinColumn;
import io.choerodon.mybatis.annotation.EnableExtensionAttribute;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.entity.BaseDTO;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;

/**
 * CodeValueDTO.
 *
 * @author runbai.chen
 */
@MultiLanguage
@EnableExtensionAttribute
@Table(name = "sys_code_value_b")
public class CodeValue extends BaseDTO {

    private static final long serialVersionUID = 7078027762943933806L;
    /**
     * 快速编码类型.
     */
    @ExcelJoinColumn(JoinTable = Code.class, JoinColumn = Code.FIELD_CODE_ID, AlternateColumn = Code.FIELD_CODE)
    private Long codeId;

    /**
     * ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @OrderBy("DESC")
    private Long codeValueId;


    /**
     * 快码编码值描述.
     */
    @MultiLanguageField
    @Length(max = 250)
    private String description;

    /**
     * 快码编码意义描述.
     */
    @MultiLanguageField
    @NotEmpty
    @Length(max = 150)
    private String meaning;

    /**
     * 快速编码code.
     */
    @NotEmpty
    @Length(max = 150)
    private String value;

    private Long orderSeq;

    @Length(max = 255)
    private String tag;

    @Length(max = 1)
    private String enabledFlag;

    @Column
    private Long parentCodeValueId;

    @Column
    private String parentCodeValue;

    @Transient
    private String parentCodeValueMeaning;


    public String getParentCodeValue() {
        return parentCodeValue;
    }

    public void setParentCodeValue(String parentCodeValue) {
        this.parentCodeValue = parentCodeValue;
    }

    public String getParentCodeValueMeaning() {
        return parentCodeValueMeaning;
    }

    public void setParentCodeValueMeaning(String parentCodeValueMeaning) {
        this.parentCodeValueMeaning = parentCodeValueMeaning;
    }

    public Long getParentCodeValueId() {
        return parentCodeValueId;
    }

    public void setParentCodeValueId(Long parentCodeValueId) {
        this.parentCodeValueId = parentCodeValueId;
    }

    public Long getCodeId() {
        return codeId;
    }

    public Long getCodeValueId() {
        return codeValueId;
    }

    public String getDescription() {
        return description;
    }

    public String getMeaning() {
        return meaning;
    }

    public String getValue() {
        return value;
    }

    public void setCodeId(Long codeId) {
        this.codeId = codeId;
    }

    public void setCodeValueId(Long codeValueId) {
        this.codeValueId = codeValueId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public void setValue(String value) {
        this.value = value == null ? null : value.trim();
    }

    public Long getOrderSeq() {
        return orderSeq;
    }

    public void setOrderSeq(Long orderSeq) {
        this.orderSeq = orderSeq;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(String enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

}

