package io.choerodon.hap.api.gateway.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.choerodon.base.annotation.Children;
import io.choerodon.hap.intergration.service.AuthenticationAdapter;
import io.choerodon.mybatis.entity.BaseDTO;
import org.hibernate.validator.constraints.Length;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 服务 DTO.
 *
 * @author lijian.yin@hand-china.com
 **/
@Table(name = "api_config_server")
public class ApiServer extends BaseDTO implements AuthenticationAdapter {

    public static final String CACHE_SEPARATOR = ":";

    public static final String FIELD_SERVICE_ID = "serverId";
    public static final String FIELD_CODE = "code";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_SERVICE_TYPE = "serviceType";
    public static final String FIELD_PUBLISH_TYPE = "publishType";
    public static final String FIELD_DOMAIN_URL = "domainUrl";
    public static final String FIELD_MAPPING_URL = "mappingUrl";
    public static final String FIELD_ENABLE_FLAG = "enableFlag";
    public static final String FIELD_NAMESPACE = "namespace";
    public static final String FIELD_AUTH_USERNAME = "authUsername";
    public static final String FIELD_AUTH_PASSWORD = "authPassword";
    public static final String FIELD_ELEMENT_FORM_DEFAULT = "elementFormDefault";
    public static final String FIELD_WSS_PASSWORD_TYPE = "wssPasswordType";
    public static final String FIELD_USERNAME = "username";
    public static final String FIELD_PASSWORD = "password";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long serverId;

    // 服务代码
    @NotEmpty
    @Length(max = 30)
    private String code;

    // 服务名称
    @NotEmpty
    @Length(max = 200)
    private String name;

    // 服务类型
    @NotEmpty
    @Length(max = 10)
    private String serviceType;

    // 服务地址
    @NotEmpty
    @Length(max = 200)
    private String domainUrl;

    // 映射地址
    @NotEmpty
    @Length(max = 200)
    private String mappingUrl;

    // 是否启用
    @NotEmpty
    @Length(max = 1)
    private String enableFlag;

    // SOAP命名空间
    @Length(max = 30)
    private String namespace;

    // 发布类型
    @Length(max = 10)
    private String publishType;

    // 校验模式
    @Length(max = 50)
    private String authType;

    // 授权模式
    @Length(max = 50)
    private String grantType;

    //授权地址 获取token的url
    @Length(max = 255)
    private String accessTokenUrl;

    //客户端Id
    @Length(max = 255)
    private String clientId;

    //客户端secret
    @Length(max = 255)
    private String clientSecret;

    // 校验用户名
    @Length(max = 80)
    private String authUsername;

    // 校验密码
    @Length(max = 200)
    private String authPassword;

    //scope
    @Length(max = 255)
    private String scope;

    // 参数前缀标志
    @Length(max = 30)
    private String elementFormDefault;

    //加密类型
    @Length(max = 50)
    private String wssPasswordType;

    //认证用户名
    @Length(max = 255)
    private String username;

    //认证密码
    @Length(max = 255)
    private String password;

    //导入地址
    @Transient
    private String importUrl;

    //接口列表
    @Transient
    @Children
    private List<ApiInterface> interfaces;

    //接口信息
    @Transient
    @JsonIgnore
    private ApiInterface apiInterface;

    public Long getServerId() {
        return serverId;
    }

    public void setServerId(Long serverId) {
        this.serverId = serverId;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setPublishType(String publishType) {
        this.publishType = publishType;
    }

    public String getPublishType() {
        return publishType;
    }

    public void setDomainUrl(String domainUrl) {
        this.domainUrl = domainUrl;
    }

    public String getDomainUrl() {
        return domainUrl;
    }

    public void setMappingUrl(String mappingUrl) {
        this.mappingUrl = mappingUrl;
    }

    public String getMappingUrl() {
        return mappingUrl;
    }

    public void setEnableFlag(String enableFlag) {
        this.enableFlag = enableFlag;
    }

    public String getEnableFlag() {
        return enableFlag;
    }

    public void setAuthUsername(String authUsername) {
        this.authUsername = authUsername;
    }

    @Override
    public String getAuthUsername() {
        return authUsername;
    }

    public void setAuthPassword(String authPassword) {
        this.authPassword = authPassword;
    }

    @Override
    public String getAuthPassword() {
        return authPassword;
    }

    public String getImportUrl() {
        return importUrl;
    }

    public void setImportUrl(String importUrl) {
        this.importUrl = importUrl;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    @Override
    public String getAccessTokenUrl() {
        return accessTokenUrl;
    }

    public void setAccessTokenUrl(String accessTokenUrl) {
        this.accessTokenUrl = accessTokenUrl;
    }

    @Override
    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    @Override
    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public List<ApiInterface> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(List<ApiInterface> interfaces) {
        this.interfaces = interfaces;
    }

    public ApiInterface getApiInterface() {
        return apiInterface;
    }

    public void setApiInterface(ApiInterface apiInterface) {
        this.apiInterface = apiInterface;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getElementFormDefault() {
        return elementFormDefault;
    }

    public void setElementFormDefault(String elementFormDefault) {
        this.elementFormDefault = elementFormDefault;
    }

    public String getWssPasswordType() {
        return wssPasswordType;
    }

    public void setWssPasswordType(String wssPasswordType) {
        this.wssPasswordType = wssPasswordType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getAccessTokenKey() {
        return getCode() + "-";
    }

    @Override
    public String getRefreshTokenKey() {
        return getCode() + "-";
    }


}
