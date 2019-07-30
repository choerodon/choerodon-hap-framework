package io.choerodon.hap.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.hap.security.oauth.dto.Oauth2ClientDetails;
import io.choerodon.hap.security.oauth.service.IOauth2ClientDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @author Qixiangyu
 * @author njq.niu@hand-china.com
 * @since 2017/4/18.
 */
public class CustomJdbcClientDetailsService implements ClientDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomJdbcClientDetailsService.class);

    /**
     * 警告: 仅用于内部登录生成 AccessToken 使用!
     */
    public static final String DEFAULT_CLIENT_ID = "HAP_INNER_CLIENT_ID";
    public static final String DEFAULT_CLIENT_SECRET = "2d6be1aa-5e3b-4b03-b8c9-d553ea276a05";

    private static final String DEFAULT_SCOPE = "default";
    private static final String DEFAULT_RESOURCE_ID = "api-resource";
    private static final String DEFAULT_AUTHORIZED_GRANT_TYPES = "password,refresh_token";
    private static final String DEFAULT_AUTHORITIES = "authorities";

    private static final ThreadLocal<ClientDetails> CLIENT_DETAILS = new ThreadLocal<>();

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordManager passwordManager;

    @Autowired
    private IOauth2ClientDetailsService clientDetailsService;

    public ClientDetails loadInnerClient() {
        BaseClientDetails baseClientDetails = new BaseClientDetails(DEFAULT_CLIENT_ID, DEFAULT_RESOURCE_ID, DEFAULT_SCOPE, DEFAULT_AUTHORIZED_GRANT_TYPES, DEFAULT_AUTHORITIES, null);
        baseClientDetails.setClientSecret(DEFAULT_CLIENT_SECRET);
        // 默认设置1天
        baseClientDetails.setAccessTokenValiditySeconds(86400);
        return baseClientDetails;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        if (DEFAULT_CLIENT_ID.equalsIgnoreCase(clientId)) {
            return loadInnerClient();
        }
      /*  if(CLIENT_DETAILS.get() != null){
            return CLIENT_DETAILS.get();
        }*/

        Oauth2ClientDetails oauth2ClientDetails = clientDetailsService.selectByClientId(clientId);
        if (oauth2ClientDetails == null) {
            throw new NoSuchClientException("No client with requested id: " + clientId);
        }
        BaseClientDetails details = new BaseClientDetails(oauth2ClientDetails.getClientId(),
                oauth2ClientDetails.getResourceIds(), oauth2ClientDetails.getScope(),
                oauth2ClientDetails.getAuthorizedGrantTypes(), oauth2ClientDetails.getAuthorities(),
                oauth2ClientDetails.getRedirectUri());
        details.setAutoApproveScopes(StringUtils.commaDelimitedListToSet(oauth2ClientDetails.getAutoApprove()));
        details.setClientSecret(passwordManager.encode(oauth2ClientDetails.getClientSecret()));
        if (oauth2ClientDetails.getRefreshTokenValidity() != null) {
            details.setRefreshTokenValiditySeconds(oauth2ClientDetails.getRefreshTokenValidity().intValue());
        }
        if (oauth2ClientDetails.getAccessTokenValidity() != null) {
            details.setAccessTokenValiditySeconds(oauth2ClientDetails.getAccessTokenValidity().intValue());
        }
        if (oauth2ClientDetails.getAdditionalInformation() != null) {
            try {
                Map<String, Object> additionalInformation = objectMapper.readValue(oauth2ClientDetails.getAdditionalInformation(), Map.class);
                details.setAdditionalInformation(additionalInformation);
            } catch (Exception e) {
                logger.warn("Could not decode JSON for additional information: " + details, e);
            }
        }
       /* CLIENT_DETAILS.set(details);*/
        return details;
    }

    public static void clearInfo() {
        if (CLIENT_DETAILS != null) {
            CLIENT_DETAILS.remove();
        }
    }
}
