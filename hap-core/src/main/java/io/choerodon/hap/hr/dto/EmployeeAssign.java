package io.choerodon.hap.hr.dto;

import io.choerodon.base.util.BaseConstants;
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

/**
 * 员工分配对象.
 *
 * @author shengyang.zhou@hand-china.com
 */
@Table(name = "hr_employee_assign")
public class EmployeeAssign extends BaseDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Where
    private Long assignId;

    @Where
    private Long employeeId;

    @JoinTable(name = "positionJoin", joinMultiLanguageTable = true, target = Position.class, type = JoinType.LEFT, on = {@JoinOn(joinField = Position.FIELD_POSITION_ID), @JoinOn(joinField = BaseDTO.FIELD_LANG, joinExpression = BaseConstants.PLACEHOLDER_LOCALE)})
    private Long positionId;

    @Transient
    @JoinColumn(joinName = "positionJoin", field = Position.FIELD_NAME)
    private String positionName;

    @Transient
    private String unitName;

    @Length(max = 1)
    private String primaryPositionFlag;

    @Length(max = 1)
    private String enabledFlag;

    public Long getAssignId() {
        return assignId;
    }

    public void setAssignId(Long assignId) {
        this.assignId = assignId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public Long getPositionId() {
        return positionId;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }

    public String getPrimaryPositionFlag() {
        return primaryPositionFlag;
    }

    public void setPrimaryPositionFlag(String primaryPositionFlag) {
        this.primaryPositionFlag = primaryPositionFlag;
    }

    public String getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(String enabledFlag) {
        this.enabledFlag = enabledFlag;
    }
}
