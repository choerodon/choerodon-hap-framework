package io.choerodon.hap.adaptor.impl;

import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.common.base.Throwables;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.WebUtils;

import io.choerodon.base.util.BaseConstants;
import io.choerodon.hap.account.dto.User;
import io.choerodon.hap.account.exception.RoleException;
import io.choerodon.hap.account.exception.UserException;
import io.choerodon.hap.account.service.IUserService;
import io.choerodon.hap.adaptor.ILoginAdaptor;
import io.choerodon.hap.core.components.CaptchaConfig;
import io.choerodon.hap.core.components.SysConfigManager;
import io.choerodon.hap.iam.app.service.ChoerodonRoleService;
import io.choerodon.hap.iam.app.service.RoleMemberService;
import io.choerodon.hap.iam.infra.dto.RoleDTO;
import io.choerodon.hap.iam.infra.enums.MemberType;
import io.choerodon.hap.security.TokenUtils;
import io.choerodon.hap.security.captcha.ICaptchaManager;
import io.choerodon.web.core.IRequest;
import io.choerodon.web.dto.ResponseData;
import io.choerodon.web.util.TimeZoneUtils;

/**
 * 默认登陆代理类.
 *
 * @author njq.niu@hand-china.com
 * @author xiawang.liu@hand-china.com
 * @since 2016年1月19日
 */
public class DefaultLoginAdaptor implements ILoginAdaptor {

    private static final boolean VALIDATE_CAPTCHA = true;

    /**
     * 校验码
     */
    private static final String KEY_VERIFICODE = "verifiCode";

    /**
     * 默认主页
     */
    private static final String VIEW_INDEX = "/";

    /**
     * 默认的登录页
     */
    private static final String VIEW_LOGIN = "/login";

    /**
     * 默认角色选择路径
     */
    private static final String VIEW_ROLE_SELECT = "/role";


    @Autowired
    private ICaptchaManager captchaManager;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ChoerodonRoleService roleService;

    @Autowired
    private RoleMemberService roleMemberService;

    @Autowired
    private IUserService userService;

    @Autowired
    private CaptchaConfig captchaConfig;

    @Autowired
    private SysConfigManager sysConfigManager;

    public ModelAndView doLogin(User user, HttpServletRequest request, HttpServletResponse response) {

        ModelAndView view = new ModelAndView();
        Locale locale = RequestContextUtils.getLocale(request);
        view.setViewName(getLoginView(request));
        try {
            beforeLogin(view, user, request, response);
            checkCaptcha(view, user, request, response);
            user = userService.login(user);
            HttpSession session = request.getSession(true);
            session.setAttribute(User.FIELD_USER_ID, user.getUserId());
            session.setAttribute(User.FIELD_USER_NAME, user.getUserName());
            session.setAttribute(IRequest.FIELD_LOCALE, locale.toString());
            setTimeZoneFromPreference(session, user.getUserId());
            generateSecurityKey(session);
            afterLogin(view, user, request, response);
        } catch (UserException e) {
            view.addObject("msg", messageSource.getMessage(e.getCode(), e.getParameters(), locale));
            view.addObject("code", e.getCode());
            processLoginException(view, user, e, request, response);
        }
        return view;
    }

    private void setTimeZoneFromPreference(HttpSession session, Long accountId) {
        String tz = "GMT+0800";
        if (StringUtils.isBlank(tz)) {
            tz = TimeZoneUtils.toGMTFormat(TimeZone.getDefault());
        }
        session.setAttribute(BaseConstants.PREFERENCE_TIME_ZONE, tz);
    }

    private String generateSecurityKey(HttpSession session) {
        return TokenUtils.setSecurityKey(session);
    }

    /**
     * 登陆前逻辑.
     *
     * @param view     视图
     * @param account  账号
     * @param request  请求
     * @param response 响应
     * @throws UserException 异常
     */
    protected void beforeLogin(ModelAndView view, User account, HttpServletRequest request,
                               HttpServletResponse response) throws UserException {

    }

