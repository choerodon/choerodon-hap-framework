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
import java.util.List;

@Table(name = "fnd_flex_rule")
public class FlexRule extends BaseDTO {

    public static final String FIELD_RULE_ID = "ruleId";
    public static final String FIELD_RULE_SET_ID = "ruleSetId";
    public static final String FIELD_RULE_CODE = "ruleCode";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_ENABLE_FLAG = "enableFlag";
    public static final String FIELD_FLEX_RULE_DETAIL_LIST = "flexRuleDetailList";


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ruleId;

    @NotNull
    private Long ruleSetId; //规则集id

    @NotEmpty
    @Length(max = 50)
    @Where
    private String ruleCode; //规则code

    @NotEmpty
    @Length(max = 80)
    private String description; //规则描述

    @NotEmpty
    @Length(max = 2)
    private String enableFlag; //是否启用

    @Transient
    private List<FlexRuleDetail> flexRuleDetailList;


    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleSetId(Long ruleSetId) {
        this.ruleSetId = ruleSetId;
    }

    public Long getRuleSetId() {
        return ruleSetId;
    }

    public void setRuleCode(String ruleCode) {
        this.ruleCode = StringUtils.trim(ruleCode);
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setEnableFlag(String enableFlag) {
        this.enableFlag = enableFlag;
    }

    public String getEnableFlag() {
        return enableFlag;
    }

    public List<FlexRuleDetail> getFlexRuleDetailList() {
        return flexRuleDetailList;
    }

    public void setFlexRuleDetailList(List<FlexRuleDetail> flexRuleDetailList) {
        this.flexRuleDetailList = flexRuleDetailList;
    }
}
