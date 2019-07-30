package io.choerodon.hap.security.service.imp;

import io.choerodon.hap.security.CustomJdbcClientDetailsService;
import io.choerodon.hap.security.IAuthenticationSuccessListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * @author shengyang.zhou@hand-china.com
 * @author njq.niu@hand-china.com
 */
@Component
public class GenerateTokenAuthenticationSuccessListener implements IAuthenticationSuccessListener {


    @Autowired
    private AuthorizationServerTokenServices tokenServices;

    @Autowired
    private DefaultOAuth2RequestFactory defaultOAuth2RequestFactory;


    @Value("${sys.user.security.generate.accesstoken:false}")
    private boolean generateAccessToken;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        HttpSession session = request.getSession(false);
        if(generateAccessToken) {
            generateAccessToken(authentication, session);
        }
    }



    private void generateAccessToken(Authentication authentication, HttpSession session) {
        Map<String, String> authorizationParameters = new HashMap<>(5);
        authorizationParameters.put("client_id", CustomJdbcClientDetailsService.DEFAULT_CLIENT_ID);
        authorizationParameters.put("client_secret", CustomJdbcClientDetailsService.DEFAULT_CLIENT_SECRET);
        authorizationParameters.put("grant_type", "password");

        AuthorizationRequest authorizationRequest = defaultOAuth2RequestFactory.createAuthorizationRequest(authorizationParameters);
        OAuth2Request oAuth2Request = defaultOAuth2RequestFactory.createOAuth2Request(authorizationRequest);
        OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(oAuth2Request, authentication);
        OAuth2AccessToken oAuth2AccessToken = tokenServices.createAccessToken(oAuth2Authentication);

        session.setAttribute("access_token", oAuth2AccessToken.getValue());
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
