package io.choerodon.hap.function.dto;

/**
 * 构造组件菜单ID的中转类.
 * 用来保证菜单的ID不会重复
 *
 * @author qiang.zeng
 * @since 2017/9/1
 */
public class ResourceItemCount {

    private Long resourceId = 0L;
    private Long variateId = -1L;
    private Long btnGroupId = -2L;
    private Long formId = -3L;
    private Long gridId = -4L;
    private Long btnId = -5L;
    private Long fieldId = -6L;
    private Long toolbarId = -7L;
    private Long columnId = -8L;
    private Long columnButtonId = -9L;

    public Long getVariateId() {
        return variateId;
    }

    public void setVariateId(Long variateId) {
        this.variateId = variateId;
    }

    public Long getBtnId() {
        return btnId;
    }

    public void setBtnId(Long btnId) {
        this.btnId = btnId;
    }

    public Long getFieldId() {
        return fieldId;
    }

    public void setFieldId(Long fieldId) {
        this.fieldId = fieldId;
    }

    public Long getToolbarId() {
        return toolbarId;
    }

    public void setToolbarId(Long toolbarId) {
        this.toolbarId = toolbarId;
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public Long getColumnButtonId() {
        return columnButtonId;
    }

    public void setColumnButtonId(Long columnButtonId) {
        this.columnButtonId = columnButtonId;
    }

    public Long getGridId() {
        return gridId;
    }

    public void setGridId(Long gridId) {
        this.gridId = gridId;
    }

    public Long getFormId() {
        return formId;
    }

    public void setFormId(Long formId) {
        this.formId = formId;
    }

    public Long getBtnGroupId() {
        return btnGroupId;
    }

    public void setBtnGroupId(Long btnGroupId) {
        this.btnGroupId = btnGroupId;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }
}
