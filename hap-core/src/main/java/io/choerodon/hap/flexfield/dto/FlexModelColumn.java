package io.choerodon.hap.flexfield.dto;

import io.choerodon.mybatis.common.query.Comparison;
import io.choerodon.mybatis.common.query.Where;
import io.choerodon.mybatis.entity.BaseDTO;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Table(name = "fnd_flex_model_column")
public class FlexModelColumn extends BaseDTO {

    public static final String FIELD_MODEL_COLUMN_ID = "modelColumnId";
    public static final String FIELD_MODEL_ID = "modelId";
    public static final String FIELD_COLUMN_NAME = "columnName";
    public static final String FIELD_DESCRIPTION = "description";


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @OrderBy("ASC")
    private Long modelColumnId;

    @NotNull
    @Where
    private Long modelId; //模型ID

    @NotEmpty
    @Length(max = 50)
    @Where(comparison = Comparison.LIKE)
    private String columnName; //模型弹性域

    @Length(max = 80)
    private String description; //弹性域描述

    public FlexModelColumn() {

    }

    public FlexModelColumn(Long modelId) {
        this.modelId = modelId;
    }

    public void setModelColumnId(Long modelColumnId) {
        this.modelColumnId = modelColumnId;
    }

    public Long getModelColumnId() {
        return modelColumnId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public Long getModelId() {
        return modelId;
    }

    public void setColumnName(String columnName) {
        this.columnName = StringUtils.trim(columnName);
    }

    public String getColumnName() {
        return columnName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
