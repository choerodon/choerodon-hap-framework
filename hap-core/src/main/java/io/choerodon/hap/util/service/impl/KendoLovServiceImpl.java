package io.choerodon.hap.util.service.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.base.util.BaseConstants;
import io.choerodon.hap.util.cache.impl.LovCache;
import io.choerodon.hap.util.dto.Lov;
import io.choerodon.hap.util.dto.LovItem;
import io.choerodon.hap.util.mapper.LovItemMapper;
import io.choerodon.hap.util.mapper.LovMapper;
import io.choerodon.hap.util.service.IKendoLovService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * KendoLov
 *
 * @author njq.niu@hand-china.com
 */
@Service
@Transactional
public class KendoLovServiceImpl implements IKendoLovService {

    private final Logger logger = LoggerFactory.getLogger(KendoLovServiceImpl.class);

    @Autowired
    private LovMapper lovMapper;

    @Autowired
    private LovItemMapper lovItemMapper;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LovCache lovCache;

    public String getLov(String contextPath, Locale locale, String lovCode) {
        LovEditor editor = getLovEditor(contextPath, locale, lovCode);
        try {
            return objectMapper.writeValueAsString(editor);
        } catch (JsonProcessingException e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
            }
        }
        return "''";
    }

    private LovEditor getLovEditor(String contextPath, Locale locale, String lovCode) {
        Lov lov = lovCache.getValue(lovCode);
        if (lov == null) {
            lov = lovMapper.selectByCode(lovCode);
            if (lov != null) {
                List<LovItem> items = lovItemMapper.selectByLovId(lov.getLovId());
                lov.setLovItems(items);
                lovCache.setValue(lov.getCode(), lov);
            }
        }

        return lov != null ? createLovEditor(contextPath, locale, lov, lov.getLovItems()) : null;
    }

    private LovEditor createLovEditor(String contextPath, Locale locale, Lov lov, List<LovItem> items) {
        LovEditor editor = new LovEditor(lov, locale);
        editor.setGrid(new LovGrid(contextPath, locale, lov, items));
        buildConditionForm(editor, items, contextPath, locale);
        return editor;
    }

    private void buildConditionForm(LovEditor editor, List<LovItem> items, String contextPath, Locale locale) {
        StringBuilder html = new StringBuilder();
        int customCols = editor.getQueryColumns() == null ? 1 : editor.getQueryColumns();
        int colsNum = 0;
        int labelWidth = 3;
        if (customCols < 1) {
            customCols = 1;
        } else if (customCols > 4) {
            customCols = 4;
        }

        List<LovItem> formItems = new ArrayList<>();

        if (items != null) {
            for (LovItem item : items) {
                if (BaseConstants.YES.equalsIgnoreCase(item.getConditionField())) {
                    formItems.add(item);
                    colsNum++;
                }
            }
        }
        formItems.sort(Comparator.comparingInt(LovItem::getConditionFieldSequence));
        if (customCols > colsNum && colsNum > 0) {
            customCols = colsNum;
        }
        if (customCols >= 3) {
            labelWidth = 2;
        }
        int colCounter = customCols;
        html.append("<div class='form-group' style='width:100%'>");
        int overlength = 12;
        for (LovItem item : formItems) {
            int finalLabelWidth = labelWidth;
            int widthValue = (12 - finalLabelWidth * customCols) / customCols;
            if (BaseConstants.YES.equalsIgnoreCase(item.getConditionField())) {
                String field = StringUtils.defaultIfEmpty(item.getConditionFieldName(), item.getGridFieldName());
                if (item.getConditionFieldLabelWidth() != null && item.getConditionFieldLabelWidth() > 0) {
                    finalLabelWidth = item.getConditionFieldLabelWidth();
                }
                if (item.getConditionFieldWidth() != null) {
                    if (item.getConditionFieldWidth() <= (12 - finalLabelWidth) && item.getConditionFieldWidth() > 0) {
                        widthValue = item.getConditionFieldWidth();
                    } else if (item.getConditionFieldWidth() > (12 - finalLabelWidth)) {
                        widthValue = 12 - finalLabelWidth;
                    }
                }
                if (colCounter > 0) {
                    if (overlength - finalLabelWidth >= widthValue) {
                        overlength = overlength - finalLabelWidth - widthValue;
                        item.setConditionFieldNewline(BaseConstants.NO);
                        colCounter--;
                    } else {
                        overlength = 12 - finalLabelWidth - widthValue;
                        item.setConditionFieldNewline(BaseConstants.YES);
                        colCounter = customCols - 1;
                    }
                } else {
                    item.setConditionFieldNewline(BaseConstants.YES);
                    colCounter = customCols - 1;
                }

                if (BaseConstants.YES.equals(item.getConditionFieldNewline())) {
                    html.append("</div><div class='form-group' style='width:100%'>");
                }
                html.append("<label class='col-sm-").append(finalLabelWidth).append(" control-label' >")
                        .append(messageSource.getMessage(item.getDisplay(), null, item.getDisplay(), locale))
                        .append("</label>").append("<div class='col-sm-").append(widthValue).append(" k-lov-input' >")
                        .append("<input name='").append(field).append("' data-bind='value:data.").append(field)
                        .append("' style='width:100%'>").append("</div>");

                editor.getFormItemMap().put(field, buildConditionFormItem(item, contextPath));
            }
        }
        html.append("</div>");
        editor.setForm(html.toString());
    }

    /**
     * 暂有text、combobox、number、date四种组件.
     *
     * @param item
     * @param contextPath
     * @return
     */
    private LovFormItem buildConditionFormItem(LovItem item, String contextPath) {
        LovFormItem formItem = new LovFormItem();
        String type = StringUtils.defaultIfEmpty(item.getConditionFieldType(), "text");
        switch (type) {
            case "text":
                formItem.setType("kendoMaskedTextBox");
                break;
            case "select":
                formItem.setType("kendoComboBox");
                formItem.setValuePrimitive(true);
                if (StringUtils.isNotEmpty(item.getConditionFieldSelectCode())) {
                    formItem.setDataSource(contextPath + "/common/code/" + item.getConditionFieldSelectCode() + "/");
                    formItem.setDataValueField("value");
                    formItem.setDataTextField("meaning");
                } else if (StringUtils.isNotEmpty(item.getConditionFieldSelectUrl())) {
                    formItem.setDataSource(contextPath + item.getConditionFieldSelectUrl());
                    formItem.setDataValueField(item.getConditionFieldSelectVf());
                    formItem.setDataTextField(item.getConditionFieldSelectTf());
                }
                break;
            case "int":
                formItem.setType("kendoNumericTextBox");
                break;
            case "date":
                formItem.setType("kendoDatePicker");
                break;
            default:
                break;
        }
        return formItem;
    }

    private class LovEditor {

        LovEditor(Lov lov, Locale locale) {
            if (lov != null) {
                setTitle(messageSource.getMessage(lov.getTitle(), null, lov.getTitle(), locale));
                setWidth(lov.getWidth());
                setHeight(lov.getHeight());
                setQueryColumns(lov.getQueryColumns());
                setDataTextField(lov.getTextField());
                setDataValueField(lov.getValueField());
                if (lov.getPlaceholder() != null) {
                    setPlaceholder(messageSource.getMessage(lov.getPlaceholder(), null, locale));
                }
                if (BaseConstants.YES.equals(lov.getEditableFlag())) {
                    setReadonly(false);
                } else {
                    setReadonly(true);
                }
                if (BaseConstants.YES.equals(lov.getTreeFlag())) {
                    setTree(true);
                    setIdField(lov.getIdField());
                    setParentIdField(lov.getParentIdField());
                } else {
                    setTree(false);
                }

            }

        }

        @JsonInclude(Include.NON_NULL)
        private Integer queryColumns;

        @JsonInclude(Include.NON_NULL)
        private Integer height;

        @JsonInclude(Include.NON_NULL)
        private Integer width;

        private boolean readonly;

        private String dataValueField;

        private String dataTextField;

        @JsonInclude(Include.NON_NULL)
        private String title;

        @JsonInclude(Include.NON_NULL)
        private String placeholder;

        private String form;

        private Map<String, LovFormItem> formItemMap = new HashMap<>();

        private LovGrid grid;

        private Boolean isTree;

        private String idField;

        private String parentIdField;


        public Boolean getTree() {
            return isTree;
        }

        public void setTree(Boolean tree) {
            isTree = tree;
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

        public Integer getQueryColumns() {
            return queryColumns;
        }

        public void setQueryColumns(Integer queryColumns) {
            this.queryColumns = queryColumns;
        }

        public boolean isReadonly() {
            return readonly;
        }

        public void setReadonly(boolean readonly) {
            this.readonly = readonly;
        }

        public Map<String, LovFormItem> getFormItemMap() {
            return formItemMap;
        }

        public void setFormItemMap(Map<String, LovFormItem> formItemMap) {
            this.formItemMap = formItemMap;
        }

        public String getForm() {
            return form;
        }

        public void setForm(String form) {
            this.form = form;
        }

        public Integer getWidth() {
            return width;
        }

        public void setWidth(Integer width) {
            this.width = width;
        }

        public Integer getHeight() {
            return height;
        }

        public void setHeight(Integer height) {
            this.height = height;
        }

        public String getDataValueField() {
            return dataValueField;
        }

        public void setDataValueField(String dataValueField) {
            this.dataValueField = dataValueField;
        }

        public String getDataTextField() {
            return dataTextField;
        }

        public void setDataTextField(String dataTextField) {
            this.dataTextField = dataTextField;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public LovGrid getGrid() {
            return grid;
        }

        public void setGrid(LovGrid grid) {
            this.grid = grid;
        }

        public String getPlaceholder() {
            return placeholder;
        }

        public void setPlaceholder(String placeholder) {
            this.placeholder = placeholder;
        }

    }

    private class LovFormItem {

        private String type;

        @JsonInclude(Include.NON_NULL)
        private String dataSource;

        @JsonInclude(Include.NON_NULL)
        private String dataValueField;

        @JsonInclude(Include.NON_NULL)
        private String dataTextField;

        @JsonInclude(Include.NON_NULL)
        private Boolean valuePrimitive;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDataSource() {
            return dataSource;
        }

        public void setDataSource(String dataSource) {
            this.dataSource = dataSource;
        }

        public String getDataValueField() {
            return dataValueField;
        }

        public void setDataValueField(String dataValueField) {
            this.dataValueField = dataValueField;
        }

        public Boolean getValuePrimitive() {
            return valuePrimitive;
        }

        public void setValuePrimitive(Boolean valuePrimitive) {
            this.valuePrimitive = valuePrimitive;
        }

        public String getDataTextField() {
            return dataTextField;
        }

        public void setDataTextField(String dataTextField) {
            this.dataTextField = dataTextField;
        }

    }

    private class LovGrid {

        LovGrid(String contextPath, Locale locale, Lov lov, List<LovItem> items) {
            String customUrl = lov.getCustomUrl();
            if (StringUtils.isEmpty(customUrl)) {
                setUrl(contextPath + "/common/lov/" + lov.getCode());
            } else {
                setUrl(contextPath + customUrl);
            }
            setPageSize(lov.getLovPageSize());
            setHeight(lov.getHeight());
            if (items != null) {
                for (LovItem item : items) {
                    if (BaseConstants.YES.equalsIgnoreCase(item.getGridField())) {
                        addColumn(new LovGridColumn(contextPath, locale, item));
                    }
                }
                columns.sort(Comparator.comparingInt(LovGridColumn::getSequence));
            }
        }

        private Integer height;

        private String url;

        private List<LovGridColumn> columns = new ArrayList<>();

        private String pageSize;

        public String getPageSize() {
            return pageSize;
        }

        public void setPageSize(String pageSize) {
            this.pageSize = pageSize;
        }

        public Integer getHeight() {
            return height;
        }

        public void setHeight(Integer height) {
            this.height = height;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void addColumn(LovGridColumn column) {
            columns.add(column);
        }

        public List<LovGridColumn> getColumns() {
            return columns;
        }

        public void setColumns(List<LovGridColumn> columns) {
            this.columns = columns;
        }

    }

    private class LovGridColumn {

        LovGridColumn(String contextPath, Locale locale, LovItem item) {
            String display = messageSource.getMessage(item.getDisplay(), null, item.getDisplay(), locale);
            setTitle(display);
            setField(item.getGridFieldName());
            setAlign(item.getGridFieldAlign());
            setSequence(item.getGridFieldSequence());
            setWidth(item.getGridFieldWidth());

        }

        @JsonIgnore
        private Integer sequence = 1;

        private String field;

        @JsonInclude(Include.NON_NULL)
        private String title;

        @JsonInclude(Include.NON_NULL)
        private Integer width;

        @JsonInclude(Include.NON_NULL)
        private Map<String, Object> attributes;

        public Map<String, Object> getAttributes() {
            return attributes;
        }

        public void setAttributes(Map<String, Object> attributes) {
            this.attributes = attributes;
        }

        public void setAlign(String align) {
            Map<String, Object> attrs = getAttributes();
            if (attrs == null) {
                attrs = new HashMap<>();
                setAttributes(attrs);
            }
            attrs.put("style", "text-align:" + align);
        }

        public Integer getSequence() {
            return sequence;
        }

        public void setSequence(Integer sequence) {
            this.sequence = sequence;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Integer getWidth() {
            return width;
        }

        public void setWidth(Integer width) {
            this.width = width;
        }

    }

}
