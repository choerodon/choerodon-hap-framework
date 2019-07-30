package io.choerodon.hap.security;

import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

/**
 * @author qixiangyu
 */
@Component("ldapAuthoritiesPopulator")
public class SimpleRoleGrantingLdapAuthoritiesPopulator implements LdapAuthoritiesPopulator {

    protected String role = "ROLE_USER";

    @Override
    public Collection<GrantedAuthority> getGrantedAuthorities(
            DirContextOperations userData, String username) {
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}  