package io.choerodon.hap.mail.dto;

import io.choerodon.base.annotation.Children;
import io.choerodon.mybatis.common.query.Where;
import io.choerodon.mybatis.entity.BaseDTO;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 邮箱配置.
 *
 * @author Clerifen Li
 */
@Table(name = "sys_message_email_config")
public class MessageEmailConfig extends BaseDTO {

    private static final long serialVersionUID = 8742354571330468329L;
    public static final String FIELD_CONFIG_CODE = "configCode";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long configId;

    @Length(max = 50)
    @NotEmpty
    @Where
    private String configCode;

    @Length(max = 50)
    @NotEmpty
    private String host;

    @Length(max = 10)
    @NotEmpty
    private String port;

    private Long tryTimes;

    @Length(max = 240)
    private String userName;

    @Length(max = 240)
    private String password;

    @Length(max = 20)
    private String description;

    @Length(max = 1)
    private String useWhiteList;

    @Length(max = 1)
    private String enable;

    @Length(max = 1)
    private String serverEnable;

    @Children
    @Transient
    private List<MessageEmailAccount> emailAccounts;

    @Children
    @Transient
    private List<MessageEmailWhiteList> whiteLists;

    @Children
    @Transient
    private List<MessageEmailProperty> propertyLists;

    public List<MessageEmailProperty> getPropertyLists() {
        return propertyLists;
    }

    public void setPropertyLists(List<MessageEmailProperty> propertyLists) {
        this.propertyLists = propertyLists;
    }

    public List<MessageEmailAccount> getEmailAccounts() {
        return emailAccounts;
    }

    public void setEmailAccounts(List<MessageEmailAccount> emailAccounts) {
        this.emailAccounts = emailAccounts;
    }

    public List<MessageEmailWhiteList> getWhiteLists() {
        return whiteLists;
    }

    public void setWhiteLists(List<MessageEmailWhiteList> whiteLists) {
        this.whiteLists = whiteLists;
    }

    public Long getConfigId() {
        return configId;
    }

    public void setConfigId(Long configId) {
        this.configId = configId;
    }

    public String getConfigCode() {
        return configCode;
    }

    public void setConfigCode(String configCode) {
        this.configCode = StringUtils.trim(configCode);
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = StringUtils.trim(host);
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = StringUtils.trim(port);
    }

    public Long getTryTimes() {
        return tryTimes;
    }

    public void setTryTimes(Long tryTimes) {
        this.tryTimes = tryTimes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUseWhiteList() {
        return useWhiteList;
    }

    public void setUseWhiteList(String useWhiteList) {
        this.useWhiteList = useWhiteList;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = StringUtils.trim(userName);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    public String getServerEnable() {
        return serverEnable;
    }

    public void setServerEnable(String serverEnable) {
        this.serverEnable = serverEnable;
    }
}