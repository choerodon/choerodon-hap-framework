package io.choerodon.hap.security;

import io.choerodon.hap.CoreAutoConfiguration;
import io.choerodon.hap.security.oauth.dto.Oauth2ClientDetails;
import io.choerodon.hap.security.service.impl.CustomUserDetailsService;
import io.choerodon.redis.impl.RedisCache;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerSecurityConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.TokenStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationProcessingFilter;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenEndpointFilter;
import org.springframework.security.oauth2.provider.client.ClientDetailsUserDetailsService;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpointHandlerMapping;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.vote.ScopeVoter;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
@ComponentScan
@EnableWebSecurity
@AutoConfigureBefore(CoreAutoConfiguration.class) //CoreAutoConfiguration 内有对于/**的filter，需要先执行 OAuth2AutoConfiguration
public class OAuth2AutoConfiguration {
    @Autowired
    private PasswordManager passwordManager;

    @Bean
    @SuppressWarnings("unchecked")
    public RedisCache oauthClient() {
        RedisCache redisCache = new RedisCache();
        redisCache.setName("oauth_client");
        redisCache.setType(Oauth2ClientDetails.class);
        redisCache.setKeyField(StringUtils.split("clientId"));
        redisCache.setLoadOnStartUp(true);
        redisCache.setSqlId("io.choerodon.hap.security.oauth.mapper.Oauth2ClientDetailsMapper.selectAllClient");
        return redisCache;
    }

