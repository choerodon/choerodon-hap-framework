package io.choerodon.hap.code.rule.dto;

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
import java.util.Date;

@Table(name = "sys_code_rules_line")
public class CodeRulesLine extends BaseDTO {

    public static final String FIELD_LINE_ID = "lineId";
    public static final String FIELD_HEADER_ID = "headerId";
    public static final String FIELD_FILED_TYPE = "filedType";
    public static final String FIELD_FILED_VALUE = "filedValue";
    public static final String FIELD_FIELD_SEQUENCE = "fieldSequence";
    public static final String FIELD_DATE_MASK = "dateMask";
    public static final String FIELD_SEQ_LENGTH = "seqLength";
    public static final String FIELD_START_VALUE = "startValue";
    public static final String FIELD_CURRENT_VALUE = "currentValue";
    public static final String FIELD_REST_FREQUENCY = "resetFrequency";
    public static final String FIELD_REST_DATE = "resetDate";
    public static final String FIELD_STEP_NUMBER = "stepNumber";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lineId;

    @Where
    private Long headerId; // 编码头ID

    @NotEmpty
    @Length(max = 10)
    private String filedType; // 编码段类型

    @Length(max = 100)
    private String filedValue; // 编码段值

    @OrderBy
    private Long fieldSequence; // 序号

    @Length(max = 50)
    private String dateMask; // 日期掩码

    private Long seqLength; // 序列长度

    private Long startValue; // 序列开始值

    private Long currentValue; // 序列号段当前值

    @Length(max = 10)
    private String resetFrequency; // 重置频率

    private Date resetDate; // 上次重置日期

    private Long stepNumber; // 索引更新步长
    //编码code
    @Transient
    private String ruleCode;

    public void setLineId(Long lineId) {
        this.lineId = lineId;
    }

    public Long getLineId() {
        return lineId;
    }

    public void setHeaderId(Long headerId) {
        this.headerId = headerId;
    }

    public Long getHeaderId() {
        return headerId;
    }

    public void setFiledType(String filedType) {
        this.filedType = filedType;
    }

    public String getFiledType() {
        return filedType;
    }

    public void setFiledValue(String filedValue) {
        this.filedValue = filedValue;
    }

    public String getFiledValue() {
        return filedValue;
    }

    public void setFieldSequence(Long fieldSequence) {
        this.fieldSequence = fieldSequence;
    }

    public Long getFieldSequence() {
        return fieldSequence;
    }

    public void setDateMask(String dateMask) {
        this.dateMask = dateMask;
    }

    public String getDateMask() {
        return dateMask;
    }

    public void setSeqLength(Long seqLength) {
        this.seqLength = seqLength;
    }

    public Long getSeqLength() {
        return seqLength;
    }

    public void setStartValue(Long startValue) {
        this.startValue = startValue;
    }

    public Long getStartValue() {
        return startValue;
    }

    public void setCurrentValue(Long currentValue) {
        this.currentValue = currentValue;
    }

    public Long getCurrentValue() {
        return currentValue;
    }

    public void setResetFrequency(String resetFrequency) {
        this.resetFrequency = resetFrequency;
    }

    public String getResetFrequency() {
        return resetFrequency;
    }

    public void setResetDate(Date resetDate) {
        this.resetDate = resetDate;
    }

    public Date getResetDate() {
        return resetDate;
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }

    public Long getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(Long stepNumber) {
        this.stepNumber = stepNumber;
    }
}
