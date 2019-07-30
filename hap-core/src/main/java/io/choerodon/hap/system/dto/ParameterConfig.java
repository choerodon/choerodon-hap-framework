package io.choerodon.hap.system.dto;

import io.choerodon.mybatis.common.query.Where;
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
 * @author qiang.zeng
 * @since 2017/11/6
 */
@Table(name = "sys_parameter_config")
public class ParameterConfig extends BaseDTO {

    public static final String FIELD_PARAMETER_ID = "parameterId";
    public static final String FIELD_CODE = "code";
    public static final String FIELD_TARGET_ID = "targetId";
    public static final String FIELD_DISPLAY = "display";
    public static final String FIELD_TABLE_FIELD_NAME = "tableFieldName";
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_LINE_NUMBER = "lineNumber";
    public static final String FIELD_COLUMN_NUMBER = "columnNumber";
    public static final String FIELD_DISPLAY_LENGTH = "displayLength";
    public static final String FIELD_LABEL_WIDTH = "labelWidth";
    public static final String FIELD_DATA_LENGTH = "dataLength";
    public static final String FIELD_REQUIRED = "required";
    public static final String FIELD_READ_ONLY = "readOnly";
    public static final String FIELD_ENABLED = "enabled";
    public static final String FIELD_SOURCE_TYPE = "sourceType";
    public static final String FIELD_SOURCE_CODE = "sourceCode";
    public static final String FIELD_SOURCE_NAME = "sourceName";
    public static final String FIELD_DEFAULT_TYPE = "defaultType";
    public static final String FIELD_DEFAULT_VALUE = "defaultValue";
    public static final String FIELD_DEFAULT_TEXT = "defaultText";
    public static final String FIELD_EXTRA_ATTRIBUTE = "extraAttribute";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long parameterId;
    @Column
    @Length(max = 20)
    @Where
    private String code;
    @Column
    @Where
    private Long targetId;
    @Column
    @NotEmpty
    @Length(max = 20)
    private String display;
    @Column
    @NotEmpty
    @Length(max = 80)
    private String tableFieldName;
    @Column
    @NotEmpty
    @Length(max = 50)
    private String title;
    @Column
    @Length(max = 255)
    private String description;
    @Column
    @OrderBy("ASC")
    private Integer lineNumber;
    @Column
    private Integer columnNumber;
    @Column
    private Integer displayLength;
    @Column
    private Integer labelWidth;
    @Column
    private Integer dataLength;
    @Column
    @Length(max = 1)
    private String required;
    @Column
    @Length(max = 1)
    private String readOnly;
    @Column
    @Length(max = 1)
    private String enabled;
    @Column
    @Length(max = 20)
    private String sourceType;
    @Column
    @Length(max = 80)
    private String sourceCode;
    @Transient
    private String sourceName;
    @Column
    @Length(max = 20)
    private String defaultType;
    @Column
    private String defaultValue;
    @Column
    @Length(max = 50)
    private String defaultText;
    @Column
    private String extraAttribute;

    public Long getParameterId() {
        return parameterId;
    }

    public void setParameterId(Long parameterId) {
        this.parameterId = parameterId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getTableFieldName() {
        return tableFieldName;
    }

    public void setTableFieldName(String tableFieldName) {
        this.tableFieldName = tableFieldName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    public Integer getColumnNumber() {
        return columnNumber;
    }

    public void setColumnNumber(Integer columnNumber) {
        this.columnNumber = columnNumber;
    }

    public Integer getDisplayLength() {
        return displayLength;
    }

    public void setDisplayLength(Integer displayLength) {
        this.displayLength = displayLength;
    }

    public Integer getLabelWidth() {
        return labelWidth;
    }

    public void setLabelWidth(Integer labelWidth) {
        this.labelWidth = labelWidth;
    }

    public Integer getDataLength() {
        return dataLength;
    }

    public void setDataLength(Integer dataLength) {
        this.dataLength = dataLength;
    }

    public String getRequired() {
        return required;
    }

    public void setRequired(String required) {
        this.required = required;
    }

    public String getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(String readOnly) {
        this.readOnly = readOnly;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getDefaultType() {
        return defaultType;
    }

    public void setDefaultType(String defaultType) {
        this.defaultType = defaultType;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDefaultText() {
        return defaultText;
    }

    public void setDefaultText(String defaultText) {
        this.defaultText = defaultText;
    }

    public String getExtraAttribute() {
        return extraAttribute;
    }

    public void setExtraAttribute(String extraAttribute) {
        this.extraAttribute = extraAttribute;
    }
}