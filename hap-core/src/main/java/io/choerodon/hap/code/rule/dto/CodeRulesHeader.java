package io.choerodon.hap.code.rule.dto;

import io.choerodon.base.annotation.Children;
import io.choerodon.mybatis.common.query.Where;
import io.choerodon.mybatis.entity.BaseDTO;
import org.hibernate.validator.constraints.Length;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Table(name = "sys_code_rules_header")
public class CodeRulesHeader extends BaseDTO {

    public static final String FIELD_HEADER_ID = "headerId";
    public static final String FIELD_RULE_CODE = "ruleCode";
    public static final String FIELD_RULE_NAME = "ruleName";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_ENABLE_FLAG = "enableFlag";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Where
    @OrderBy("DESC")
    private Long headerId;

    @NotEmpty
    @Length(max = 50)
    @Where
    @OrderBy("ASC")
    private String ruleCode; // 编码规则CODE

    @Length(max = 50)
    @Where
    private String ruleName; // 名称

    @Length(max = 255)
    private String description; // 描述

    @Length(max = 5)
    private String enableFlag; // 启用标志

    @Transient
    @Children
    private List<CodeRulesLine> lines;

    public List<CodeRulesLine> getLines() {
        return lines;
    }

    public void setLines(List<CodeRulesLine> lines) {
        this.lines = lines;
    }

    public void setHeaderId(Long headerId) {
        this.headerId = headerId;
    }

    public Long getHeaderId() {
        return headerId;
    }

    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleName() {
        return ruleName;
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

}
