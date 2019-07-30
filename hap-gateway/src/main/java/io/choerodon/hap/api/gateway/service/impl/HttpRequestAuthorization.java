package io.choerodon.hap.api.gateway.service.impl;

import io.choerodon.hap.api.ApiConstants;
import io.choerodon.hap.api.gateway.dto.ApiServer;
import io.choerodon.hap.intergration.service.IHapAuthenticationService;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpRequest;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.util.StringUtil;

/**
 * auth 认证 set and get.
 *
 * @author peng.jiang@hand-china.com
 * @since 2017/9/25.
 */
@Service
public class HttpRequestAuthorization {

    private Logger logger = LoggerFactory.getLogger(ApiSoapInvokeServiceImpl.class);

    @Autowired
    private IHapAuthenticationService authenticationService;

    /**
     * 获取请求token,设置请求认证信息.
     *
     * @param httpRequest HttpRequest
     * @param server      服务
     * @param isSOAP      是否是SOAP协议
     */
    public void setHttpRequestAuthorization(HttpRequest httpRequest, ApiServer server, boolean isSOAP) {
        httpRequest.setHeader(HTTP.USER_AGENT, "Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1667.0 Safari/537.36");
        httpRequest.setHeader(HTTP.CONTENT_TYPE, "text/xml; charset=utf-8");
        if (isSOAP && server.getApiInterface().getSoapVersion().equals(ApiConstants.SOAP11) && StringUtil.isNotEmpty(server.getApiInterface().getSoapAction())) {
            httpRequest.setHeader("SOAPAction", server.getApiInterface().getSoapAction());
        }
        if (ApiConstants.AUTH_TYPE_BASIC.equalsIgnoreCase(server.getAuthType())) {
            String e1 = server.getAuthUsername() + ":" + server.getAuthPassword();
            String basicBase64 = new String(Base64.encodeBase64(e1.getBytes()));
            httpRequest.setHeader("Authorization", "Basic " + basicBase64);
        } else if (ApiConstants.AUTH_TYPE_OAUTH2.equalsIgnoreCase(server.getAuthType())) {
            String accessToken = getToken(server);
            httpRequest.setHeader("Authorization", "Bearer " + accessToken);
        }
    }


    /**
     * 获取token
     *
     * @param server 服务
     * @return token
     */
    public String getToken(ApiServer server) {
        String accessToken = authenticationService.getToken(server);
        // 获取token失败
        if (StringUtil.isEmpty(accessToken)) {
            logger.error("get access_token failure,check your config");
            throw new RuntimeException("get access_token failure,check your config");
        }
        return accessToken;
    }

    /**
     * 刷新token.
     *
     * @param httpRequest HttpRequest
     * @param server      服务
     */
    public void updateToken(HttpRequest httpRequest, ApiServer server) {
        authenticationService.updateToken(server);
        String accessToken = getToken(server);
        httpRequest.setHeader("Authorization", "Bearer " + accessToken);
    }

    /**
     * 更新token.
     *
     * @param server 服务
     * @return token
     */
    public String updateToken(ApiServer server) {
        return authenticationService.updateToken(server);
    }
}
