package io.choerodon.hap.flexfield.dto;

import io.choerodon.mybatis.entity.BaseDTO;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Table(name = "fnd_flex_rule_detail")
public class FlexRuleDetail extends BaseDTO {

    public static final String FIELD_DETAIL_ID = "detailId";
    public static final String FIELD_RULE_ID = "ruleId";
    public static final String FIELD_FIELD_NAME = "fieldName";
    public static final String FIELD_FIELD_TYPE = "fieldType";
    public static final String FIELD_FIELD_VALUE = "fieldValue";


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long detailId;

    @NotNull
    private Long ruleId; //规则id

    @NotEmpty
    @Length(max = 50)
    private String fieldName; //规则name

    @NotEmpty
    @Length(max = 500)
    private String fieldType;

    @NotEmpty
    @Length(max = 50)
    private String fieldValue; //规则value


    public void setDetailId(Long detailId) {
        this.detailId = detailId;
    }

    public Long getDetailId() {
        return detailId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = StringUtils.trim(fieldName);
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = StringUtils.trim(fieldType);
    }
}
