package io.choerodon.hap.util.dto;


import io.choerodon.base.annotation.Children;
import io.choerodon.base.util.BaseConstants;
import io.choerodon.mybatis.common.query.Comparison;
import io.choerodon.mybatis.common.query.Where;
import io.choerodon.mybatis.entity.BaseDTO;
import org.hibernate.validator.constraints.Length;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * LovDTO.
 *
 * @author njq.niu@hand-china.com
 * @since 2016/2/1
 */
@Table(name = "sys_lov")
public class Lov extends BaseDTO {
    private static final long serialVersionUID = -466598144320311424L;

    public static final String FIELD_CODE = "code";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_HEIGHT = "height";
    public static final String FIELD_LOV_ID = "lovId";
    public static final String FIELD_LOV_ITEMS = "lovItems";
    public static final String FIELD_PLACEHOLDER = "placeholder";
    public static final String FIELD_SQL_ID = "sqlId";
    public static final String FIELD_CUSTOM_SQL = "customSql";
    public static final String FIELD_QUERY_COLUMNS = "queryColumns";
    public static final String FIELD_CUSTOM_URL = "customUrl";
    public static final String FIELD_TEXT_FIELD = "textField";
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_VALUE_FIELD = "valueField";
    public static final String FIELD_WIDTH = "width";
    public static final String FIELD_DELAY_LOAD = "delayLoad";
    public static final String FIELD_NEED_QUERY_PARAM = "needQueryParam";
    public static final String FIELD_EDITABLE_FLAG = "editableFlag";
    public static final String FIELD_CAN_POPUP = "canPopup";
    public static final String LOV_PAGE_SIZE = "lovPageSize";
    public static final String TREE_FLAG = "treeFlag";
    public static final String ID_FIELD = "idField";
    public static final String PARENT_ID_FIELD = "parentIdField";

    @NotEmpty
    @Length(max = 80)
    @Where
    private String code;

    @Where(comparison = Comparison.LIKE)
    @Length(max = 240)
    private String description;

    @Where(exclude = true)
    private Integer height;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lovId;

    @Children
    @Transient
    private List<LovItem> lovItems;

    @Where(exclude = true)
    private String placeholder;

    @Column
    @Length(max = 225)
    private String sqlId;

    @Column
    private String customSql;

    @Column
    private Integer queryColumns;

    @Column

    @Length(max = 255)
    private String customUrl;

    @NotEmpty
    @Length(max = 80)
    private String textField;

    @Where(comparison = Comparison.LIKE)
    private String title;

    @NotEmpty
    @Where(exclude = true)
    @Length(max = 80)
    private String valueField;

    @Where(exclude = true)
    private Integer width;

    @Where(exclude = true)
    @Length(max = 1)
    private String delayLoad = BaseConstants.NO;

    @Where(exclude = true)
    @Length(max = 1)
    private String needQueryParam = BaseConstants.NO;

    @Where(exclude = true)
    @Column(name = "EDITABLE")
    @Length(max = 1)
    private String editableFlag = BaseConstants.NO;

    @Where(exclude = true)
    @Length(max = 1)
    private String canPopup = BaseConstants.YES;

    @Where(exclude = true)
    @Length(max = 3)
    private String lovPageSize;

    @Where(exclude = true)
    @Length(max = 1)
    private String treeFlag = BaseConstants.NO;

    @Where(exclude = true)
    @Length(max = 80)
    private String idField;

    @Where(exclude = true)
    @Length(max = 80)
    private String parentIdField;

    public String getTreeFlag() {
        return treeFlag;
    }

    public void setTreeFlag(String treeFlag) {
        this.treeFlag = treeFlag;
    }

    public String getIdField() {
        return idField;
    }

    public void setIdField(String idField) {
        this.idField = idField;
    }

    public String getParentIdField() {
        return parentIdField;
    }

    public void setParentIdField(String parentIdField) {
        this.parentIdField = parentIdField;
    }

    public String getLovPageSize() {
        return lovPageSize;
    }

    public void setLovPageSize(String lovPageSize) {
        this.lovPageSize = lovPageSize;
    }

    public String getCustomUrl() {
        return customUrl;
    }

    public void setCustomUrl(String customUrl) {
        this.customUrl = customUrl;
    }

    public String getCustomSql() {
        return customSql;
    }

    public Integer getQueryColumns() {
        return queryColumns;
    }

    public void setQueryColumns(Integer queryColumns) {
        this.queryColumns = queryColumns;
    }

    public void setCustomSql(String customSql) {
        this.customSql = customSql;
    }

    public String getNeedQueryParam() {
        return needQueryParam;
    }

    public void setNeedQueryParam(String needQueryParam) {
        this.needQueryParam = needQueryParam;
    }

    public String getDelayLoad() {
        return delayLoad;
    }

    public void setDelayLoad(String delayLoad) {
        this.delayLoad = delayLoad;
    }

    public String getEditableFlag() {
        return editableFlag;
    }

    public void setEditableFlag(String editable) {
        this.editableFlag = editable;
    }

    public String getCanPopup() {
        return canPopup;
    }

    public void setCanPopup(String canPopup) {
        this.canPopup = canPopup;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public Integer getHeight() {
        return height;
    }

    public Long getLovId() {
        return lovId;
    }

    public List<LovItem> getLovItems() {
        return lovItems;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public String getSqlId() {
        return sqlId;
    }

    public String getTextField() {
        return textField;
    }

    public String getTitle() {
        return title;
    }

    public String getValueField() {
        return valueField;
    }

    public Integer getWidth() {
        return width;
    }

    public void setCode(String code) {
        this.code = StringUtils.trimWhitespace(code);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public void setLovId(Long lovId) {
        this.lovId = lovId;
    }

    public void setLovItems(List<LovItem> lovItems) {
        this.lovItems = lovItems;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public void setSqlId(String sqlId) {
        this.sqlId = StringUtils.trimWhitespace(sqlId);
    }

    public void setTextField(String textField) {
        this.textField = StringUtils.trimWhitespace(textField);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setValueField(String valueField) {
        this.valueField = StringUtils.trimWhitespace(valueField);
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

}