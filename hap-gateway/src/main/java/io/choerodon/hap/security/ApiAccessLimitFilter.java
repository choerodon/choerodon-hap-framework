package io.choerodon.hap.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.hap.api.ApiConstants;
import io.choerodon.hap.api.application.dto.ApiAccessLimit;
import io.choerodon.hap.api.gateway.dto.ApiInterface;
import io.choerodon.hap.api.gateway.dto.ApiServer;
import io.choerodon.hap.api.gateway.service.IApiServerService;
import io.choerodon.hap.cache.impl.ApiAccessLimitCache;
import io.choerodon.hap.intergration.exception.HapApiException;
import io.choerodon.base.util.BaseConstants;
import io.choerodon.web.dto.ResponseData;
import org.apache.commons.lang.StringUtils;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * API访问限制过滤器.
 *
 * @author lijian.yin@hand-china.com
 * @since 2017/10/16.
 */
@Component(value = "accessLimitFilter")
public class ApiAccessLimitFilter extends OncePerRequestFilter {

    @Autowired()
    private ObjectMapper objectMapper;

    @Autowired
    private ApiAccessLimitCache apiAccessLimitCache;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private IApiServerService serverService;

    private static final Logger logger = LoggerFactory.getLogger(ApiAccessLimitFilter.class);

    private static final String REDIS_CATALOG = BaseConstants.HAP_CACHE + "access_limit:";

    private static final String URL_HEAD = "/api/rest/";

    private static final String CLIENT_ID = "client_id";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2Authentication oAuth2Authentication;
        try {
            //服务注册 权限控制
            if (authentication instanceof OAuth2Authentication) {
                oAuth2Authentication = (OAuth2Authentication) authentication;
                String url = StringUtils.substringAfter(request.getRequestURI(), request.getContextPath());

                if (url.startsWith(URL_HEAD)) {
                    // 是否授权
                    checkAuth(url, oAuth2Authentication);
                }
            }
        } catch (HapApiException e) {
            response.setHeader(HTTP.CONTENT_TYPE, ApiConstants.CONTENY_TYPE_JSON);
            ServletOutputStream outputStream = response.getOutputStream();
            ResponseData responseData = new ResponseData();
            responseData.setMessage(e.getMessage());
            responseData.setCode(e.getCode());
            responseData.setSuccess(false);
            outputStream.write(objectMapper.writeValueAsString(responseData).getBytes());
            return;
        }
        filterChain.doFilter(request, response);
    }

    /**
     * 是否授权.
     *
     * @param url                  路径
     * @param oAuth2Authentication 授权bean
     * @throws HapApiException  HapApi异常
     * @throws IOException      流异常
     * @throws ServletException Servlet异常
     */
    private void checkAuth(String url, OAuth2Authentication oAuth2Authentication) throws HapApiException, IOException, ServletException {
        // 分割url，serverUrl、interfaceUrl
        url = StringUtils.substringAfter(url, URL_HEAD);
        String[] temp = url.split("/");
        if (temp.length < 2) {
            throw new HapApiException(HapApiException.CODE_API_ACCESS_LIMIT, HapApiException.MSG_ERROR_API_URL);
        }

        String serverUrl = temp[temp.length - 2];
        String interfaceUrl = temp[temp.length - 1];
        ApiServer apiServer = serverService.getByMappingUrl(serverUrl, interfaceUrl);

        // server是否存在
        if (null == apiServer) {
            throw new HapApiException(HapApiException.CODE_API_ACCESS_LIMIT, HapApiException.MSG_ERROR_API_NO_SERVER);
        }

        // server 是否启用
        if (!ApiConstants.ENABLE_FLAG_Y.equalsIgnoreCase(apiServer.getEnableFlag())) {
            throw new HapApiException(HapApiException.CODE_API_ACCESS_LIMIT, HapApiException.MSG_ERROR_API_SERVER_DISABLE);
        }

        // 服务是否绑定client
        if (!oAuth2Authentication.getOAuth2Request().getScope().contains(apiServer.getCode())) {
            throw new HapApiException(HapApiException.CODE_API_ACCESS_LIMIT, HapApiException.ERROR_NOT_FOUND);
        }

        // 获取  clientId
        Map<String, String> map = oAuth2Authentication.getOAuth2Request().getRequestParameters();
        String clientId = map.get(CLIENT_ID);
        // 查询server 获取 interface
        ApiInterface apiInterface = apiServer.getApiInterface();

        // 查询访问限制记录 ApiAccessLimit
        ApiAccessLimit apiAccessLimit = null;
        if (null != apiInterface) {
            // interface是否启用
            if (!ApiConstants.ENABLE_FLAG_Y.equalsIgnoreCase(apiInterface.getEnableFlag())) {
                throw new HapApiException(HapApiException.CODE_API_ACCESS_LIMIT, HapApiException.MSG_ERROR_API_INTERFACE_DISABLE);
            }
            Map<String, ApiAccessLimit> apiAccessLimitMap
                    = apiAccessLimitCache.getValue(clientId, apiServer.getCode());
            if (null != apiAccessLimitMap) {
                String interfaceCode = apiInterface.getCode();
                apiAccessLimit = apiAccessLimitMap.get(interfaceCode);
            }
        }

        if (null == apiAccessLimit) {
            return;
        }
        // interface限制是否启用
        if (!ApiConstants.ENABLE_FLAG_Y.equalsIgnoreCase(apiAccessLimit.getAccessFlag())) {
            return;
        }

        //判定访问次数
        if (!checkAccessLimit(clientId, serverUrl, interfaceUrl, apiAccessLimit)) {
            throw new HapApiException(HapApiException.CODE_API_ACCESS_LIMIT, HapApiException.MSG_ERROR_API_OVERSTEP);
        }

    }

    /**
     * 请求次数限制判定
     *
     * @param clientId       客户端ID
     * @param serverUrl      服务路径
     * @param interfaceUrl   接口路径
     * @param apiAccessLimit 访问次数限制
     * @return 是否被限制
     */
    private boolean checkAccessLimit(String clientId, String serverUrl, String interfaceUrl, ApiAccessLimit apiAccessLimit) {
        // 1、redis获取访问次数
        String key = REDIS_CATALOG + clientId + "_" + serverUrl + "_" + interfaceUrl;
        String visitsStr = redisTemplate.opsForValue().get(key);
        Long visits = 0L;

        if (null != apiAccessLimit.getAccessFrequency()) {

            if (null == visitsStr) {
                visits = 1L;
                redisTemplate.opsForValue().set(key, visits + "", 1, TimeUnit.MINUTES);
                return true;
            } else {
                visits = Long.parseLong(visitsStr);
                //2、判定，超过时抛异常，没超过存+1缓存
                if (visits < apiAccessLimit.getAccessFrequency()) {
                    redisTemplate.opsForValue().increment(key, 1L);
                    return true;
                } else {
                    logger.debug("Too many visits");
                    return false;
                }
            }
        } else {
            return true;
        }
    }

}
