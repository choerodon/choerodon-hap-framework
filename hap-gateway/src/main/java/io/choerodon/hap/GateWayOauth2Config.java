package io.choerodon.hap;

import io.choerodon.hap.security.ApiAccessLimitFilter;
import io.choerodon.hap.security.OAuth2AutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

/**
 * @author jiameng.cao
 * @since 2019/2/27
 */

@Configuration
@EnableWebSecurity
@Order(99)
public class GateWayOauth2Config extends WebSecurityConfigurerAdapter {
    @Autowired
    ApiAccessLimitFilter accessLimitFilter;
    @Autowired
    OAuth2AutoConfiguration oAuth2AutoConfiguration;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .antMatcher("/api/rest/**")
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests().accessDecisionManager(oAuth2AutoConfiguration.oauth2AccessDecisionManager())
                .and()
                .csrf().disable()
                .anonymous()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(oAuth2AutoConfiguration.oauth2AuthenticationEntryPoint())
                .accessDeniedHandler(oAuth2AutoConfiguration.oAuth2AccessDeniedHandler())
                .and()
                .authorizeRequests().antMatchers("/api/rest/**").fullyAuthenticated()
                .anyRequest().authenticated()
                .and()
                .addFilterAfter(accessLimitFilter, FilterSecurityInterceptor.class)
                .addFilterBefore(oAuth2AutoConfiguration.apiResourceServer(), AbstractPreAuthenticatedProcessingFilter.class);

    }

}
