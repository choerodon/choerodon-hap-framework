package io.choerodon.hap.mail.dto;

import io.choerodon.mybatis.entity.BaseDTO;

/**
 * 邮箱账号.
 *
 * @author Clerifen Li
 */
public class MessageEmailAccountVo extends BaseDTO {

    private static final long serialVersionUID = 9164922042055278043L;

    private Long accountId;

    private Long configId;

    private String accountCode;

    private String userName;

    private String password;

    private String description;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getConfigId() {
        return configId;
    }

    public void setConfigId(Long configId) {
        this.configId = configId;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}