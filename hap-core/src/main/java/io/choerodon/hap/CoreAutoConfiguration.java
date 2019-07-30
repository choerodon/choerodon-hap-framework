package io.choerodon.hap;

import com.codahale.metrics.servlet.InstrumentedFilter;
import com.codahale.metrics.servlets.AdminServlet;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.hap.core.web.FormView;
import io.choerodon.hap.core.web.HapEnhanceFilter;
import io.choerodon.hap.security.CORSFilter;
import io.choerodon.hap.security.CsrfSecurityRequestMatcher;
import io.choerodon.freemarker.ChoerodonFreemarkerAutoConfiguration;
import io.choerodon.hap.util.cache.impl.CodeRedisCacheGroupResolve;
import io.choerodon.hap.util.cache.impl.LovRedisCacheGroupResolve;
import io.choerodon.hap.util.dto.Prompt;
import io.choerodon.redis.CacheResolve;
import io.choerodon.redis.impl.HashStringRedisCacheGroup;
import io.choerodon.redis.impl.HashStringRedisCacheGroupResolve;
import io.choerodon.web.json.CustomStringDeserializer;
import io.choerodon.web.json.DateTimeDeserializer;
import io.choerodon.web.json.DateTimeSerializer;
import org.apache.commons.lang3.LocaleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.theme.SessionThemeResolver;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;
import tk.mybatis.spring.annotation.MapperScan;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@ComponentScan
@EnableAspectJAutoProxy(exposeProxy = true, proxyTargetClass = true)
@MapperScan(basePackages = "io.choerodon.**.mapper")
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 3600)
@PropertySource("classpath:hap-default-config.properties")
public class CoreAutoConfiguration {

    @Bean
    @SuppressWarnings("unchecked")
    public FilterRegistrationBean corsFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean(new CORSFilter());
        Map<String, String> initParameters = new HashMap<>();
        initParameters.put("allowedMappings", "/oauth/**;/api/**;/r/api/**");
        initParameters.put("allowedHeader", "*");
        initParameters.put("allowedOrigin", "*");
        initParameters.put("allowedMethod", "*");
        registration.setInitParameters(initParameters);
        registration.setAsyncSupported(true);
        registration.addUrlPatterns("/*");
        registration.setName("corsFilter");
        return registration;
    }

    @Bean
    @SuppressWarnings("unchecked")
    public FilterRegistrationBean encodingFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean(new CharacterEncodingFilter());
        Map<String, String> initParameters = new HashMap<>();
        initParameters.put("encoding", "UTF-8");
        initParameters.put("forceEncoding", "true");
        registration.setInitParameters(initParameters);
        registration.setAsyncSupported(true);
        registration.addUrlPatterns("/*");
        registration.setName("encodingFilter");
        return registration;
    }

    @Bean
    @SuppressWarnings("unchecked")
    public FilterRegistrationBean hapEnhanceFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean(new HapEnhanceFilter());
        registration.setAsyncSupported(true);
        registration.addUrlPatterns("/*");
        registration.setName("hapEnhanceFilter");
        return registration;
    }

    @Bean
    @SuppressWarnings("unchecked")
    public FilterRegistrationBean instrumentedFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean(new InstrumentedFilter());
        registration.setAsyncSupported(true);
        registration.addUrlPatterns("/*");
        registration.setName("instrumentedFilter");
        return registration;
    }

    @Bean
    @SuppressWarnings("unchecked")
    public ServletRegistrationBean metrics() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new AdminServlet());
        registration.setAsyncSupported(true);
        registration.addUrlMappings("/metrics/*");
        return registration;
    }

    @Bean
    public Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.serializerByType(Date.class, new DateTimeSerializer());
        builder.deserializerByType(Date.class, new DateTimeDeserializer());
        builder.deserializerByType(String.class, new CustomStringDeserializer());
        builder.featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return builder;
    }

    @Bean
    public CsrfSecurityRequestMatcher csrfSecurityRequestMatcher() {
        CsrfSecurityRequestMatcher csrfSecurityRequestMatcher = new CsrfSecurityRequestMatcher();
        List<String> excludeUrls = new ArrayList<>();
        excludeUrls.add("/**");
        excludeUrls.add("/login/**");
        excludeUrls.add("/logout/**");
        excludeUrls.add("/websocket/**");
        excludeUrls.add("/ureport/**");
        excludeUrls.add("/kendo/export");
        csrfSecurityRequestMatcher.setExcludeUrls(excludeUrls);
        return csrfSecurityRequestMatcher;
    }

    @Bean(value = "localeResolver")
    public SessionLocaleResolver sessionLocaleResolver() {
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        localeResolver.setDefaultLocale(LocaleUtils.toLocale("zh_CN"));
        return localeResolver;
    }

    @Bean(value = "themeResolver")
    public SessionThemeResolver sessionThemeResolver() {
        SessionThemeResolver themeResolver = new SessionThemeResolver();
        themeResolver.setDefaultThemeName("bootstrap");
        return themeResolver;
    }

    @Bean("formView")
    public FreeMarkerViewResolver formView() {
        FreeMarkerViewResolver formView = new FreeMarkerViewResolver();
        formView.setViewClass(FormView.class);
        formView.setOrder(1);
        ChoerodonFreemarkerAutoConfiguration.setCommonFreeMarkerViewResolverConfig(formView);
        return formView;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return jacksonObjectMapperBuilder().build();
    }
}

