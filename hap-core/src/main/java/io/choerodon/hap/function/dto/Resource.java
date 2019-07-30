package io.choerodon.hap.function.dto;

import io.choerodon.mybatis.annotation.EnableExtensionAttribute;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
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

/**
 * 资源DTO.
 *
 * @author wuyichu
 * @author njq.niu@hand-china.com
 */
@MultiLanguage
@Table(name = "sys_resource_b")
@EnableExtensionAttribute
public class Resource extends BaseDTO {

    public static final String FIELD_RESOURCE_ID = "resourceId";
    public static final String FIELD_ACCESS_CHECK = "accessCheck";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_LOGIN_REQUIRE = "loginRequire";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_TYPE = "type";
    public static final String FIELD_URL = "url";
    public static final String FIELD_LOCKED_BY = "lockedBy";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resourceId;

    @NotEmpty
    @Length(max = 1)
    private String accessCheck;

    @MultiLanguageField
    @Length(max = 240)
    private String description;

    @NotEmpty
    @Length(max = 1)
    private String loginRequire;

    @MultiLanguageField
    @NotEmpty
    @Where(comparison = Comparison.LIKE)
    @Length(max = 40)
    private String name;

    @NotEmpty
    @Where
    @Length(max = 15)
    private String type;

    @NotEmpty
    @Where(comparison = Comparison.LIKE)
    @OrderBy("ASC")
    @Length(max = 255)
    private String url;

    /**
     * 建模页面被谁锁定.
     */
    @Length(max = 40)
    @Where
    String lockedBy;

    public String getAccessCheck() {
        return accessCheck;
    }

    public String getDescription() {
        return description;
    }

    public String getLoginRequire() {
        return loginRequire;
    }

    public String getName() {
        return name;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public void setAccessCheck(String accessCheck) {
        this.accessCheck = accessCheck;
    }

    public void setDescription(String description) {
        this.description = StringUtils.trim(description);
    }

    public void setLoginRequire(String loginRequire) {
        this.loginRequire = StringUtils.trim(loginRequire);
    }

    public void setName(String name) {
        this.name = StringUtils.trim(name);
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public void setType(String type) {
        this.type = StringUtils.trim(type);
    }

    public void setUrl(String url) {
        this.url = StringUtils.trim(url);
    }

    public String getLockedBy() {
        return lockedBy;
    }

    public void setLockedBy(String lockedBy) {
        this.lockedBy = lockedBy;
    }
}