package io.choerodon.hap.account.dto;

public class UserInfo {
    private String email;
    private String locale;
    private String userName;
    private Long userId;
    private String timeZone;
    private String phone;
    private Integer passwordMinLength;
    private String PasswordComplexity;
    private String token;
    private Long objectVersionNumber;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getPasswordMinLength() {
        return passwordMinLength;
    }

    public void setPasswordMinLength(Integer passwordMinLength) {
        this.passwordMinLength = passwordMinLength;
    }

    public String getPasswordComplexity() {
        return PasswordComplexity;
    }

    public void setPasswordComplexity(String passwordComplexity) {
        PasswordComplexity = passwordComplexity;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }
}