    /**
     * 处理登陆异常.
     *
     * @param view     视图
     * @param account  账号
     * @param e        异常
     * @param request  请求
     * @param response 响应
     */
    protected void processLoginException(ModelAndView view, User account, UserException e, HttpServletRequest request,
                                         HttpServletResponse response) {

    }

    /**
     * 校验验证码是否正确.
     *
     * @param view     视图
     * @param user     账号
     * @param request  请求
     * @param response 响应
     * @throws UserException 异常
     */
    private void checkCaptcha(ModelAndView view, User user, HttpServletRequest request, HttpServletResponse response)
            throws UserException {
        if (VALIDATE_CAPTCHA) {
            Cookie cookie = WebUtils.getCookie(request, captchaManager.getCaptchaKeyName());
            String captchaCode = request.getParameter(KEY_VERIFICODE);
            if (cookie == null || StringUtils.isEmpty(captchaCode)
                    || !captchaManager.checkCaptcha(cookie.getValue(), captchaCode)) {
                throw new UserException(UserException.ERROR_INVALID_CAPTCHA, UserException.ERROR_INVALID_CAPTCHA, null);
            }
        }
    }

    /**
     * 账号登陆成功后处理逻辑.
     *
     * @param view     视图
     * @param user     账号
     * @param request  请求
     * @param response 响应
     * @throws UserException 异常
     */
    protected void afterLogin(ModelAndView view, User user, HttpServletRequest request, HttpServletResponse response)
            throws UserException {
        view.setViewName(BaseConstants.VIEW_REDIRECT + getRoleView(request));
        Cookie cookie = new Cookie(User.FIELD_USER_NAME, user.getUserName());
        cookie.setPath(StringUtils.defaultIfEmpty(request.getContextPath(), "/"));
        cookie.setMaxAge(-1);
        response.addCookie(cookie);
    }

    @Override
    public ModelAndView doSelectRole(RoleDTO role, HttpServletRequest request, HttpServletResponse response)
            throws RoleException {
        ModelAndView result = new ModelAndView();
        // 选择角色
        HttpSession session = request.getSession(false);
        if (session != null && role != null && role.getId() != null) {
            Long userId = (Long) session.getAttribute(User.FIELD_USER_ID);
            roleMemberService.checkUserRoleExists(userId, role.getId());
            if (!sysConfigManager.getRoleMergeFlag()) {
                Long[] ids = new Long[1];
                ids[0] = role.getId();
                session.setAttribute(IRequest.FIELD_ALL_ROLE_ID, ids);
            }
            session.setAttribute(IRequest.FIELD_ROLE_ID, role.getId());
            String targetUrl = (String) session.getAttribute("targetUrl");
            if (targetUrl != null) {
                result.setViewName(BaseConstants.VIEW_REDIRECT + targetUrl);
            } else {
                result.setViewName(BaseConstants.VIEW_REDIRECT + getIndexView(request));
            }
            session.removeAttribute("targetUrl");
        } else {
            result.setViewName(BaseConstants.VIEW_REDIRECT + getLoginView(request));
        }
        return result;
    }

    /**
     * 获取主界面.
     *
     * @param request HttpServletRequest
     * @return 视图
     */
    protected String getIndexView(HttpServletRequest request) {
        return VIEW_INDEX;
    }

    /**
     * 获取登陆界面.
     *
     * @param request HttpServletRequest
     * @return 视图
     */
    protected String getLoginView(HttpServletRequest request) {
        return VIEW_LOGIN;
    }


    /**
     * 获取角色选择界面.
     *
     * @param request HttpServletRequest
     * @return 视图
     */
    protected String getRoleView(HttpServletRequest request) {
        return VIEW_ROLE_SELECT;
    }

    /**
     * 集成类中可扩展此方法实现不同的userService.
     *
     * @return IUserService
     */
    public IUserService getUserService() {
        return userService;
    }

