package io.choerodon.hap.security;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author xiangyu.qi@hand-china.com
 * @since 2017/9/23.
 */
public class AuthenticationRequestContextFilter extends OncePerRequestFilter {

    public static final ThreadLocal<HttpServletRequest> HTTP_SERVLET_REQUEST = new ThreadLocal<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //清除ThreadLocal变量
        try {
            HTTP_SERVLET_REQUEST.set(request);
            filterChain.doFilter(request, response);
        }finally {
            HTTP_SERVLET_REQUEST.remove();
            CustomAuthenticationProvider.clearClientInfo();
            CustomJdbcClientDetailsService.clearInfo();
        }
    }
}
