package io.choerodon.hap.fnd.dto;

import io.choerodon.base.util.BaseConstants;
import io.choerodon.hap.hr.dto.Position;
import io.choerodon.mybatis.annotation.EnableExtensionAttribute;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.common.query.Comparison;
import io.choerodon.mybatis.common.query.JoinCode;
import io.choerodon.mybatis.common.query.JoinColumn;
import io.choerodon.mybatis.common.query.JoinOn;
import io.choerodon.mybatis.common.query.JoinTable;
import io.choerodon.mybatis.common.query.Where;
import io.choerodon.mybatis.entity.BaseDTO;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.criteria.JoinType;
import javax.validation.constraints.NotEmpty;
import java.util.Date;

/**
 * 公司对象.
 *
 * @author jialong.zuo@hand-china.com
 * @since 2016/10/9.
 */
@MultiLanguage
@Table(name = "fnd_company_b")
@EnableExtensionAttribute
public class Company extends BaseDTO {
    public static final String FIELD_COMPANY_ID = "companyId";
    public static final String FIELD_COMPANY_CODE = "companyCode";
    public static final String FIELD_COMPANY_TYPE = "companyType";
    public static final String FIELD_ADDRESS = "address";
    public static final String FIELD_COMPANY_LEVEL_ID = "companyLevelId";
    public static final String FIELD_PARENT_COMPANY_ID = "parentCompanyId";
    public static final String FIELD_CHIEF_POSITION_ID = "chiefPositionId";
    public static final String FIELD_START_DATE_ACTIVE = "startDateActive";
    public static final String FIELD_END_DATE_ACTIVE = "endDateActive";
    public static final String FIELD_COMPANY_SHORT_NAME = "companyShortName";
    public static final String FIELD_COMPANY_FULL_NAME = "companyFullName";
    public static final String FIELD_ZIPCODE = "zipcode";
    public static final String FIELD_FAX = "fax";
    public static final String FIELD_PHONE = "phone";
    public static final String FIELD_CONTACT_PERSON = "contactPerson";
    public static final String FIELD_PARENT_COMPANY_NAME = "parentCompanyName";
    public static final String FIELD_POSITION_NAME = "positionName";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @OrderBy("DESC")
    private Long companyId;

    @NotEmpty
    @Where
    @Length(max = 30)
    @OrderBy("ASC")
    private String companyCode;

    @Where
    @Length(max = 30)
    private String companyType;

    @Transient
    @JoinCode(code = "FND.COMPANY_TYPE", joinKey = FIELD_COMPANY_TYPE)
    private String companyTypeName;

    @Length(max = 250)
    private String address;

    private Long companyLevelId;

    @Transient
    @JoinCode(code = "FND.COMPANY_LEVEL", joinKey = FIELD_COMPANY_LEVEL_ID)
    private String companyLevelName;

    @Where
    @JoinTable(name = "companyJoin", joinMultiLanguageTable = true, target = Company.class, type = JoinType.LEFT, on = {@JoinOn(joinField = Company.FIELD_COMPANY_ID), @JoinOn(joinField = BaseDTO.FIELD_LANG, joinExpression = BaseConstants.PLACEHOLDER_LOCALE)})
    private Long parentCompanyId;

    @JoinTable(name = "positionJoin", joinMultiLanguageTable = true, target = Position.class, type = JoinType.LEFT, on = {@JoinOn(joinField = Position.FIELD_POSITION_ID), @JoinOn(joinField = BaseDTO.FIELD_LANG, joinExpression = BaseConstants.PLACEHOLDER_LOCALE)})
    private Long chiefPositionId;

    private Date startDateActive;
    private Date endDateActive;

    @NotEmpty
    @MultiLanguageField
    @Length(max = 250)
    @Where(comparison = Comparison.LIKE)
    private String companyShortName;

    @NotEmpty
    @MultiLanguageField
    @Where(comparison = Comparison.LIKE)
    @Length(max = 250)
    private String companyFullName;

    @Length(max = 100)
    private String zipcode;

    @Length(max = 100)
    private String fax;

    @Length(max = 100)
    private String phone;

    @Length(max = 100)
    private String contactPerson;

    @Transient
    @JoinColumn(joinName = "companyJoin", field = Company.FIELD_COMPANY_FULL_NAME)
    private String parentCompanyName;

    @Transient
    @JoinColumn(joinName = "positionJoin", field = Position.FIELD_NAME)
    private String positionName;

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = StringUtils.trim(companyCode);
    }

    public String getCompanyType() {
        return companyType;
    }

    public void setCompanyType(String companyType) {
        this.companyType = companyType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getCompanyLevelId() {
        return companyLevelId;
    }

    public void setCompanyLevelId(Long companyLevelId) {
        this.companyLevelId = companyLevelId;
    }

    public Long getParentCompanyId() {
        return parentCompanyId;
    }

    public void setParentCompanyId(Long parentCompanyId) {
        this.parentCompanyId = parentCompanyId;
    }

    public Long getChiefPositionId() {
        return chiefPositionId;
    }

    public void setChiefPositionId(Long chiefPositionId) {
        this.chiefPositionId = chiefPositionId;
    }

    public Date getStartDateActive() {
        return startDateActive;
    }

    public void setStartDateActive(Date startDateActive) {
        this.startDateActive = startDateActive;
    }

    public Date getEndDateActive() {
        return endDateActive;
    }

    public void setEndDateActive(Date endDateActive) {
        this.endDateActive = endDateActive;
    }

    public String getCompanyShortName() {
        return companyShortName;
    }

    public void setCompanyShortName(String companyShortName) {
        this.companyShortName = StringUtils.trim(companyShortName);
    }

    public String getCompanyFullName() {
        return companyFullName;
    }

    public void setCompanyFullName(String companyFullName) {
        this.companyFullName = StringUtils.trim(companyFullName);
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getParentCompanyName() {
        return parentCompanyName;
    }

    public void setParentCompanyName(String parentCompanyName) {
        this.parentCompanyName = parentCompanyName;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public String getCompanyTypeName() {
        return companyTypeName;
    }

    public void setCompanyTypeName(String companyTypeName) {
        this.companyTypeName = companyTypeName;
    }

    public String getCompanyLevelName() {
        return companyLevelName;
    }

    public void setCompanyLevelName(String companyLevelName) {
        this.companyLevelName = companyLevelName;
    }
}
