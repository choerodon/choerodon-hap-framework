package io.choerodon.hap.flexfield.dto;

import io.choerodon.mybatis.common.query.Where;
import io.choerodon.mybatis.entity.BaseDTO;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Table(name = "fnd_flex_rule_set")
public class FlexRuleSet extends BaseDTO {

    public static final String FIELD_RULE_SET_ID = "ruleSetId";
    public static final String FIELD_RULE_SET_CODE = "ruleSetCode";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_MODEL_ID = "modelId";
    public static final String FIELD_ENABLE_FLAG = "enableFlag";
    public static final String FIELD_MODEL_NAME = "modelName";


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ruleSetId;

    @NotEmpty
    @Length(max = 50)
    @Where
    private String ruleSetCode; //规则集CODE

    @Length(max = 50)
    private String description; //规则集描述

    @NotNull
    private Long modelId; //弹性域模板id

    @NotEmpty
    @Length(max = 2)
    private String enableFlag; //是否启用

    @Transient
    private String modelName;


    public void setRuleSetId(Long ruleSetId) {
        this.ruleSetId = ruleSetId;
    }

    public Long getRuleSetId() {
        return ruleSetId;
    }

    public void setRuleSetCode(String ruleSetCode) {
        this.ruleSetCode = StringUtils.trim(ruleSetCode);
    }

    public String getRuleSetCode() {
        return ruleSetCode;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public Long getModelId() {
        return modelId;
    }

    public void setEnableFlag(String enableFlag) {
        this.enableFlag = enableFlag;
    }

    public String getEnableFlag() {
        return enableFlag;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
}
