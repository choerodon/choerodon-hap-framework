package io.choerodon.hap.intergration.service;

/**
 * Created by  peng.jiang@hand-china.com on 2017/9/29.
 */
public interface AuthenticationAdapter {

    String getGrantType();

    String getClientId();

    String getClientSecret();

    String getScope();

    String getAuthUsername();

    String getAuthPassword();

    String getAccessTokenUrl();

    String getAccessTokenKey();

    String getRefreshTokenKey();

}
