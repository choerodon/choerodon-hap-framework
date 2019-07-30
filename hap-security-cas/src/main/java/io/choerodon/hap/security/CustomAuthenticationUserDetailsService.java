package io.choerodon.hap.security;

import io.choerodon.hap.account.dto.User;
import io.choerodon.hap.account.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author hailor
 * @since 2016/6/16.
 */
@Component
public class CustomAuthenticationUserDetailsService implements AuthenticationUserDetailsService {

    @Autowired
    private IUserService userService;

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomAuthenticationUserDetailsService.class);

    @Override
    public UserDetails loadUserDetails(Authentication authentication) throws UsernameNotFoundException {
        User user = userService.selectByUserName(authentication.getPrincipal().toString());
        if (user == null) {
            throw new UsernameNotFoundException("User not found:" + authentication.getPrincipal().toString());
        }
        CheckUserUtil.checkUserException(user);
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        for (String role : user.getRoleCode()) {
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return new CustomUserDetails(user.getUserId(), user.getUserName(),
                user.getPasswordEncrypted(), true, true, true, true, authorities, user.getEmployeeId(), user.getEmployeeCode());
    }
}
