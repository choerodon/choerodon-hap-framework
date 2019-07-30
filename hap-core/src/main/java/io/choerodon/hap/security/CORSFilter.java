package io.choerodon.hap.security;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;
import org.springframework.web.cors.*;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

/**
 * 跨域请求过滤器.
 *
 * @author jialong.zuo@hand-china.com
 * @since 2017-3-16
 **/
public class CORSFilter extends OncePerRequestFilter {
    private CorsProcessor processor;
    private UrlBasedCorsConfigurationSource source;
    private CorsConfiguration config;
    private CorsConfigurationSource configSource;
    private FilterConfig configConfig;
    private String allowedMappings;
    private String credential;
    private String origin;
    private String method;
    private String exposeHeader;
    private String header;
    private String maxAge;

    public CORSFilter() {
        processor = new DefaultCorsProcessor();
        source = new UrlBasedCorsConfigurationSource();
        config = new CorsConfiguration();
    }

    @Override
    protected void initBeanWrapper(BeanWrapper bw) throws BeansException {
        configConfig = getFilterConfig();
        allowedMappings = configConfig.getInitParameter("allowedMappings");
        credential = configConfig.getInitParameter("allowCredentials");
        origin = configConfig.getInitParameter("allowedOrigin");
        header = configConfig.getInitParameter("allowedHeader");
        method = configConfig.getInitParameter("allowedMethod");
        exposeHeader = configConfig.getInitParameter("exposedHeader");
        maxAge = configConfig.getInitParameter("maxAge");
        initCorsConfig();
    }

    private void initCorsConfig(){
        if (null == allowedMappings) {
            ///no mapping
            logger.error("No mapping with cors filter");
        } else {

            if (null != credential) {
                config.setAllowCredentials(Boolean.parseBoolean(credential));
            }
            if (null != origin) {
                config.setAllowedOrigins(Arrays.asList(origin.split(";")));
            } else {
                config.addAllowedOrigin("*");
            }
            if (null != header) {
                config.setAllowedHeaders(Arrays.asList(header.split(";")));
            } else {
                config.addAllowedHeader("*");
            }
            if (null != method) {
                config.setAllowedMethods(Arrays.asList(method.split(";")));
            } else {
                config.addAllowedMethod("*");
            }
            if (null != exposeHeader) {
                config.setExposedHeaders(Arrays.asList(exposeHeader.split(";")));
            }
            if (null != maxAge) {
                config.setMaxAge(Long.parseLong(maxAge));
            }

            String[] mappings = allowedMappings.split(";");
            for (String mapping : mappings) {
                source.registerCorsConfiguration(mapping, config);
            }
            this.configSource = source;
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        if (CorsUtils.isCorsRequest(httpServletRequest)) {
            CorsConfiguration corsConfiguration = this.configSource.getCorsConfiguration(httpServletRequest);
            if (corsConfiguration != null) {
                boolean isValid = this.processor.processRequest(corsConfiguration, httpServletRequest, httpServletResponse);
                if (!isValid || CorsUtils.isPreFlightRequest(httpServletRequest)) {
                    return;
                }
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