    @Override
    public ModelAndView loginView(HttpServletRequest request, HttpServletResponse response) {

        ModelAndView view = new ModelAndView(getLoginView(request));
        // 配置3次以后开启验证码
        Cookie cookie = WebUtils.getCookie(request, CaptchaConfig.LOGIN_KEY);
        if (captchaConfig.getWrongTimes() > 0) {
            if (cookie == null) {
                String uuid = UUID.randomUUID().toString();
                cookie = new Cookie(CaptchaConfig.LOGIN_KEY, uuid);
                cookie.setPath(StringUtils.defaultIfEmpty(request.getContextPath(), "/"));
                cookie.setMaxAge(captchaConfig.getExpire());
                cookie.setHttpOnly(true);
                if (SysConfigManager.useHttps) {
                    cookie.setSecure(true);
                }
                response.addCookie(cookie);
                captchaConfig.updateLoginFailureInfo(cookie);
            }
        }

        // 向前端传递是否开启验证码
        view.addObject("ENABLE_CAPTCHA", captchaConfig.isEnableCaptcha(cookie));

        Boolean error = (Boolean) request.getAttribute("error");
        Throwable exception = (Exception) request.getAttribute("exception");
        String code = UserException.ERROR_USER_PASSWORD;
        if (exception != null && !(exception instanceof BadCredentialsException)) {
            exception = Throwables.getRootCause(exception);
            code = exception.getMessage();
        }
        if (error != null && error) {
            String msg;
            Locale locale = RequestContextUtils.getLocale(request);
            msg = messageSource.getMessage(code, null, locale);
            view.addObject("msg", msg);
        }
        view.addObject("systemInfo", sysConfigManager.getSystemInfo());
        return view;
    }

    @Override
    public ModelAndView roleView(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new ModelAndView(getRoleView(request));
        HttpSession session = request.getSession(false);
        mv.addObject("systemInfo", sysConfigManager.getSystemInfo());
        if (session != null) {
            // 获取user
            Long userId = (Long) session.getAttribute(User.FIELD_USER_ID);
            if (userId != null) {
                User user = new User();
                user.setUserId(userId);
                user.setUserName((String) session.getAttribute(User.FIELD_USER_NAME));
                session.setAttribute(User.FIELD_USER_ID, userId);
                addCookie(User.FIELD_USER_ID, userId.toString(), request, response);
                List<RoleDTO> roles = roleService.selectEnableRolesInfoByMemberIdAndMemberType(userId, MemberType.USER.value());
                mv.addObject("roles", roles);
            }
        }
        return mv;
    }

    protected void addCookie(String cookieName, String cookieValue, HttpServletRequest request,
                             HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setPath(StringUtils.defaultIfEmpty(request.getContextPath(), "/"));
        cookie.setMaxAge(-1);
        response.addCookie(cookie);
    }

    @Override
    public ResponseData sessionExpiredLogin(User account, HttpServletRequest request, HttpServletResponse response)
            throws RoleException {
        ResponseData data = new ResponseData();
        ModelAndView view = this.doLogin(account, request, response);
        ModelMap mm = view.getModelMap();
        if (mm.containsAttribute("code")) {
            data.setSuccess(false);
            data.setCode((String) mm.get("code"));
            data.setMessage((String) mm.get("msg"));
        } else {
            Object userIdObj = request.getParameter(User.FIELD_USER_ID);
            Object roleIdObj = request.getParameter(IRequest.FIELD_ROLE_ID);
            if (userIdObj != null && roleIdObj != null) {
                Long userId = Long.valueOf(userIdObj.toString());
                Long roleId = Long.valueOf(roleIdObj.toString());
                roleMemberService.checkUserRoleExists(userId, roleId);
                HttpSession session = request.getSession();
                session.setAttribute(User.FIELD_USER_ID, userId);
                session.setAttribute(IRequest.FIELD_ROLE_ID, roleId);
            }
        }
        return data;
    }
}
