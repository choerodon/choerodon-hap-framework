package io.choerodon.hap.security;

import io.choerodon.hap.function.dto.Resource;
import io.choerodon.redis.Cache;
import io.choerodon.redis.CacheManager;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.FrameworkServlet;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author njq.niu@hand-china.com
 */
@Component
public class ResourceAccessor {

    public static final ThreadLocal<Resource> CURRENT_RESOURCE = new ThreadLocal<>();

    private static final Logger logger = LoggerFactory.getLogger(ResourceAccessor.class);

    private static String APP_SERVLET_CONTEXT_KEY = FrameworkServlet.SERVLET_CONTEXT_PREFIX + "appServlet";

    private static final String CACHE_RESOURCE_URL = "resource_url";

    @Autowired
    private CacheManager cacheManager;

    private Cache<Resource> resourceCache;

    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    private void initRequestMappingHandlerMapping(HttpServletRequest request) {
        if (requestMappingHandlerMapping != null) {
            return;
        }

        try {
            ServletContext servletContext = request.getServletContext();
            WebApplicationContext appContext = WebApplicationContextUtils.getWebApplicationContext(servletContext, APP_SERVLET_CONTEXT_KEY);
            Map<String, HandlerMapping> allRequestMappings = BeanFactoryUtils.beansOfTypeIncludingAncestors(appContext,
                    HandlerMapping.class, true, false);
            for (HandlerMapping handlerMapping : allRequestMappings.values()) {
                if (handlerMapping instanceof RequestMappingHandlerMapping) {
                    requestMappingHandlerMapping = (RequestMappingHandlerMapping) handlerMapping;
                    break;
                }
            }
        } catch (Exception e) {
        }
    }

    public Resource getResourceOfUri(HttpServletRequest request, String uriOri, String uriTrim) {
        if (resourceCache == null) {
            resourceCache = cacheManager.getCache(CACHE_RESOURCE_URL);
        }
        Resource resource = resourceCache.getValue(uriTrim);
        if (resource == null) {
            if (logger.isWarnEnabled()) {
                logger.debug("url {} is not registered", uriTrim);
            }
            // 所有的 html 都是通过 pattern 映射的,但是这些 pattern 都没有注册
            // 每个 html 的路径都是单独注册,对于一个 html url,如果在 cache 中没找到,说明这个 url 没有注册
            // 这种情况,没必要再去解析 这个 url 对应哪个 pattern
            if (!StringUtils.endsWith(uriOri, ".html")) {
                String pattern = getBestMatchPattern(request, uriOri);
                if (Objects.equals(pattern, uriOri)) {
                    return null;
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("{} match pattern {}", uriOri, pattern);
                }
                if (pattern.startsWith("/")) {
                    pattern = pattern.substring(1);
                }
                resource = resourceCache.getValue(pattern);
                if (resource == null) {
                    if (logger.isWarnEnabled()) {
                        logger.debug("pattern {} is not registered", pattern);
                    }
                }
            }
        }
        return resource;
    }

    private String getBestMatchPattern(HttpServletRequest request, String uri) {
        initRequestMappingHandlerMapping(request);
        if (requestMappingHandlerMapping != null) {
            Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
            for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
                RequestMappingInfo requestMappingInfo = entry.getKey();
                List<String> patterns = requestMappingInfo.getPatternsCondition().getMatchingPatterns(uri);
                if (patterns.size() > 0) {
                    return patterns.get(0);
                }
            }
        }

        return uri;
    }
}
