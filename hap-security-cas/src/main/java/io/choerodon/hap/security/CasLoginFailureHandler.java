package io.choerodon.hap.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * cas登录失败handler.
 *
 * @author qiang.zeng
 * @since 2018/7/19
 */
public class CasLoginFailureHandler implements AuthenticationFailureHandler {
    private static final Logger log = LoggerFactory.getLogger(CasLoginFailureHandler.class);

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        if (log.isDebugEnabled()) {
            log.debug("cas login failed");
        }
        request.setAttribute("exception", exception);
        request.getRequestDispatcher("/casLoginFailure").forward(request, response);
    }
}