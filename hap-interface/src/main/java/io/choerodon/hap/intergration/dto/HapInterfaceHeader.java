package io.choerodon.hap.intergration.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.base.annotation.Children;
import io.choerodon.hap.intergration.service.AuthenticationAdapter;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.entity.BaseDTO;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author xiangyu.qi@hand-china.com
 * @version 2016/11/01
 */
@Table(name = "sys_if_config_header_b")
@MultiLanguage
public class HapInterfaceHeader extends BaseDTO implements AuthenticationAdapter {

    public static final String CACHE_SEPARATOR = ":";
    public static final String FIELD_INTERFACE_CODE = "interfaceCode";

    @Id
    @OrderBy("DESC")
    private String headerId;

    @NotEmpty
    @Length(max = 30)
    private String interfaceCode;

    // 接口类型
    @NotEmpty
    @Length(max = 10)
    private String interfaceType;

    // SOAP报文前缀
    @Length(max = 2000)
    private String bodyHeader;

    // SOAP报文后缀
    @Length(max = 2000)
    private String bodyTail;

    // SOAP命名空间
    @Length(max = 30)
    private String namespace;

    // 系统地址
    @NotEmpty
    @Length(max = 200)
    private String domainUrl;

    // 请求方法
    @NotEmpty
    @Length(max = 10)
    private String requestMethod;

    // 请求形式
    @NotEmpty
    @Length(max = 30)
    private String requestFormat;

    // 请求报文格式
    @Column(name = "REQUEST_CONTENTTYPE")
    @Length(max = 80)
    private String requestContentType;

    // 请求接收类型
    @Length(max = 80)
    private String requestAccept;

    // 是否需要验证
    @NotEmpty
    @Length(max = 1)
    private String authFlag;

    // 校验用户名
    @Length(max = 80)
    private String authUsername;

    // 校验密码
    @Length(max = 200)
    private String authPassword;

    // 是否有效
    @NotEmpty
    @Length(max = 1)
    private String enableFlag;

    // 校验类型
    @Length(max = 50)
    private String authType;

    // 获取授权码的url
    @Length(max = 255)
    private String authUrl;

    // 获取token的url
    @Length(max = 255)
    private String accessTokenUrl;

    @Length(max = 255)
    private String clientId;

    @Length(max = 255)
    private String clientSecret;

    @Length(max = 255)
    private String scope;

    // 授权模式
    @Length(max = 50)
    private String grantType;

    // 包装类
    @Length(max = 255)
    private String mapperClass;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Transient
    private String lang;

    // 多语言
    @MultiLanguageField
    @NotEmpty
    @Length(max = 200)
    private String name;

    @MultiLanguageField
    @Length(max = 255)
    private String description;

    private String systemType;

    @Length(max = 255)
    private String soapAction;
    // 一行

    @Transient
    private String lineId;

    @Transient
    private String lineCode;

    @Transient
    private String iftUrl;

    @Transient
    @Children
    private List<HapInterfaceLine> lineList;

    public String getHeaderId() {
        return headerId;
    }

    public void setHeaderId(String headerId) {
        this.headerId = headerId;
    }

    public List<HapInterfaceLine> getLineList() {
        return lineList;
    }

    public void setLineList(List<HapInterfaceLine> lineList) {
        this.lineList = lineList;
    }

    public String getInterfaceCode() {
        return interfaceCode;
    }

    public void setInterfaceCode(String interfaceCode) {
        this.interfaceCode = interfaceCode;
    }

    public String getInterfaceType() {
        return interfaceType;
    }

    public void setInterfaceType(String interfaceType) {
        this.interfaceType = interfaceType;
    }

    public String getBodyHeader() {
        return bodyHeader;
    }

    public void setBodyHeader(String bodyHeader) {
        this.bodyHeader = bodyHeader;
    }

    public String getBodyTail() {
        return bodyTail;
    }

    public void setBodyTail(String bodyTail) {
        this.bodyTail = bodyTail;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getDomainUrl() {
        return domainUrl;
    }

    public void setDomainUrl(String domainUrl) {
        this.domainUrl = domainUrl;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getRequestFormat() {
        return requestFormat;
    }

    public void setRequestFormat(String requestFormat) {
        this.requestFormat = requestFormat;
    }

    public String getRequestContentType() {
        return requestContentType;
    }

    public void setRequestContentType(String requestContentType) {
        this.requestContentType = requestContentType;
    }

    public String getRequestAccept() {
        return requestAccept;
    }

    public void setRequestAccept(String requestAccept) {
        this.requestAccept = requestAccept;
    }

    public String getAuthFlag() {
        return authFlag;
    }

    public void setAuthFlag(String authFlag) {
        this.authFlag = authFlag;
    }

    public String getAuthUsername() {
        return authUsername;
    }

    public void setAuthUsername(String authUsername) {
        this.authUsername = authUsername;
    }

    public String getAuthPassword() {
        return authPassword;
    }

    public void setAuthPassword(String authPassword) {
        this.authPassword = authPassword;
    }

    public String getEnableFlag() {
        return enableFlag;
    }

    public void setEnableFlag(String enableFlag) {
        this.enableFlag = enableFlag;
    }

    public String getMapperClass() {
        return mapperClass;
    }

    public void setMapperClass(String mapperClass) {
        this.mapperClass = mapperClass;
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

    public String getSystemType() {
        return systemType;
    }

    public void setSystemType(String systemType) {
        this.systemType = systemType;
    }

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public String getLineCode() {
        return lineCode;
    }

    public void setLineCode(String lineCode) {
        this.lineCode = lineCode;
    }

    public String getIftUrl() {
        return iftUrl;
    }

    public void setIftUrl(String iftUrl) {
        this.iftUrl = iftUrl;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public String getAuthUrl() {
        return authUrl;
    }

    public void setAuthUrl(String authUrl) {
        this.authUrl = authUrl;
    }

    public String getAccessTokenUrl() {
        return accessTokenUrl;
    }

    public void setAccessTokenUrl(String accessTokenUrl) {
        this.accessTokenUrl = accessTokenUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getSoapAction() {
        return soapAction;
    }

    public void setSoapAction(String soapAction) {
        this.soapAction = soapAction;
    }

    @Override
    public String getAccessTokenKey() {
        return getInterfaceCode() + "-";
    }

    @Override
    public String getRefreshTokenKey() {
        return getInterfaceCode() + "-";
    }

}
