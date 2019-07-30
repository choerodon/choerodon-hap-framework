package io.choerodon.hap.util.dto;

import io.choerodon.base.util.BaseConstants;
import io.choerodon.mybatis.common.query.Where;
import io.choerodon.mybatis.entity.BaseDTO;
import org.hibernate.validator.constraints.Length;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

/**
 * Lov子项DTO.
 *
 * @author njq.niu@hand-china.com
 * @since 2016/2/1
 */
@Table(name = "sys_lov_item")
public class LovItem extends BaseDTO {

    private static final long serialVersionUID = -1573793167997659244L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lovItemId;

    private Long lovId;

    @Length(max = 255)
    @NotEmpty
    private String display;

    @Where(exclude = true)
    @Length(max = 80)
    @NotEmpty
    private String gridFieldName;

    @Where(exclude = true)
    private Integer gridFieldWidth;

    @Where(exclude = true)
    @Length(max = 10)
    private String gridFieldAlign;

    @Where(exclude = true)
    @Length(max = 1)
    private String autocompleteField = BaseConstants.YES;

    @Where(exclude = true)
    @Length(max = 80)
    private String conditionField = BaseConstants.NO;

    @Where(exclude = true)
    @Length(max = 1)
    private String isAutocomplete = BaseConstants.NO;

    @Where(exclude = true)
    @Length(max = 1)
    private String gridField = BaseConstants.YES;

    @Where(exclude = true)
    private Integer conditionFieldWidth;

    @Where(exclude = true)
    private Integer conditionFieldLabelWidth;

    @Where(exclude = true)
    @Length(max = 30)
    private String conditionFieldType;

    @Where(exclude = true)
    @Length(max = 80)
    private String conditionFieldName;

    @Where(exclude = true)
    @Length(max = 80)
    private String conditionFieldTextfield;

    @Where(exclude = true)
    @Length(max = 1)
    private String conditionFieldNewline = BaseConstants.NO;

    @Where(exclude = true)
    @Length(max = 255)
    private String conditionFieldSelectUrl;

    @Where(exclude = true)
    @Length(max = 80)
    private String conditionFieldSelectVf;

    @Where(exclude = true)
    @Length(max = 80)
    private String conditionFieldSelectTf;

    @Where(exclude = true)
    @Length(max = 80)
    private String conditionFieldSelectCode;

    @Where(exclude = true)
    @Length(max = 80)
    private String conditionFieldLovCode;

    @Where(exclude = true)
    private Integer conditionFieldSequence = 1;

    @Where(exclude = true)
    private Integer gridFieldSequence = 1;

    public String getConditionFieldTextfield() {
        return conditionFieldTextfield;
    }

    public Integer getConditionFieldLabelWidth() {
        return conditionFieldLabelWidth;
    }

    public void setConditionFieldLabelWidth(Integer conditionFieldLabelWidth) {
        this.conditionFieldLabelWidth = conditionFieldLabelWidth;
    }

    public void setConditionFieldTextfield(String conditionFieldTextfield) {
        this.conditionFieldTextfield = conditionFieldTextfield;
    }

    public String getConditionFieldSelectUrl() {
        return conditionFieldSelectUrl;
    }

    public void setConditionFieldSelectUrl(String conditionFieldSelectUrl) {
        this.conditionFieldSelectUrl = conditionFieldSelectUrl;
    }

    public String getConditionFieldSelectVf() {
        return conditionFieldSelectVf;
    }

    public void setConditionFieldSelectVf(String conditionFieldSelectVf) {
        this.conditionFieldSelectVf = conditionFieldSelectVf;
    }

    public String getConditionFieldSelectTf() {
        return conditionFieldSelectTf;
    }

    public void setConditionFieldSelectTf(String conditionFieldSelectTf) {
        this.conditionFieldSelectTf = conditionFieldSelectTf;
    }

    public String getAutocompleteField() {
        return autocompleteField;
    }

    public void setAutocompleteField(String autocompleteField) {
        this.autocompleteField = autocompleteField;
    }

    public String getGridFieldAlign() {
        return gridFieldAlign;
    }

    public void setGridFieldAlign(String gridFieldAlign) {
        this.gridFieldAlign = gridFieldAlign != null ? gridFieldAlign : "center";
    }

    public Integer getConditionFieldSequence() {
        return conditionFieldSequence;
    }

    public void setConditionFieldSequence(Integer conditionFieldSequence) {
        this.conditionFieldSequence = conditionFieldSequence;
    }

    public Integer getGridFieldSequence() {
        return gridFieldSequence;
    }

    public void setGridFieldSequence(Integer gridFieldSequence) {
        this.gridFieldSequence = gridFieldSequence;
    }

    public String getConditionFieldType() {
        return conditionFieldType;
    }

    public void setConditionFieldType(String conditionFieldType) {
        this.conditionFieldType = conditionFieldType;
    }

    public String getConditionFieldName() {
        return conditionFieldName;
    }

    public void setConditionFieldName(String conditionFieldName) {
        this.conditionFieldName = conditionFieldName;
    }

    public String getConditionFieldNewline() {
        return conditionFieldNewline;
    }

    public void setConditionFieldNewline(String conditionFieldNewline) {
        this.conditionFieldNewline = conditionFieldNewline;
    }

    public String getConditionFieldSelectCode() {
        return conditionFieldSelectCode;
    }

    public void setConditionFieldSelectCode(String conditionFieldSelectCode) {
        this.conditionFieldSelectCode = conditionFieldSelectCode;
    }

    public String getConditionFieldLovCode() {
        return conditionFieldLovCode;
    }

    public void setConditionFieldLovCode(String conditionFieldLovCode) {
        this.conditionFieldLovCode = conditionFieldLovCode;
    }

    public Integer getConditionFieldWidth() {
        return conditionFieldWidth;
    }

    public void setConditionFieldWidth(Integer conditionFieldWidth) {
        this.conditionFieldWidth = conditionFieldWidth;
    }

    public String getGridField() {
        return gridField;
    }

    public void setGridField(String gridField) {
        this.gridField = gridField;
    }

    public Long getLovItemId() {
        return lovItemId;
    }

    public void setLovItemId(Long lovItemId) {
        this.lovItemId = lovItemId;
    }

    public Long getLovId() {
        return lovId;
    }

    public void setLovId(Long lovId) {
        this.lovId = lovId;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display == null ? null : display.trim();
    }

    public String getGridFieldName() {
        return gridFieldName;
    }

    public void setGridFieldName(String name) {
        this.gridFieldName = name == null ? null : name.trim();
    }

    public Integer getGridFieldWidth() {
        return gridFieldWidth;
    }

    public void setGridFieldWidth(Integer width) {
        this.gridFieldWidth = width;
    }

    public String getConditionField() {
        return conditionField;
    }

    public void setConditionField(String conditionField) {
        this.conditionField = conditionField == null ? null : conditionField.trim();
    }

    public void setIsAutocomplete(String isAutoComplete) {
        this.isAutocomplete = isAutoComplete;
    }

    public String getIsAutocomplete() {
        return isAutocomplete;
    }
}