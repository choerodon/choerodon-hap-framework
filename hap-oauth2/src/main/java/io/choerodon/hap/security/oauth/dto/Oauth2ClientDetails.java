package io.choerodon.hap.security.oauth.dto;

import io.choerodon.mybatis.common.query.Where;
import io.choerodon.mybatis.entity.BaseDTO;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

/**
 * @author qixiangyu
 */
@Table(name = "sys_oauth_client_details")
public class Oauth2ClientDetails extends BaseDTO {

    public static final String FIELD_CLIENT_ID = "clientId";
    public static final String FIELD_CLIENT_SECRET = "clientSecret";
    public static final String FIELD_AUTHORIZED_GRANTTYPES = "authorizedGrantTypes";
    public static final String FIELD_RESOURCE_IDS = "resourceIds";
    public static final String FIELD_AUTHORITIES = "authorities";
    public static final String FIELD_AUTO_APPROVE = "autoApprove";
    public static final String FIELD_SCOPE = "scope";
    public static final String FIELD_ACCESS_TOKEN_VALIDITY = "accessTokenValidity";
    public static final String FIELD_REFRESH_TOKEN_VALIDITY = "refreshTokenValidity";
    public static final String FIELD_REDIRECTURI = "redirectUri";
    public static final String FIELD_ADDITIONAL_INFORMATION = "additionalInformation";

    @Id
    @OrderBy("DESC")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Where
    @Length(max = 100)
    @OrderBy("ASC")
    private String clientId;

    @Length(max = 256)
    private String clientSecret;

    @NotEmpty
    @Length(max = 256)
    private String authorizedGrantTypes;

    @Length(max = 256)
    private String resourceIds;

    @Length(max = 256)
    private String authorities;

    @Length(max = 256)
    private String autoApprove;

    @Length(max = 256)
    private String scope = "default";

    private Long accessTokenValidity;

    private Long refreshTokenValidity;

    @Column(name = "WEB_SERVER_REDIRECT_URI")
    @Length(max = 2000)
    private String redirectUri;

    @Length(max = 4000)
    private String additionalInformation;


    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setAuthorizedGrantTypes(String authorizedGrantTypes) {
        this.authorizedGrantTypes = authorizedGrantTypes;
    }

    public String getAuthorizedGrantTypes() {
        return authorizedGrantTypes;
    }

    public void setResourceIds(String resourceIds) {
        this.resourceIds = resourceIds;
    }

    public String getResourceIds() {
        return resourceIds;
    }

    public void setAuthorities(String authorities) {
        this.authorities = authorities;
    }

    public String getAuthorities() {
        return authorities;
    }

    public void setAutoApprove(String autoApprove) {
        this.autoApprove = autoApprove;
    }

    public String getAutoApprove() {
        return autoApprove;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getScope() {
        return scope;
    }

    public void setAccessTokenValidity(Long accessTokenValidity) {
        this.accessTokenValidity = accessTokenValidity;
    }

    public Long getAccessTokenValidity() {
        return accessTokenValidity;
    }

    public void setRefreshTokenValidity(Long refreshTokenValidity) {
        this.refreshTokenValidity = refreshTokenValidity;
    }

    public Long getRefreshTokenValidity() {
        return refreshTokenValidity;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

}
