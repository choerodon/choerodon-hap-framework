package io.choerodon.hap.function.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.base.annotation.Children;
import io.choerodon.base.util.BaseConstants;
import io.choerodon.mybatis.annotation.EnableExtensionAttribute;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.common.query.Comparison;
import io.choerodon.mybatis.common.query.JoinCache;
import io.choerodon.mybatis.common.query.JoinColumn;
import io.choerodon.mybatis.common.query.JoinOn;
import io.choerodon.mybatis.common.query.JoinTable;
import io.choerodon.mybatis.common.query.Where;
import io.choerodon.mybatis.entity.BaseDTO;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.criteria.JoinType;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 功能DTO.
 *
 * @author njq.niu@hand-china.com
 */
@MultiLanguage
@Table(name = "sys_function_b")
@EnableExtensionAttribute
public class Function extends BaseDTO {

    public static final String FIELD_FUNCTION_ID = "functionId";
    public static final String FIELD_FUNCTION_CODE = "functionCode";
    public static final String FIELD_FUNCTION_DESCRIPTION = "functionDescription";
    public static final String FIELD_FUNCTION_ICON = "functionIcon";
    public static final String FIELD_FUNCTION_SEQUENCE = "functionSequence";
    public static final String FIELD_FUNCTION_NAME = "functionName";
    public static final String FIELD_LANG = "lang";
    public static final String FIELD_MODULE_CODE = "moduleCode";
    public static final String FIELD_PARENT_FUNCTION_ID = "parentFunctionId";
    public static final String FIELD_PARENT_FUNCTION_NAME = "parentFunctionName";
    public static final String FIELD_RESOURCE_NAME = "resourceName";
    public static final String FIELD_RESOURCE_ID = "resourceId";
    public static final String FIELD_RESOURCES = "resources";
    public static final String FIELD_TYPE = "type";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_RESOURCE_URL = "resourceUrl";
    public static final String FIELD_RESOURCE_TYPE = "resourceType";
    public static final String CACHE_FUNCTION = "function";


    @Id
    @Column
    @Where
    @OrderBy("DESC")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long functionId;

    @Column
    private String enabledFlag;

    @Column
    @OrderBy("ASC")
    @Where
    @NotEmpty
    @Length(max = 30)
    private String functionCode;

    @Column
    @MultiLanguageField
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Length(max = 240)
    private String functionDescription;

    @Column
    @Where(exclude = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Length(max = 100)
    private String functionIcon;

    @Column
    @Where(exclude = true)
    @OrderBy
    private Long functionSequence;

    @Column
    @MultiLanguageField
    @Where(comparison = Comparison.LIKE)
    @Length(max = 150)
    private String functionName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Transient
    @Length(max = 10)
    private String lang;

    @Column
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Where
    @Length(max = 30)
    @OrderBy
    private String moduleCode;

    @Column
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Where
    private Long parentFunctionId;

    @Transient
    @JoinCache(joinKey = FIELD_PARENT_FUNCTION_ID, joinColumn = FIELD_FUNCTION_NAME, cacheName = CACHE_FUNCTION)
    @Length(max = 150)
    private String parentFunctionName;

    @Transient
    @JoinColumn(joinName = "resourceJoin", field = Resource.FIELD_NAME)
    @Length(max = 40)
    private String resourceName;

    @Transient
    private String resourceUrl;

    @Transient
    private boolean expand = false;

    @Transient
    private String isChecked;

    @Transient
    private String resourceType;

    @Column
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JoinTable(name = "resourceJoin", joinMultiLanguageTable = true, target = Resource.class, type = JoinType.LEFT, on = {@JoinOn(joinField = Resource.FIELD_RESOURCE_ID), @JoinOn(joinField = BaseDTO.FIELD_LANG, joinExpression = BaseConstants.PLACEHOLDER_LOCALE)})
    @Where
    private Long resourceId;

    @Children
    @Transient
    private List<Resource> resources;

    @Column
    private String type = "PAGE";

    public String getFunctionCode() {
        return functionCode;
    }

    public String getFunctionDescription() {
        return functionDescription;
    }

    public String getFunctionIcon() {
        return functionIcon;
    }

    public Long getFunctionId() {
        return functionId;
    }

    public String getFunctionName() {
        return functionName;
    }

    public String getLang() {
        return lang;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public Long getParentFunctionId() {
        return parentFunctionId;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public String getType() {
        return type;
    }

    public void setFunctionCode(String functionCode) {
        this.functionCode = StringUtils.trim(functionCode);
    }

    public void setFunctionDescription(String functionDescription) {
        this.functionDescription = functionDescription;
    }

    public void setFunctionIcon(String functionIcon) {
        this.functionIcon = functionIcon;
    }

    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public void setLang(String sourceLang) {
        this.lang = sourceLang;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = StringUtils.trim(moduleCode);
    }

    public void setParentFunctionId(Long parentFunctionId) {
        this.parentFunctionId = parentFunctionId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getFunctionSequence() {
        return functionSequence;
    }

    public void setFunctionSequence(Long functionSequence) {
        this.functionSequence = functionSequence;
    }

    public String getParentFunctionName() {
        return parentFunctionName;
    }

    public void setParentFunctionName(String parentFunctionName) {
        this.parentFunctionName = parentFunctionName;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(String enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public boolean isExpand() {
        return expand;
    }

    public void setExpand(boolean expand) {
        this.expand = expand;
    }

    public String getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(String isChecked) {
        this.isChecked = isChecked;
    }
}