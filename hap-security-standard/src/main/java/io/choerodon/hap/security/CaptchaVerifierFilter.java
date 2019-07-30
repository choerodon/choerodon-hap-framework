package io.choerodon.hap.security;

import io.choerodon.hap.account.exception.UserException;
import io.choerodon.hap.core.components.CaptchaConfig;
import io.choerodon.hap.security.captcha.ICaptchaManager;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author hailor
 * @since 16/6/12.
 */
@Component(value = "captchaVerifierFilter")
public class CaptchaVerifierFilter extends OncePerRequestFilter {
    @Autowired
    private ICaptchaManager captchaManager;

    @Autowired
    private CaptchaConfig captchaConfig;

    private RequestMatcher loginRequestMatcher;

    private String captchaField = "captcha";

    private String loginUrl = "/login";

    public CaptchaVerifierFilter() {
        setFilterProcessesUrl(this.loginUrl);
        setCaptchaField("verifiCode");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (captchaConfig.isEnableCaptcha(WebUtils.getCookie(httpServletRequest, CaptchaConfig.LOGIN_KEY))
                && requiresValidateCaptcha(httpServletRequest)) {
            Cookie cookie = WebUtils.getCookie(httpServletRequest, captchaManager.getCaptchaKeyName());
            String captchaCode = httpServletRequest.getParameter(getCaptchaField());
            if (cookie == null || StringUtils.isEmpty(captchaCode)
                    || !captchaManager.checkCaptcha(cookie.getValue(), captchaCode)) {
                httpServletRequest.setAttribute("error", true);
                httpServletRequest.setAttribute("code", "CAPTCHA_INVALID");
                httpServletRequest.setAttribute("exception", new UserException(UserException.LOGIN_VERIFICATION_CODE_ERROR, null));
                httpServletRequest.getRequestDispatcher("login.html").forward(httpServletRequest, httpServletResponse);
                return;
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);

    }

    public String getCaptchaField() {
        return captchaField;
    }

    public void setCaptchaField(String captchaField) {
        this.captchaField = captchaField;
    }

    public void setFilterProcessesUrl(String filterProcessesUrl) {
        this.loginRequestMatcher = new AntPathRequestMatcher(filterProcessesUrl);
    }

    protected boolean requiresValidateCaptcha(HttpServletRequest request) {
        return loginRequestMatcher.matches(request) && "POST".equalsIgnoreCase(request.getMethod());
    }
}
