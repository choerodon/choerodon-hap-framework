package io.choerodon.hap.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;

/**
 * @author shengyang.zhou@hand-china.com
 */
public interface IAuthenticationSuccessListener extends Comparable<IAuthenticationSuccessListener> {

    void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication);

    default int getOrder() {return 10;}

    @Override
    default int compareTo(IAuthenticationSuccessListener o) {
        return getOrder() - o.getOrder();
    }
}
