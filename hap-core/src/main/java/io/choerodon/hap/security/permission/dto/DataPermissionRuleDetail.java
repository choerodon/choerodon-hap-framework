package io.choerodon.hap.security.permission.dto;

import io.choerodon.mybatis.common.query.Where;
import io.choerodon.mybatis.entity.BaseDTO;
import org.hibernate.validator.constraints.Length;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

/**
 * @author jialong.zuo@hand-china.com
 */
@Table(name = "sys_permission_rule_detail")
public class DataPermissionRuleDetail extends BaseDTO {

    public static final String FIELD_DETAIL_ID = "detailId";
    public static final String FIELD_RULE_ID = "ruleId";
    public static final String FIELD_PERMISSION_FIELD_VALUE = "permissionFieldValue";
    public static final String FIELD_PERMISSION_FIELD = "permissionField";
    public static final String FIELD_PERMISSION_FIELD_SQL_VALUE = "permissionFieldSqlValue";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long detailId;

    //MANGE CODE
    @NotNull
    @Where
    private Long ruleId;

    //安全性字段值
    @Length(max = 200)
    private String permissionFieldValue;

    //安全性sql字段值
    @Length(max = 2000)
    private String permissionFieldSqlValue;

    @Transient
    private String permissionFieldName;

    @Transient
    private String ruleCode;

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

    public void setPermissionFieldValue(String permissionFieldValue) {
        this.permissionFieldValue = permissionFieldValue;
    }

    public String getPermissionFieldValue() {
        return permissionFieldValue;
    }

    public void setPermissionFieldSqlValue(String permissionFieldSqlValue) {
        this.permissionFieldSqlValue = permissionFieldSqlValue;
    }

    public String getPermissionFieldSqlValue() {
        return permissionFieldSqlValue;
    }

    public String getPermissionFieldName() {
        return permissionFieldName;
    }

    public void setPermissionFieldName(String permissionFieldName) {
        this.permissionFieldName = permissionFieldName;
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }
}
