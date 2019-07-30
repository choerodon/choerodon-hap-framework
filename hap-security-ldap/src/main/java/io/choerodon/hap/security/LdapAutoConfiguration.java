package io.choerodon.hap.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.userdetails.InetOrgPersonContextMapper;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;

@Configuration
@EnableWebSecurity
@PropertySource("classpath:hap-default-ldap-config.properties")
public class LdapAutoConfiguration {

    @Value("${ldap.server.url}")
    private String ldapServerUrl;
    @Value("${ldap.conn.userDn}")
    private String ldapConnUserDn;
    @Value("${ldap.conn.password}")
    private String ldapConnPassword;
    @Value("${ldap.user.search.base}")
    private String ldapUserSearchBase;
    @Value("${ldap.user.search.filter}")
    private String ldapUserSearchFilter;

    @Autowired
    private LdapAuthoritiesPopulator ldapAuthoritiesPopulator;

    @Bean
    public DefaultSpringSecurityContextSource contextSource(){
        DefaultSpringSecurityContextSource contextSource = new DefaultSpringSecurityContextSource(ldapServerUrl);
        contextSource.setUserDn(ldapConnUserDn);
        contextSource.setPassword(ldapConnPassword);
        return contextSource;
    }

    @Bean
    public FilterBasedLdapUserSearch userSearch(){
        return new FilterBasedLdapUserSearch(ldapUserSearchBase, ldapUserSearchFilter, contextSource());
    }

    @Bean
    public BindAuthenticator ldapBindAuthenticator(){
        BindAuthenticator authenticator = new BindAuthenticator(contextSource());
        authenticator.setUserSearch(userSearch());
        return authenticator;
    }

    @Bean
    public InetOrgPersonContextMapper ldapUserDetailsContextMapper(){
        return new InetOrgPersonContextMapper();
    }

    @Bean("standardOrLdapAuthenticationProvider")
    public LdapAuthenticationProvider ldapAuthenticationProvider() {
        LdapAuthenticationProvider authenticationProvider = new LdapAuthenticationProvider(ldapBindAuthenticator(), ldapAuthoritiesPopulator);
        authenticationProvider.setUserDetailsContextMapper(ldapUserDetailsContextMapper());
        return authenticationProvider;
    }

}
