package io.choerodon.hap.security;

import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.jasig.cas.client.validation.TicketValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@PropertySource("classpath:hap-default-cas-config.properties")
public class CasAutoConfiguration extends WebSecurityConfigurerAdapter {
    @Value("${cas.service}")
    private String casService;
    @Value("${cas.ssoserver.loginurl}")
    private String casSsoServerLoginUrl;
    @Value("${cas.ssoserver.url}")
    private String casSsoServerUrl;
    @Value("${cas.ssoserver.logouturl}")
    private String casSsoServerLogoutUrl;
    @Autowired
    private ApplicationContext context;

    @Autowired
    private CustomAuthenticationUserDetailsService customAuthenticationUserDetailsService;

    @Autowired
    private CustomAuthenticationSuccessHandler successHandler;

    @Autowired
    private CsrfSecurityRequestMatcher csrfSecurityRequestMatcher;

    @Bean
    public ServiceProperties serviceProperties() {
        ServiceProperties serviceProperties = new ServiceProperties();
        serviceProperties.setService(casService);
        serviceProperties.setSendRenew(false);
        return serviceProperties;
    }

    @Bean
    @Primary
    public AuthenticationEntryPoint authenticationEntryPoint() {
        CasAuthenticationEntryPoint entryPoint = new CasAuthenticationEntryPoint();
        entryPoint.setLoginUrl(casSsoServerLoginUrl);
        entryPoint.setServiceProperties(serviceProperties());
        return entryPoint;
    }

    @Bean
    public TicketValidator ticketValidator() {
        return new Cas20ServiceTicketValidator(casSsoServerUrl);
    }

    @Bean
    public CasAuthenticationProvider casAuthenticationProvider() {
        CasAuthenticationProvider provider = new CasAuthenticationProvider();
        provider.setServiceProperties(serviceProperties());
        provider.setTicketValidator(ticketValidator());
        provider.setAuthenticationUserDetailsService(customAuthenticationUserDetailsService);
        provider.setKey("an_id_for_this_auth_provider_only");
        return provider;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().requireCsrfProtectionMatcher(csrfSecurityRequestMatcher)
                .and()
                .authorizeRequests()
                .antMatchers("/login/**", "/resources/**", "/lib/**", "/timeout", "/websocket/**", "/sys/um/user_personal_info")
                .permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(singleSignOutFilter(), CasAuthenticationFilter.class)
                .addFilterBefore(logoutFilter(), LogoutFilter.class)
                .httpBasic()
                .authenticationEntryPoint(authenticationEntryPoint());
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
                .ignoring().antMatchers("/websocket/info")
                .and()
                .ignoring().antMatchers("/sockjs**");
    }

    @Bean
    public SecurityContextLogoutHandler securityContextLogoutHandler() {
        return new SecurityContextLogoutHandler();
    }

    @Bean
    public LogoutFilter logoutFilter() {
        LogoutFilter logoutFilter = new LogoutFilter(casSsoServerLogoutUrl, securityContextLogoutHandler());
        logoutFilter.setFilterProcessesUrl("/logout");
        return logoutFilter;
    }

    @Bean
    public SingleSignOutFilter singleSignOutFilter() {
        SingleSignOutFilter singleSignOutFilter = new SingleSignOutFilter();
        singleSignOutFilter.setCasServerUrlPrefix("");
        singleSignOutFilter.setIgnoreInitConfiguration(true);
        return singleSignOutFilter;
    }

    @Bean
    public CasAuthenticationFilter casAuthenticationFilter() throws Exception {
        CasAuthenticationFilter casAuthenticationFilter = new CasAuthenticationFilter();
        casAuthenticationFilter.setAuthenticationFailureHandler(new CasLoginFailureHandler());
        casAuthenticationFilter.setAuthenticationSuccessHandler(successHandler);
        casAuthenticationFilter.setAuthenticationManager(authenticationManager());
        return casAuthenticationFilter;
    }

    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return new ProviderManager(Collections.singletonList(casAuthenticationProvider()));
    }
}
