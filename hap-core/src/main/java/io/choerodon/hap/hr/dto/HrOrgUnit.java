package io.choerodon.hap.hr.dto;

import io.choerodon.hap.fnd.dto.Company;
import io.choerodon.base.util.BaseConstants;
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

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.criteria.JoinType;
import javax.validation.constraints.NotEmpty;

/**
 * 部门组织对象.
 *
 * @author jialong.zuo@hand-china.com
 * @since 2016/9/26.
 */
@MultiLanguage
@Table(name = "hr_org_unit_b")
@EnableExtensionAttribute
public class HrOrgUnit extends BaseDTO {

    public static final String FIELD_UNIT_ID = "unitId";
    public static final String FIELD_PARENT_ID = "parentId";
    public static final String FIELD_UNIT_CODE = "unitCode";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_MANAGER_POSITION = "managerPosition";
    public static final String FIELD_COMPANY_ID = "companyId";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_POSITION_NAME = "positionName";
    public static final String FIELD_COMPANY_NAME = "companyName";
    public static final String FIELD_PARENT_NAME = "parentName";
    public static final String FIELD_UNIT_CATEGORY = "unitCategory";
    public static final String FIELD_UNIT_CATEGORY_NAME = "unitCategoryName";
    public static final String FIELD_UNIT_TYPE = "unitType";

    @Id
    @Where
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @OrderBy("DESC")
    private Long unitId;

    @Column
    @Where
    @JoinTable(name = "unitJoin", joinMultiLanguageTable = true, target = HrOrgUnit.class, type = JoinType.LEFT, on = {@JoinOn(joinField = HrOrgUnit.FIELD_UNIT_ID), @JoinOn(joinField = BaseDTO.FIELD_LANG, joinExpression = BaseConstants.PLACEHOLDER_LOCALE)})
    @JoinTable(name = "unitJoin2", target = HrOrgUnit.class, type = JoinType.LEFT, on = {@JoinOn(joinField = HrOrgUnit.FIELD_UNIT_ID)})
    @OrderBy
    private Long parentId;

    @NotEmpty
    @Where
    @Length(max = 50)
    private String unitCode;

    @MultiLanguageField
    @Column
    @NotEmpty
    @Where(comparison = Comparison.LIKE)
    @Length(max = 100)
    private String name;

    @MultiLanguageField
    @Where(comparison = Comparison.LIKE)
    @Column
    @Length(max = 255)
    private String description;

    @JoinTable(name = "positionJoin", joinMultiLanguageTable = true, target = Position.class, type = JoinType.LEFT, on = {@JoinOn(joinField = Position.FIELD_POSITION_ID), @JoinOn(joinField = BaseDTO.FIELD_LANG, joinExpression = BaseConstants.PLACEHOLDER_LOCALE)})
    @OrderBy
    private Long managerPosition;

    @JoinTable(name = "companyJoin", joinMultiLanguageTable = true, target = Company.class, type = JoinType.LEFT, on = {@JoinOn(joinField = Company.FIELD_COMPANY_ID), @JoinOn(joinField = BaseDTO.FIELD_LANG, joinExpression = BaseConstants.PLACEHOLDER_LOCALE)})
    @JoinTable(name = "companyJoin2", target = Company.class, type = JoinType.LEFT, on = {@JoinOn(joinField = HrOrgUnit.FIELD_COMPANY_ID)})
    @OrderBy
    private Long companyId;

    @NotEmpty
    @Length(max = 1)
    @OrderBy
    private String enabledFlag;

    @Transient
    @JoinColumn(joinName = "positionJoin", field = Position.FIELD_NAME)
    @OrderBy
    private String positionName;

    @Transient
    @JoinColumn(joinName = "companyJoin", field = Company.FIELD_COMPANY_FULL_NAME)
    private String companyName;

    @Transient
    @JoinColumn(joinName = "companyJoin2", field = Company.FIELD_COMPANY_CODE)
    @OrderBy
    private String companyCode;

    @Transient
    @JoinColumn(joinName = "unitJoin", field = HrOrgUnit.FIELD_NAME)
    private String parentName;


    @Transient
    @JoinColumn(joinName = "unitJoin2", field = HrOrgUnit.FIELD_UNIT_CODE)
    @OrderBy
    private String parentCode;

    @Length(max = 50)
    @OrderBy
    private String unitCategory;

    @Transient
    @JoinCode(code = "SYS.UNIT_CATEGORY", joinKey = HrOrgUnit.FIELD_UNIT_CATEGORY)
    private String unitCategoryName;

    @Length(max = 50)
    @Where
    @OrderBy
    private String unitType;

    @Transient
    @JoinCode(code = "SYS.UNIT_TYPE", joinKey = HrOrgUnit.FIELD_UNIT_TYPE)
    private String unitTypeName;


    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    public String getUnitCategory() {
        return unitCategory;
    }

    public void setUnitCategory(String unitCategory) {
        this.unitCategory = unitCategory;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(String enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = StringUtils.trim(unitCode);
    }

    public Long getManagerPosition() {
        return managerPosition;
    }

    public void setManagerPosition(Long managerPosition) {
        this.managerPosition = managerPosition;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getUnitCategoryName() {
        return unitCategoryName;
    }

    public void setUnitCategoryName(String unitCategoryName) {
        this.unitCategoryName = unitCategoryName;
    }

    public String getUnitTypeName() {
        return unitTypeName;
    }

    public void setUnitTypeName(String unitTypeName) {
        this.unitTypeName = unitTypeName;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }
}
