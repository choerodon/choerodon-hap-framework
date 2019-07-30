package io.choerodon.hap.intergration.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.hap.intergration.service.AuthenticationAdapter;
import io.choerodon.hap.intergration.service.IHapAuthenticationService;
import io.choerodon.base.util.BaseConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.util.StringUtil;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Qixiangyu
 * @since 2017/3/28.
 */
@Component
public class HapAuthenticationServiceImpl implements IHapAuthenticationService, BaseConstants {

    private Logger logger = LoggerFactory.getLogger(HapAuthenticationServiceImpl.class);

    private static final Integer DEFAULT_TOKEN_EXPIRE_TIME = 24 * 60 * 60;

    private static final Integer DEFAULT_REFESH_TOKEN_EXPIRE_TIME = 30 * 24 * 60 * 60;

    private static final String redisCatalog = HAP_CACHE + "interface:auth:";

    private static ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public String getToken(AuthenticationAdapter authenticationAdapter) {

        if (!checkParm(authenticationAdapter)) {
            return null;
        }
        String accessTokenKey = redisCatalog + authenticationAdapter.getAccessTokenKey() + AUTH_ACCESS_TOKEN;
        // 先从redis里查
        String accessToken = redisTemplate.opsForValue().get(accessTokenKey);
        if (StringUtil.isEmpty(accessToken)) {
            // 更新一下redis里的token信息
            return updateToken(authenticationAdapter);
        }
        return accessToken;
    }

    @Override
    public String updateToken(AuthenticationAdapter authenticationAdapter) {

        if (!checkParm(authenticationAdapter)) {
            return null;
        }
        String type = authenticationAdapter.getGrantType().toLowerCase();
        String clientId = authenticationAdapter.getClientId();
        String clientSecret = authenticationAdapter.getClientSecret();
        String scope = authenticationAdapter.getScope();
        String username = authenticationAdapter.getAuthUsername();
        String password = authenticationAdapter.getAuthPassword();
        String accessToken = null;

        String accessTokenKey = redisCatalog + authenticationAdapter.getAccessTokenKey() + AUTH_ACCESS_TOKEN;
        String refreshTokenKey = redisCatalog + authenticationAdapter.getRefreshTokenKey() + GRANT_TYPE_REFRESH;

        StringBuilder parm = new StringBuilder();
        parm.append("grant_type=" + type).append("&client_id=" + clientId).append("&client_secret=" + clientSecret);
        if (GRANT_TYPE_PASSWORD.equalsIgnoreCase(type)) {
            parm.append("&username=" + username).append("&password=" + password);
        }
        if (StringUtil.isNotEmpty(scope)) {
            parm.append("&scope=" + scope);
        }

        String refreshToken = redisTemplate.opsForValue().get(refreshTokenKey);
        if (StringUtil.isNotEmpty(refreshToken)) {
            StringBuilder refreshParm = new StringBuilder();
            refreshParm.append("grant_type=" + GRANT_TYPE_REFRESH).append("&refresh_token=" + refreshToken)
                    .append("&client_id=" + clientId).append("&client_secret=" + clientSecret);
            Map response = getResponseData(authenticationAdapter.getAccessTokenUrl(), refreshParm);
            if (response != null) {
                Object tokenObj = response.get(AUTH_ACCESS_TOKEN);
                if (tokenObj != null) {
                    accessToken = tokenObj.toString();
                    updateRedisRecord(response, accessTokenKey, refreshTokenKey, accessToken);
                    return accessToken;
                }
            } else {
                redisTemplate.opsForValue().set(refreshTokenKey, "");
            }
        }
        Map response = getResponseData(authenticationAdapter.getAccessTokenUrl(), parm);
        if (response != null) {
            Object tokenObj = response.get(AUTH_ACCESS_TOKEN);
            if (tokenObj != null) {
                accessToken = tokenObj.toString();
                updateRedisRecord(response, accessTokenKey, refreshTokenKey, accessToken);
                return accessToken;
            }
        } else {
            // 获取token失败
            // 设置redis中token为null
            redisTemplate.opsForValue().set(accessTokenKey, "");
        }

        return accessToken;
    }

    @Override
    public void removeToken(AuthenticationAdapter authenticationAdapter) {
        if (!checkParm(authenticationAdapter)) {
            return;
        }
        String accessTokenKey = redisCatalog + authenticationAdapter.getAccessTokenKey() + AUTH_ACCESS_TOKEN;
        String refreshTokenKey = redisCatalog + authenticationAdapter.getRefreshTokenKey() + GRANT_TYPE_REFRESH;
        redisTemplate.opsForValue().set(accessTokenKey, "");
        redisTemplate.opsForValue().set(refreshTokenKey, "");
    }

    private void updateRedisRecord(Map response, String atokenKey, String rtokenKey, String accessToken) {
        // update access_token
        Integer exp = DEFAULT_TOKEN_EXPIRE_TIME;
        Object expiresObj = response.get(AUTH_EXPIRES_IN);
        if (expiresObj != null) {
            exp = Integer.parseInt(expiresObj.toString());
        }
        redisTemplate.opsForValue().set(atokenKey, accessToken, exp,
                TimeUnit.SECONDS);

        // update refresh_token
        Object refreshObj = response.get(GRANT_TYPE_REFRESH);
        if (refreshObj != null) {
            redisTemplate.opsForValue().set(rtokenKey, refreshObj.toString(), DEFAULT_REFESH_TOKEN_EXPIRE_TIME,
                    TimeUnit.SECONDS);
        }
    }

    private boolean checkParm(AuthenticationAdapter authenticationAdapter) {
        if (StringUtil.isEmpty(authenticationAdapter.getAccessTokenUrl())) {
            logger.error("access token url is null");
            return false;
        }
        if (StringUtil.isEmpty(authenticationAdapter.getClientId())) {
            logger.error("client id is null");
            return false;
        }
        if (StringUtil.isEmpty(authenticationAdapter.getClientSecret())) {
            logger.error("client secret  is null");
            return false;
        }
        return true;
    }

    // 用POST请求获取token 如果responseCode != 200 ,返回空
    private Map getResponseData(String url, StringBuilder parm) {
        Map result = null;
        HttpURLConnection connection = null;
        try {
            URL authURL = new URL(url);
            if (url.contains("https")) {
                connection = (HttpsURLConnection) authURL.openConnection();
            } else {
                connection = (HttpURLConnection) authURL.openConnection();
            }
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.connect();
            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(parm.toString().getBytes("UTF-8"));
                outputStream.flush();
            }
            if (connection.getResponseCode() != 200) {
                logger.warn("HTTP GET Request Failed with Error code : " + connection.getResponseCode());
                connection.disconnect();
                return null;
            }
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                StringBuilder results = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    results.append(line);
                }
                result = objectMapper.readValue(results.toString(), Map.class);
            }

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }
}
