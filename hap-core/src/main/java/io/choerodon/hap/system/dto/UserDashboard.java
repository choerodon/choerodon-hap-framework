package io.choerodon.hap.system.dto;

import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Table(name = "sys_user_dashboard")
public class UserDashboard extends BaseDTO {

    private static final long serialVersionUID = 7078027762943933806L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userDashboardId;

    private Long userId;
    
    private Long dashboardId;

    @Transient
    private String dashboardCode;
    
    @Transient
    private String dashboardTitle;
    
    @Transient
    private String dashboardUrl;
    
    private Long dashboardSequence;

    @Transient
    private String enabledFlag;

    public Long getUserDashboardId() {
        return userDashboardId;
    }

    public void setUserDashboardId(Long userDashboardId) {
        this.userDashboardId = userDashboardId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(Long dashboardId) {
        this.dashboardId = dashboardId;
    }

    public Long getDashboardSequence() {
        return dashboardSequence;
    }

    public void setDashboardSequence(Long dashboardSequence) {
        this.dashboardSequence = dashboardSequence;
    }

    public String getDashboardTitle() {
        return dashboardTitle;
    }

    public void setDashboardTitle(String dashboardTitle) {
        this.dashboardTitle = dashboardTitle;
    }

    public String getDashboardUrl() {
        return dashboardUrl;
    }

    public void setDashboardUrl(String dashboardUrl) {
        this.dashboardUrl = dashboardUrl;
    }

    public String getDashboardCode() {
        return dashboardCode;
    }

    public void setDashboardCode(String dashboardCode) {
        this.dashboardCode = dashboardCode;
    }

    public String getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(String enabledFlag) {
        this.enabledFlag = enabledFlag;
    }
}