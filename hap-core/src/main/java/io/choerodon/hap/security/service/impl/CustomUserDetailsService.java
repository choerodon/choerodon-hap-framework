package io.choerodon.hap.security.service.impl;

import java.util.ArrayList;
import java.util.Collection;

import io.choerodon.hap.account.dto.User;
import io.choerodon.hap.account.service.IUserService;
import io.choerodon.hap.security.CheckUserUtil;
import io.choerodon.hap.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author hailor
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private IUserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.selectByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found:" + username);
        }

        CheckUserUtil.checkUserException(user);

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        for (String role : user.getRoleCode()) {
            authorities.add(new SimpleGrantedAuthority(role));
        }

        UserDetails userDetails = new CustomUserDetails(user.getUserId(), user.getUserName(),
                user.getPasswordEncrypted(), true, true, true, true, authorities, user.getEmployeeId(), user.getEmployeeCode());
        return userDetails;
    }


}
