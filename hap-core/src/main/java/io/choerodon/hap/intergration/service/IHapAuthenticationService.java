package io.choerodon.hap.intergration.service;

/**
 * @author Qixiangyu
 * @since 2017/3/29.
 */
public interface IHapAuthenticationService {

    String AUTH_TPYE_BASIC = "BASIC_AUTH";

    String AUTH_TYPE_OAUTH2 = "OAUTH2";

    String GRANT_TYPE_CLIENT = "client_credentials";

    String GRANT_TYPE_CODE = "authorization_code";

    String GRANT_TYPE_REFRESH = "refresh_token";

    String GRANT_TYPE_PASSWORD = "password";

    String AUTH_ACCESS_TOKEN = "access_token";

    String AUTH_EXPIRES_IN = "expires_in";

    /**
     * get oauth2 token by interface define
     */
    String getToken(AuthenticationAdapter authenticationAdapter);

    /**
     * update oauth2 access_token (Stored in redis) return access_token ,if
     * update sucess ，else return null
     */
    String updateToken(AuthenticationAdapter authenticationAdapter);

    void removeToken(AuthenticationAdapter authenticationAdapter);

}
