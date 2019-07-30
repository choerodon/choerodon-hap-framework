package io.choerodon.hap.security;

import io.choerodon.hap.core.components.CaptchaConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录失败handler.
 *
 * @author hailor
 * @since 16/6/12.
 */
@Component(value = "loginFailureHandler")
public class LoginFailureHandler implements AuthenticationFailureHandler {
    private static final Logger log = LoggerFactory.getLogger(LoginFailureHandler.class);

    @Autowired
    private CaptchaConfig captchaConfig;


    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        if (log.isDebugEnabled()) {
            log.debug("login failed");
        }
        if (captchaConfig.getWrongTimes() > 0) {
            captchaConfig.updateLoginFailureInfo(WebUtils.getCookie(request, CaptchaConfig.LOGIN_KEY));
        }
        request.setAttribute("error", true);
        request.setAttribute("code", "LOGIN_NOT_MATCH");
        request.setAttribute("exception", exception);
        request.getRequestDispatcher("/login.html").forward(request, response);
    }

}