package io.choerodon.hap;

import io.choerodon.hap.security.CaptchaVerifierFilter;
import io.choerodon.hap.security.CsrfSecurityRequestMatcher;
import io.choerodon.hap.security.CustomAuthenticationSuccessHandler;
import io.choerodon.hap.security.CustomWebExpressionVoter;
import io.choerodon.hap.security.LoginFailureHandler;
import io.choerodon.hap.security.PasswordManager;
import io.choerodon.hap.security.PermissionVoter;
import io.choerodon.hap.security.service.impl.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AbstractAccessDecisionManager;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jiameng.cao
 * @since 2019/2/28
 */
@Configuration
@EnableWebSecurity
@ConditionalOnMissingClass("io.choerodon.hap.security.CasAutoConfiguration")
public class StandardSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private CustomAuthenticationSuccessHandler successHandler;
    @Autowired
    private LoginFailureHandler loginFailureHandler;
    @Autowired
    private CaptchaVerifierFilter captchaVerifierFilter;
    @Autowired
    private LogoutSuccessHandler logoutHandler;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private PasswordManager passwordManager;
    @Autowired
    private CsrfSecurityRequestMatcher csrfSecurityRequestMatcher;
    @Autowired
    @Qualifier("standardOrLdapAuthenticationProvider")
    private AuthenticationProvider authenticationProvider;

    @Override
    public void configure(HttpSecurity http) throws Exception {

        http.
                authenticationProvider(authenticationProvider)
                .authorizeRequests().accessDecisionManager(accessDecisionManager())
                .and()
                .csrf().requireCsrfProtectionMatcher(csrfSecurityRequestMatcher)
                .and()
                .authorizeRequests()
                .antMatchers("/login", "/login.html", "/websocket/**").permitAll()
                .antMatchers("/**").access("hasRole('ROLE_USER')")
                .and()
                .exceptionHandling()
                .accessDeniedPage("/access-denied")
                .and()
                .sessionManagement().invalidSessionUrl("/timeout")
                .and()
                .formLogin().loginPage("/login").successHandler(successHandler).failureHandler(loginFailureHandler)
                .and()
                .addFilterBefore(captchaVerifierFilter, UsernamePasswordAuthenticationFilter.class)
                .logout().logoutUrl("/logout").logoutSuccessHandler(logoutHandler)
                .and()
                .headers().defaultsDisabled().cacheControl();
    }


    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring().antMatchers("/resources/**")
                .and()
                .ignoring().antMatchers("/lib/**")
                .and()
                .ignoring().antMatchers("/timeout")
                .and()
                .ignoring().antMatchers("/verifiCode")
                .and()
                .ignoring().antMatchers("/ws/**")
                .and()
                .ignoring().antMatchers("/editor-app/**")
                .and()
                .ignoring().antMatchers("/diagram-viewer/**")
                .and()
                .ignoring().antMatchers("/activiti-editor/**")
                .and()
                .ignoring().antMatchers("/ureport/res/**")
                .and()
                .ignoring().antMatchers("/ureport/designer/**")
                .and()
                .ignoring().antMatchers("/websocket/**")
                .and()
                .ignoring().antMatchers("/sockjs**");
    }

    @Bean(value = "standardOrLdapAuthenticationProvider")
    @ConditionalOnMissingBean(name = "standardOrLdapAuthenticationProvider")
    public AuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(customUserDetailsService);
        authenticationProvider.setPasswordEncoder(passwordManager);
        return authenticationProvider;
    }

    @Bean(value = "accessDecisionManager")
    public AbstractAccessDecisionManager accessDecisionManager() {
        List<AccessDecisionVoter<?>> decisionVoters = new ArrayList<>();
        decisionVoters.add(customWebExpressionVoter());
        decisionVoters.add(roleVoter());
        decisionVoters.add(authenticatedVoter());
        decisionVoters.add(permissionVoter());
        return new UnanimousBased(decisionVoters);
    }

    @Bean(value = "customWebExpressionVoter")
    public CustomWebExpressionVoter customWebExpressionVoter() {
        return new CustomWebExpressionVoter();
    }

    @Bean(value = "roleVoter")
    public RoleVoter roleVoter() {
        return new RoleVoter();
    }

    @Bean(value = "authenticatedVoter")
    public AuthenticatedVoter authenticatedVoter() {
        return new AuthenticatedVoter();
    }

    @Bean(value = "permissionVoter")
    public PermissionVoter permissionVoter() {
        return new PermissionVoter();
    }
}