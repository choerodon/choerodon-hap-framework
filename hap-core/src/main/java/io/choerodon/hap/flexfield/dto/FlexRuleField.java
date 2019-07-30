package io.choerodon.hap.flexfield.dto;

import io.choerodon.mybatis.entity.BaseDTO;
import org.hibernate.validator.constraints.Length;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Table(name = "fnd_flex_rule_field")
public class FlexRuleField extends BaseDTO {

    public static final String FIELD_FIELD_ID = "fieldId";
    public static final String FIELD_RULE_ID = "ruleId";
    public static final String FIELD_MODEL_COLUMN_ID = "modelColumnId";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_FIELD_TYPE = "fieldType";
    public static final String FIELD_FIELD_SEQUENCE = "fieldSequence";
    public static final String FIELD_FIELD_COLUMN_NUMBER = "fieldColumnNumber";
    public static final String FIELD_READABLE_FLAG = "readableFlag";
    public static final String FIELD_REQUIRED_FLAG = "requiredFlag";
    public static final String FIELD_FIELD_COLUMN_WIDTH = "fieldColumnWidth";
    public static final String FIELD_COLUMN_NAME = "columnName";
    public static final String FIELD_DATA_SOURCES = "dataSources";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fieldId;

    @NotNull
    private Long ruleId; //规则id

    @NotNull
    private Long modelColumnId; //弹性域id

    private String description;

    @NotEmpty
    @Length(max = 500)
    private String fieldType; //渲染框体类型

    @NotNull
    private Long fieldSequence;

    @NotNull
    private Long fieldColumnNumber;

    @NotEmpty
    @Length(max = 2)
    private String readableFlag;

    @NotEmpty
    @Length(max = 2)
    private String requiredFlag; //是否必输

    @NotNull
    private Long fieldColumnWidth;

    @Transient
    private String columnName;

    @Transient
    private Object dataSources;


    public void setFieldId(Long fieldId) {
        this.fieldId = fieldId;
    }

    public Long getFieldId() {
        return fieldId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setModelColumnId(Long modelColumnId) {
        this.modelColumnId = modelColumnId;
    }

    public Long getModelColumnId() {
        return modelColumnId;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public Long getFieldSequence() {
        return fieldSequence;
    }

    public Long getFieldColumnWidth() {
        return fieldColumnWidth;
    }

    public void setFieldColumnWidth(Long fieldColumnWidth) {
        this.fieldColumnWidth = fieldColumnWidth;
    }

    public void setFieldSequence(Long fieldSequence) {
        this.fieldSequence = fieldSequence;
    }

    public Long getFieldColumnNumber() {
        return fieldColumnNumber;
    }

    public void setFieldColumnNumber(Long fieldColumnNumber) {
        this.fieldColumnNumber = fieldColumnNumber;
    }


    public String getReadableFlag() {
        return readableFlag;
    }

    public void setReadableFlag(String readableFlag) {
        this.readableFlag = readableFlag;
    }

    public void setRequiredFlag(String requiredFlag) {
        this.requiredFlag = requiredFlag;
    }

    public String getRequiredFlag() {
        return requiredFlag;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getDataSources() {
        return dataSources;
    }

    public void setDataSources(Object dataSources) {
        this.dataSources = dataSources;
    }
}
