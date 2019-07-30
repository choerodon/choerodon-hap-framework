package io.choerodon.hap.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;

/**
 * 自定义UserService.
 *
 * @author hailor
 * @since 16/6/12.
 */
public class CustomUserDetails implements UserDetails,Serializable {
    private static final long serialVersionUID = 1L;
    final Long userId;
    final String userName;
    final String password;
    final boolean enabled;
    final boolean accountNonExpired;
    final boolean credentialsNonExpired;
    final boolean accountNonLocked;
    final Collection<? extends GrantedAuthority> authorities;
    final Long employeeId;
    final String employeeCode;

    public CustomUserDetails(Long userId,String userName, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.enabled = enabled;
        this.accountNonExpired = accountNonExpired;
        this.credentialsNonExpired = credentialsNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.authorities = authorities;
        this.employeeCode = null;
        this.employeeId = -1L;
    }

    public CustomUserDetails(Long userId,String userName, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities,Long employeeId,String employeeCode) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.enabled = enabled;
        this.accountNonExpired = accountNonExpired;
        this.credentialsNonExpired = credentialsNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.authorities = authorities;
        this.employeeId = employeeId;
        this.employeeCode = employeeCode;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getEmployeeId(){
        return employeeId;
    }

    public String getEmployeeCode(){
        return employeeCode;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
