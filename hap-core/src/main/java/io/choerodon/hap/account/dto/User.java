package io.choerodon.hap.account.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.choerodon.hap.hr.dto.Employee;
import io.choerodon.hap.iam.infra.dto.RoleDTO;
import io.choerodon.base.annotation.Children;
import io.choerodon.mybatis.annotation.EnableExtensionAttribute;
import io.choerodon.mybatis.common.query.Comparison;
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
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

/**
 * 用户DTO.
 *
 * @author njq.niu@hand-china.com
 * @since 2016/6/9
 */
@Table(name = "sys_user")
@EnableExtensionAttribute
public class User extends BaseDTO {
    public static final String FIELD_USER_ID = "userId";
    public static final String FIELD_USER_NAME = "userName";
    public static final String FIELD_PASSWORD_ENCRYPTED = "passwordEncrypted";
    public static final String FIELD_PASSWORD = "password";
    public static final String FIELD_PASSWORD_AGAIN = "passwordAgain";
    public static final String FIELD_USER_TYPE = "userType";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_PHONE = "phone";
    public static final String FIELD_START_ACTIVE_DATE = "startActiveDate";
    public static final String FIELD_END_ACTIVE_DATE = "endActiveDate";
    public static final String FIELD_STATUS = "status";
    public static final String FIELD_LAST_LOGIN_DATE = "lastLoginDate";
    public static final String FIELD_LAST_PASSWORD_UPDATE_DATE = "lastPasswordUpdateDate";
    public static final String FIELD_FIRST_LOGIN = "firstLogin";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_CUSTOMER_ID = "customerId";
    public static final String FIELD_SUPPLIER_ID = "supplierId";
    public static final String FIELD_EMPLOYEE_ID = "employeeId";
    public static final String FIELD_EMPLOYEE_CODE = "employeeCode";
    public static final String FIELD_EMPLOYEE_NAME = "employeeName";

    public static final String LOGIN_CHANGE_INDEX = "login_change_index";
    public static final String FIRST_LOGIN_STATUS = "Y";
    public static final String NOT_FIRST_LOGIN_STATUS = "N";

    public static final String STATUS_ACTV = "ACTV";
    public static final String STATUS_EXPR = "EXPR";
    public static final String STATUS_LOCK = "LOCK";


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotEmpty
    @Length(max = 150)
    @JsonInclude(Include.NON_NULL)
    @Where
    @OrderBy("ASC")
    private String userName;

    @Where(expression = " 1=1 ")
    @Length(max = 80)
    private String passwordEncrypted;

    @Transient
    @JsonInclude(Include.NON_NULL)
    private String password;

    @Transient
    @JsonInclude(Include.NON_NULL)
    private String passwordAgain;

    @Column
    @JsonInclude(Include.NON_NULL)
    private String userType;

    @Column
    @JsonInclude(Include.NON_NULL)
    @NotEmpty
    @Email
    @Length(max = 150)
    private String email;

    @Column
    @JsonInclude(Include.NON_NULL)
    @NotEmpty
    @Length(max = 40)
    private String phone;

    @Column
    @Where(comparison = Comparison.GREATER_THAN_OR_EQUALTO)
    @JsonInclude(Include.NON_NULL)
    private Date startActiveDate;

    @Column
    @Where(comparison = Comparison.LESS_THAN_OR_EQUALTO)
    @JsonInclude(Include.NON_NULL)
    private Date endActiveDate;

    // 状态
    @JsonInclude(Include.NON_NULL)
    @Column
    @Where
    private String status;

    @Column
    @JsonInclude(Include.NON_NULL)
    private Date lastLoginDate;

    @Column
    @JsonInclude(Include.NON_NULL)
    private Date lastPasswordUpdateDate;

    @Column
    @JsonInclude(Include.NON_NULL)
    private String firstLogin;

    @Column
    @JsonInclude(Include.NON_NULL)
    @Length(max = 255)
    private String description;

    @Column
    @JsonInclude(Include.NON_NULL)
    private Long customerId;

    @Column
    @JsonInclude(Include.NON_NULL)
    private Long supplierId;

    @Column
    @JsonInclude(Include.NON_NULL)
    @JoinTable(name = "employeeJoin", target = Employee.class, type = JoinType.LEFT, on = {@JoinOn(joinField = Employee.FIELD_EMPLOYEE_ID)})
    private Long employeeId;

    @Transient
    @JsonInclude(Include.NON_NULL)
    @JoinColumn(joinName = "employeeJoin", field = Employee.FIELD_EMPLOYEE_CODE)
    @Where
    private String employeeCode;


    @Transient
    @JsonInclude(Include.NON_NULL)
    @JoinColumn(joinName = "employeeJoin", field = Employee.FIELD_NAME)
    @Where(comparison = Comparison.LIKE)
    private String employeeName;


    @Transient
    private List<String> roleCode;

    @Children
    @Transient
    private List<RoleDTO> userRoles;

    @Transient
    private Long roleId;

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getStatus() {
        return status;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public void setEmail(String email) {
        this.email = StringUtils.trim(email);
    }

    public void setPhone(String phone) {
        this.phone = StringUtils.trim(phone);
    }

    public void setStatus(String status) {
        this.status = StringUtils.trim(status);
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = StringUtils.trim(userName);
    }

    public String getPasswordEncrypted() {
        return passwordEncrypted;
    }

    public void setPasswordEncrypted(String passwordEncrypted) {
        this.passwordEncrypted = passwordEncrypted;
    }

    public Date getStartActiveDate() {
        return startActiveDate;
    }

    public void setStartActiveDate(Date startActiveDate) {
        this.startActiveDate = startActiveDate;
    }

    public Date getEndActiveDate() {
        return endActiveDate;
    }

    public void setEndActiveDate(Date endActiveDate) {
        this.endActiveDate = endActiveDate;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    @JsonIgnore
    public String getPasswordAgain() {
        return passwordAgain;
    }

    @JsonProperty
    public void setPasswordAgain(String passwordAgain) {
        this.passwordAgain = passwordAgain;
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public Date getLastPasswordUpdateDate() {
        return lastPasswordUpdateDate;
    }

    public void setLastPasswordUpdateDate(Date lastPasswordUpdateDate) {
        this.lastPasswordUpdateDate = lastPasswordUpdateDate;
    }

    public String getFirstLogin() {
        return firstLogin;
    }

    public void setFirstLogin(String firstLogin) {
        this.firstLogin = firstLogin;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(List<String> roleCode) {
        this.roleCode = roleCode;
    }

    public List<RoleDTO> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<RoleDTO> userRoles) {
        this.userRoles = userRoles;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

}