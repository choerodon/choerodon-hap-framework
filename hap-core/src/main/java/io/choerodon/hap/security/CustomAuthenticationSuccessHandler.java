package io.choerodon.hap.security;

import io.choerodon.hap.account.dto.User;
import io.choerodon.hap.account.service.IUserService;
import io.choerodon.hap.core.components.SysConfigManager;
import io.choerodon.hap.message.profile.SystemConfigListener;
import io.choerodon.hap.security.service.impl.UserSecurityStrategyManager;
import io.choerodon.web.core.IRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import tk.mybatis.mapper.util.StringUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 自定义登录成功Handler.
 *
 * @author hailor
 * @since 16/6/12.
 */
@Component(value = "successHandler")
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler implements SystemConfigListener {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private SysConfigManager sysConfigManager;

    @Autowired
    private IUserService userService;

    @Autowired
    private UserSecurityStrategyManager userSecurityStrategyManager;

    private RequestCache requestCache = new HttpSessionRequestCache();

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Map<String, IAuthenticationSuccessListener> listeners;

    public static final String DEFAULT_TARGET_URL = "DEFAULT_TARGET_URL";

    private static final String loginOauthUrl = "/login?oauth";
    private static final String loginUrl = "/login";
    private static final String logoutUrl = "/logout";
    private static final String indexUrl = "/index";
    private static final String refererStr = "Referer";
    private static final String loginCasUrl = "/login/cas";
    private static final String functionCodeStr = "functionCode";
    private static final String ROLE_URL = "/role";
    private static final String PASSWORD_RESET_URL = "/password/reset";

    {
        setDefaultTargetUrl("/");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        if (listeners == null) {
            listeners = applicationContext.getBeansOfType(IAuthenticationSuccessListener.class);
        }

        String referer = request.getHeader(refererStr);
        if (referer != null && referer.endsWith(loginOauthUrl)) {
            super.onAuthenticationSuccess(request, response, authentication);
            return;
        }

        clearAuthenticationAttributes(request);
        List<IAuthenticationSuccessListener> list = new ArrayList<>();
        list.addAll(listeners.values());
        Collections.sort(list);
        IAuthenticationSuccessListener successListener = null;
        String requestURI = request.getRequestURI();
        boolean isCas = requestURI.endsWith(loginCasUrl);
        try {
            for (IAuthenticationSuccessListener listener : list) {
                successListener = listener;
                successListener.onAuthenticationSuccess(request, response, authentication);
            }
            HttpSession session = request.getSession(false);
            session.setAttribute(User.LOGIN_CHANGE_INDEX, "CHANGE");
        } catch (Exception e) {
            logger.error("authentication success, but error occurred in " + successListener, e);
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            request.setAttribute("error", true);
            request.setAttribute("exception", e);
            if (isCas) {
                request.getRequestDispatcher("/casLoginFailure").forward(request, response);
            } else {
                request.getRequestDispatcher("/login").forward(request, response);
            }
            return;
        }
        // 增加了对于 standard security 支持
        //拿到登录以前的url
        String targetUrl = getDefaultTargetUrl();
        SavedRequest savedRequest = this.requestCache.getRequest(request, response);
        if (savedRequest != null) {
            List<String> savedReferer = savedRequest.getHeaderValues("referer");
            targetUrl = savedRequest.getRedirectUrl();
            if (!savedReferer.isEmpty()) {
                targetUrl = savedReferer.get(0);
            }
            if (targetUrl.endsWith(loginUrl)) {
                targetUrl = targetUrl.substring(0, targetUrl.lastIndexOf(loginUrl));
                targetUrl = targetUrl + "/";
            } else if (targetUrl.endsWith(logoutUrl)) {
                targetUrl = targetUrl.substring(0, targetUrl.lastIndexOf(logoutUrl));
                targetUrl = targetUrl + "/";
            }
            String defaultTarget = getDefaultTargetUrl();
            if (!targetUrl.contains(functionCodeStr) && !indexUrl.equalsIgnoreCase(defaultTarget) && !"/".equalsIgnoreCase(defaultTarget)) {
                targetUrl = getDefaultTargetUrl() + "?targetUrl=" + targetUrl;
            }

        }
        // 如果未开启角色合并 跳转到角色选择界面
        HttpSession session = request.getSession(false);
        if (session != null) {
            String userName = (String) session.getAttribute(User.FIELD_USER_NAME);
            if (userName != null) {
                User user = userService.selectByUserName(userName);
                List<IUserSecurityStrategy> userSecurityStrategies = userSecurityStrategyManager.getUserSecurityStrategyList();
                for (IUserSecurityStrategy userSecurityStrategy : userSecurityStrategies) {
                    ModelAndView mv = userSecurityStrategy.loginVerifyStrategy(user, request);
                    if (mv != null) {
                        this.getRedirectStrategy().sendRedirect(request, response, PASSWORD_RESET_URL);
                        return;
                    }
                }
            }
            if (!sysConfigManager.getRoleMergeFlag()) {
                Long roleId = (Long) session.getAttribute(IRequest.FIELD_ROLE_ID);
                if (roleId == null) {
                    session.setAttribute("targetUrl", targetUrl);
                    this.getRedirectStrategy().sendRedirect(request, response, ROLE_URL);
                    return;
                }
            }
        }
        this.getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    @Override
    public List<String> getAcceptedProfiles() {
        return Collections.singletonList(DEFAULT_TARGET_URL);
    }

    @Override
    public void updateProfile(String profileName, String profileValue) {
        if (StringUtil.isNotEmpty(profileValue)) {
            setDefaultTargetUrl(profileValue);
        }
    }
}
