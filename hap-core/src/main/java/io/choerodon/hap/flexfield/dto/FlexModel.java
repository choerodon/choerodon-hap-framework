package io.choerodon.hap.flexfield.dto;

import io.choerodon.mybatis.common.query.Where;
import io.choerodon.mybatis.entity.BaseDTO;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

@Table(name = "fnd_flex_model")
public class FlexModel extends BaseDTO {

    public static final String FIELD_MODEL_ID = "modelId";
    public static final String FIELD_MODEL_CODE = "modelCode";
    public static final String FIELD_MODEL_NAME = "modelName";
    public static final String FIELD_MODEL_TABLE = "modelTable";


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long modelId;

    @NotEmpty
    @Length(max = 50)
    @Where
    private String modelCode; //模型代码

    @NotEmpty
    @Length(max = 80)
    @Where
    private String modelName; //模型名称

    @NotEmpty
    @Length(max = 50)
    private String modelTable; //模型表


    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public Long getModelId() {
        return modelId;
    }

    public void setModelCode(String modelCode) {
        this.modelCode = StringUtils.trim(modelCode);
    }

    public String getModelCode() {
        return modelCode;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelTable(String modelTable) {
        this.modelTable = StringUtils.trim(modelTable);
    }

    public String getModelTable() {
        return modelTable;
    }

}