    @Bean(value = "tokenServices")
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setTokenStore(tokenStore());
        tokenServices.setTokenEnhancer(jwtAccessTokenConverter());
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setAccessTokenValiditySeconds(3000);
        tokenServices.setClientDetailsService(clientDetailsService());
        return tokenServices;
    }

    @Bean(value = "tokenStore")
    public CustomJwtTokenStore tokenStore() {
        return new CustomJwtTokenStore(jwtAccessTokenConverter());
    }

    @Bean(value = "OAuth2RequestFactory")
    public DefaultOAuth2RequestFactory OAuth2RequestFactory() {
        return new DefaultOAuth2RequestFactory(clientDetailsService());
    }

    @Bean(value = "jwtAccessTokenConverter")
    public CustomJwtAccessTokenConverter jwtAccessTokenConverter() {
        CustomJwtAccessTokenConverter customJwtAccessTokenConverter = new CustomJwtAccessTokenConverter();
        customJwtAccessTokenConverter.setSigningKey("handhand");
        return customJwtAccessTokenConverter;
    }

    @Bean(value = "oauthUserApprovalHandler")
    public TokenStoreUserApprovalHandler oauthUserApprovalHandler() {
        TokenStoreUserApprovalHandler tokenStoreUserApprovalHandler = new TokenStoreUserApprovalHandler();
        tokenStoreUserApprovalHandler.setTokenStore(tokenStore());
        tokenStoreUserApprovalHandler.setRequestFactory(OAuth2RequestFactory());
        tokenStoreUserApprovalHandler.setClientDetailsService(clientDetailsService());
        return tokenStoreUserApprovalHandler;
    }

    @Bean(value = "oauth2ClientDetailsUserService")
    public ClientDetailsUserDetailsService oauth2ClientDetailsUserService() {
        return new ClientDetailsUserDetailsService(clientDetailsService());
    }

    @Bean(value = "customClientAuthenticationProvider")
    public CustomAuthenticationProvider customClientAuthenticationProvider() {
        CustomAuthenticationProvider customAuthenticationProvider = new CustomAuthenticationProvider();
        customAuthenticationProvider.setUserDetailsService(oauth2ClientDetailsUserService());
        customAuthenticationProvider.setPasswordEncoder(passwordManager);
        return customAuthenticationProvider;
    }

    /**
     * oauth2认证管理器,确定用户,角色及相应的权限.
     *
     * @return oauth2认证管理器
     */
    @Bean(value = "oauth2AccessDecisionManager")
    public UnanimousBased oauth2AccessDecisionManager() {
        final List<AccessDecisionVoter<?>> accessDecisionVoters = new ArrayList<>();
        accessDecisionVoters.add(customWebExpressionVoter());
        accessDecisionVoters.add(new ScopeVoter());
        accessDecisionVoters.add(new RoleVoter());
        accessDecisionVoters.add(new AuthenticatedVoter());
        accessDecisionVoters.add(permissionVoter());
        return new UnanimousBased(accessDecisionVoters);
    }

    @ConditionalOnMissingBean
    @Bean(value = "customWebExpressionVoter")
    public CustomWebExpressionVoter customWebExpressionVoter() {
        return new CustomWebExpressionVoter();
    }

    @ConditionalOnMissingBean
    @Bean(value = "permissionVoter")
    public PermissionVoter permissionVoter() {
        return new PermissionVoter();
    }

    @Bean(value = "oauth2AuthenticationEntryPoint")
    public OAuth2AuthenticationEntryPoint oauth2AuthenticationEntryPoint() {
        return new OAuth2AuthenticationEntryPoint();
    }

    @Bean(value = "oauth2AccessDeniedHandler")
    public OAuth2AccessDeniedHandler oAuth2AccessDeniedHandler() {
        return new OAuth2AccessDeniedHandler();
    }

    /**
     * Web Application clients.
     *
     * @return Web Application clients
     */
    @Bean(value = "clientDetailsService")
    public CustomJdbcClientDetailsService clientDetailsService() {
        return new CustomJdbcClientDetailsService();
    }

    @Bean("requestContextFilter")
    public AuthenticationRequestContextFilter requestContextFilter() {
        return new AuthenticationRequestContextFilter();
    }

    @Bean(value = "clientCredentialsTokenEndpointFilter")
    public ClientCredentialsTokenEndpointFilter clientCredentialsTokenEndpointFilter() {
        ClientCredentialsTokenEndpointFilter clientCredentialsTokenEndpointFilter = new ClientCredentialsTokenEndpointFilter();
        clientCredentialsTokenEndpointFilter.setAuthenticationManager(oauth2AuthenticationManager());
        return clientCredentialsTokenEndpointFilter;
    }

    @Bean(value = "oauth2AuthenticationManager")
    public AuthenticationManager oauth2AuthenticationManager() {
        return new ProviderManager(Collections.singletonList(customClientAuthenticationProvider()));
    }

    @Configuration
    @EnableWebSecurity
    @Order(value = -1)
    public class OAuthTokenSecurityConfiguration extends AuthorizationServerSecurityConfiguration {
        @Autowired
        private ClientDetailsService clientDetailsService;

        @Autowired
        private AuthorizationServerEndpointsConfiguration endpoints;

        @Override
        public AuthenticationManager authenticationManager() throws Exception {
            return oauth2AuthenticationManager();

        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            AuthorizationServerSecurityConfigurer configurer = new AuthorizationServerSecurityConfigurer();
            FrameworkEndpointHandlerMapping handlerMapping = endpoints.oauth2EndpointHandlerMapping();
            http.setSharedObject(FrameworkEndpointHandlerMapping.class, handlerMapping);
            configure(configurer);
            http.apply(configurer);
            String tokenEndpointPath = handlerMapping.getServletPath("/oauth/token");
            String tokenKeyPath = handlerMapping.getServletPath("/oauth/token_key");
            String checkTokenPath = handlerMapping.getServletPath("/oauth/check_token");
            if (!endpoints.getEndpointsConfigurer().isUserDetailsServiceOverride()) {
                UserDetailsService userDetailsService = http.getSharedObject(UserDetailsService.class);
                endpoints.getEndpointsConfigurer().userDetailsService(userDetailsService);
            }
            // @formatter:off
            http
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .csrf().disable()
                    .anonymous().disable()
                    .exceptionHandling()
                    .authenticationEntryPoint(oauth2AuthenticationEntryPoint())
                    .accessDeniedHandler(oAuth2AccessDeniedHandler())
                    .and()
                    .authorizeRequests().antMatchers(tokenEndpointPath).fullyAuthenticated()
                    .antMatchers(tokenKeyPath).access(configurer.getTokenKeyAccess())
                    .antMatchers(checkTokenPath).access(configurer.getCheckTokenAccess())
                    .anyRequest().authenticated()
                    .and()
                    .requestMatchers()
                    .antMatchers(tokenEndpointPath, tokenKeyPath, checkTokenPath)
                    .and()
                    .httpBasic().authenticationEntryPoint(oauth2AuthenticationEntryPoint())
                    .and()
                    .addFilterBefore(requestContextFilter(), AbstractPreAuthenticatedProcessingFilter.class)
                    .addFilterBefore(clientCredentialsTokenEndpointFilter(), BasicAuthenticationFilter.class);
            // @formatter:on
            http.setSharedObject(ClientDetailsService.class, clientDetailsService);
        }
    }

    @Configuration
    @EnableWebSecurity
    @Order(value = 12)
    public class OAuthAuthorizeSecurityConfiguration extends WebSecurityConfigurerAdapter {
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return oauth2AuthenticationManager();
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .antMatcher("/oauth/authorize")
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                    .and()
                    .csrf().disable()
                    .authorizeRequests().antMatchers(HttpMethod.GET, "/oauth/authorize").access("IS_AUTHENTICATED_ANONYMOUSLY")
                    .anyRequest().permitAll()
                    .and()
                    .formLogin().loginPage("/login?oauth")
                    .and();
        }
    }


    @Configuration
    @EnableWebSecurity
    @Order(value = 13)
    public class ExternalSecurityConfiguration extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .antMatcher("/api/**")
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .authorizeRequests().accessDecisionManager(oauth2AccessDecisionManager())
                    .and()
                    .csrf().disable()
                    .anonymous()
                    .and()
                    .exceptionHandling()
                    .authenticationEntryPoint(oauth2AuthenticationEntryPoint())
                    .accessDeniedHandler(oAuth2AccessDeniedHandler())
                    .and()
                    .authorizeRequests().antMatchers("/api/public/**").permitAll()
                    .and()
                    .authorizeRequests().antMatchers("/api/**").fullyAuthenticated()
                    .anyRequest().authenticated()
                    .and()
                    .addFilterBefore(apiResourceServer(), AbstractPreAuthenticatedProcessingFilter.class);
        }
    }

    @Configuration
    @EnableWebSecurity
    @Order(value = 14)
    public class PassThroughSecurityConfiguration extends WebSecurityConfigurerAdapter {
        private static final String MATCHER_R_API = "/r/api/**";
        private static final String ACCESS_R_API = "USER";

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .antMatcher(MATCHER_R_API)
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .authorizeRequests().accessDecisionManager(oauth2AccessDecisionManager())
                    .and()
                    .csrf().disable()
                    .anonymous()
                    .and()
                    .exceptionHandling()
                    .authenticationEntryPoint(oauth2AuthenticationEntryPoint())
                    .accessDeniedHandler(oAuth2AccessDeniedHandler())
                    .and()
                    .authorizeRequests().antMatchers(HttpMethod.GET, MATCHER_R_API).hasAnyRole(ACCESS_R_API)
                    .and()
                    .authorizeRequests().antMatchers(HttpMethod.POST, MATCHER_R_API).hasAnyRole(ACCESS_R_API)
                    .and()
                    .authorizeRequests().antMatchers(HttpMethod.PUT, MATCHER_R_API).hasAnyRole(ACCESS_R_API)
                    .and()
                    .authorizeRequests().antMatchers(HttpMethod.DELETE, MATCHER_R_API).hasAnyRole(ACCESS_R_API)
                    .anyRequest().authenticated()
                    .and()
                    .addFilterBefore(apiResourceServer(), AbstractPreAuthenticatedProcessingFilter.class);
        }
    }


    @Configuration
    @EnableWebSecurity
    @Order(value = 15)
    public class InternalSecurityConfiguration extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .antMatcher("/i/api/**")
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .authorizeRequests().accessDecisionManager(oauth2AccessDecisionManager())
                    .anyRequest().authenticated()
                    .and()
                    .csrf().disable()
                    .anonymous().disable()
                    .exceptionHandling()
                    .authenticationEntryPoint(oauth2AuthenticationEntryPoint())
                    .accessDeniedHandler(oAuth2AccessDeniedHandler())
                    .and()
                    .addFilterBefore(iapiResourceServer(), AbstractPreAuthenticatedProcessingFilter.class);
        }
    }

    /**
     * OAUTH 2 : AUTHORIZATION SERVER.
     */
    @Configuration
    @EnableAuthorizationServer
    public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

        @Autowired
        private CustomUserDetailsService customUserDetailsService;

        @Bean
        public CustomAuthenticationProvider customUserAuthenticationProvider() {
            CustomAuthenticationProvider customAuthenticationProvider = new CustomAuthenticationProvider();
            customAuthenticationProvider.setUserDetailsService(customUserDetailsService);
            customAuthenticationProvider.setPasswordEncoder(passwordManager);
            return customAuthenticationProvider;
        }

        private AuthenticationManager authenticationManager() throws Exception {
            return new ProviderManager(Collections.singletonList(customUserAuthenticationProvider()));
        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            endpoints.setClientDetailsService(clientDetailsService());
            endpoints.tokenServices(tokenServices());
            endpoints.requestFactory(OAuth2RequestFactory());
            endpoints.pathMapping("/oauth/confirm_access", "forward:/oauth_approval.html");
            endpoints.userApprovalHandler(oauthUserApprovalHandler());
            endpoints.authenticationManager(authenticationManager());
        }

    }

    public OAuth2AuthenticationProcessingFilter apiResourceServer() {
        OAuth2AuthenticationProcessingFilter apiResourceServer = new OAuth2AuthenticationProcessingFilter();
        OAuth2AuthenticationManager oAuth2AuthenticationManager = new OAuth2AuthenticationManager();
        oAuth2AuthenticationManager.setResourceId("api-resource");
        oAuth2AuthenticationManager.setTokenServices(tokenServices());
        apiResourceServer.setAuthenticationManager(oAuth2AuthenticationManager);
        return apiResourceServer;
    }

    public OAuth2AuthenticationProcessingFilter iapiResourceServer() {
        OAuth2AuthenticationProcessingFilter iapiResourceServer = new OAuth2AuthenticationProcessingFilter();
        OAuth2AuthenticationManager oAuth2AuthenticationManager = new OAuth2AuthenticationManager();
        oAuth2AuthenticationManager.setResourceId("iapi-resource");
        oAuth2AuthenticationManager.setTokenServices(tokenServices());
        iapiResourceServer.setAuthenticationManager(oAuth2AuthenticationManager);
        return iapiResourceServer;
    }
}
