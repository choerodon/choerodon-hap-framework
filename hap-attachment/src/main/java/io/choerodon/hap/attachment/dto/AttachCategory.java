package io.choerodon.hap.attachment.dto;

import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.entity.BaseDTO;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;

/**
 * 文件分类DTO.
 * 
 * @author hua.xiao
 */
@MultiLanguage
@Table(name = "sys_attach_category_b")
public class AttachCategory extends BaseDTO {

    public static final String FIELD_CATEGORY_ID = "categoryId";
    public static final String FIELD_CATEGORY_NAME = "categoryName";
    public static final String FIELD_CATEGORY_PATH = "categoryPath";
    public static final String FIELD_PATH = "path";
    public static final String FIELD_ALLOWED_FILE_TYPE = "allowedFileType";
    public static final String FIELD_ALLOWED_FILE_SIZE = "allowedFileSize";
    public static final String FIELD_LEAF_FLAG = "leafFlag";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_STATUS = "status";
    public static final String FIELD_PARENT_CATEGORY_ID = "parentCategoryId";
    public static final String FIELD_SOURCE_TYPE = "sourceType";
    public static final String FIELD_IS_UNIQUE = "isUnique";

    /**
     * 设置类型分隔符.
     */
    public static final String TYPE_SEPARATOR = ";";

    /**
     * 顶层分类，没有父id.
     */
    public static final Long NO_PARENT = -1L;

    /**
     * 正常.
     */
    public static final String STATUS_NORMAL = "1";
    /**
     * 归档.
     */
    public static final String STATUS_DELETED = "2";
    /**
     * 是叶子节点.
     */
    public static final String LEAF_TRUE = "1";
    /**
     * 不是叶子节点.
     */
    public static final String LEAF_FALSE = "0";
//    /**
//     * 是唯一.
//     */
//    public static final String ISUNIQUE_TRUE = "Y";
//    /**
//     * 不是唯一.
//     */
//    public static final String ISUNIQUE_FALSE = "N";

    /**
     * 默认是设置sourceType设置.
     */
    public static final String DEFAULT_SOURCETYPE = "folder";
    /**
     * 类型id.
     */
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    /**
     * 类型名字.
     */
    @Column
    @MultiLanguageField
    @NotEmpty
    private String categoryName;

    /**
     * 类型路径.
     */
    @Column
    private String categoryPath;

    /**
     * 层级路径
     */
    private String path;


    /**
     * 可上传类型,以后缀名结尾.
     */
    @Column
    private String allowedFileType;

    /**
     * 可上传单个文件大小.
     */
    @Column
    private Long allowedFileSize;

    @Transient
    private String allowedFileSizeDesc;

    /**
     * 是否.
     */
    @Column
    private String leafFlag;

    /**
     * 分类描述.
     */
    @Column
    @MultiLanguageField
    private String description;

    @Column
    private String status;

    @Column
    private Long parentCategoryId;

    @Column
    private String sourceType;

    @Column
    private String isUnique;

    public String getAllowedFileSizeDesc() {
        return allowedFileSizeDesc;
    }

    public void setAllowedFileSizeDesc(String allowedFileSizeDesc) {
        this.allowedFileSizeDesc = allowedFileSizeDesc;
    }

    public String getIsUnique() {
        return isUnique;
    }

    public void setIsUnique(String isUnique) {
        this.isUnique = isUnique;
    }

    public AttachCategory() {
        super();
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getLeafFlag() {
        return leafFlag;
    }

    public void setLeafFlag(String leafFlag) {
        this.leafFlag = leafFlag;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(Long parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    public String getSourceType() {
        // 统一sourceType都是大写
        if (StringUtils.isNotBlank(sourceType)) {
            return sourceType.trim().toUpperCase();
        }
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getCategoryPath() {
        return categoryPath;
    }

    public void setCategoryPath(String categoryPath) {
        this.categoryPath = categoryPath;
    }

    public String getAllowedFileType() {
        return allowedFileType;
    }

    public void setAllowedFileType(String allowedFileType) {
        this.allowedFileType = allowedFileType;
    }

    public Long getAllowedFileSize() {
        return allowedFileSize;
    }

    public void setAllowedFileSize(Long allowedFileSize) {
        this.allowedFileSize = allowedFileSize;
    }

}
