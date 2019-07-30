package io.choerodon.hap.security.permission.dto;

import io.choerodon.mybatis.common.query.JoinColumn;
import io.choerodon.mybatis.common.query.JoinOn;
import io.choerodon.mybatis.common.query.JoinTable;
import io.choerodon.mybatis.common.query.Where;
import io.choerodon.mybatis.entity.BaseDTO;
import org.hibernate.validator.constraints.Length;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.criteria.JoinType;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author jialong.zuo@hand-china.com
 */
@Table(name = "sys_permission_table_rule")
public class DataPermissionTableRule extends BaseDTO {

    public static final String FIELD_TABLE_RULE_ID = "tableRuleId";
    public static final String FIELD_TABLE_ID = "tableId";
    public static final String FIELD_TABLE_FIELD = "tableField";
    public static final String FIELD_RULE_ID = "ruleId";


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tableRuleId;

    //table Id
    @NotNull
    @Where
    private Long tableId;

    //关联表字段
    @NotEmpty
    @Length(max = 250)
    private String tableField;

    //规则ID
    @NotNull
    @JoinTable(name = "permissionRuleJoin", joinMultiLanguageTable = false, target = DataPermissionRule.class, type = JoinType.LEFT, on = {@JoinOn(joinField = DataPermissionRule.FIELD_RULE_ID)})
    private Long ruleId;

    @Transient
    @JoinColumn(joinName = "permissionRuleJoin", field = DataPermissionRule.FIELD_RULE_NAME)
    private String ruleName;

    @Transient
    private String tableName;


    public void setTableRuleId(Long tableRuleId) {
        this.tableRuleId = tableRuleId;
    }

    public Long getTableRuleId() {
        return tableRuleId;
    }

    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }

    public Long getTableId() {
        return tableId;
    }

    public void setTableField(String tableField) {
        this.tableField = tableField;
    }

    public String getTableField() {
        return tableField;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }
}
