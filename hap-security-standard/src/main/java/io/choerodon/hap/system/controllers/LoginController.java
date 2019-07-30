package io.choerodon.hap.system.controllers;

import com.codahale.metrics.annotation.Timed;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.base.exception.BaseException;
import io.choerodon.base.exception.IBaseException;
import io.choerodon.base.util.BaseConstants;
import io.choerodon.hap.account.dto.User;
import io.choerodon.hap.account.exception.RoleException;
import io.choerodon.hap.account.exception.UserException;
import io.choerodon.hap.account.service.IUserService;
import io.choerodon.hap.adaptor.ILoginAdaptor;
import io.choerodon.hap.adaptor.impl.DefaultLoginAdaptor;
import io.choerodon.hap.iam.infra.dto.RoleDTO;
import io.choerodon.hap.security.service.impl.DefaultUserSecurityStrategy;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.core.IRequest;
import io.choerodon.web.dto.ResponseData;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Locale;

/**
 * 用户登录控制层.
 *
 * @author wuyichu
 * @author njq.niu@hand-china.com
 */
@Controller
public class LoginController extends BaseController implements InitializingBean {

    @Autowired(required = false)
    private ILoginAdaptor loginAdaptor;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private IUserService userService;

    @Permission(type = ResourceType.SITE, permissionPublic = true)
    @RequestMapping(value = "/login.html")
    @Timed
    public ModelAndView loginHtml(final HttpServletRequest request, final HttpServletResponse response) {
        return getLoginAdaptor().loginView(request, response);
    }

    @Permission(type = ResourceType.SITE, permissionPublic = true)
    @RequestMapping(value = "/login")
    @Timed
    public ModelAndView loginView(final HttpServletRequest request, final HttpServletResponse response) {
        return getLoginAdaptor().loginView(request, response);
    }


    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @GetMapping(value = "/role.html")
    public ModelAndView roleHtml(final HttpServletRequest request, final HttpServletResponse response)
            throws BaseException {
        return getLoginAdaptor().roleView(request, response);
    }

    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @GetMapping(value = "/role")
    public ModelAndView roleView(final HttpServletRequest request, final HttpServletResponse response)
            throws BaseException {
        return getLoginAdaptor().roleView(request, response);
    }

    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @PostMapping(value = "/role")
    public ModelAndView selectRole(final RoleDTO role, final HttpServletRequest request,
                                   final HttpServletResponse response) throws RoleException {
        return getLoginAdaptor().doSelectRole(role, request, response);
    }

    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @RequestMapping(value = "/sys/role/change")
    public ModelAndView changeRole(HttpServletRequest request, HttpServletResponse response, Long roleId) throws RoleException {
        RoleDTO role = new RoleDTO();
        role.setId(roleId);
        return getLoginAdaptor().doSelectRole(role, request, response);
    }

    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @PostMapping(value = "/sessionExpiredLogin")
    public ResponseData sessionExpiredLogin(final User account, final HttpServletRequest request,
                                            final HttpServletResponse response) throws BaseException {
        return getLoginAdaptor().sessionExpiredLogin(account, request, response);
    }

    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @GetMapping(value = "/password/reset")
    public ModelAndView resetPasswordGet(final HttpServletRequest request) {
        // 首次登录 密码过期跳转修改密码页面
        ModelAndView view = new ModelAndView("update_password");
        HttpSession session = request.getSession(false);
        if (session != null) {
            String reason = (String) session.getAttribute(DefaultUserSecurityStrategy.PASSWORD_UPDATE_REASON);
            Locale locale = RequestContextUtils.getLocale(request);
            view.addObject("update_password_title", updatePasswordTitle(reason, locale));
        }
        return view;
    }

    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @PostMapping(value = "/password/reset")
    public ModelAndView resetPasswordPost(HttpServletRequest request, String newPwd, String newPwdAgain) throws UserException {
        // 首次登录修改密码提交，密码过期提交
        ModelAndView view = new ModelAndView("update_password");
        IRequest iRequest = createRequestContext(request);
        HttpSession session = request.getSession(false);
        String reason = (String) session.getAttribute(DefaultUserSecurityStrategy.PASSWORD_UPDATE_REASON);
        try {
            if (reason != null) {
                request.getSession(false).removeAttribute(DefaultUserSecurityStrategy.PASSWORD_UPDATE_REASON);
                userService.firstAndExpiredLoginUpdatePassword(newPwd, newPwdAgain);
                return new ModelAndView(BaseConstants.VIEW_REDIRECT + "/login");
            }
        } catch (Exception e) {
            if (e instanceof IBaseException) {
                IBaseException be = (IBaseException) e;
                Locale locale = RequestContextUtils.getLocale(request);
                String messageKey = be.getDescriptionKey();
                String message = getMessageSource().getMessage(messageKey, be.getParameters(), messageKey, locale);
                view.addObject("update_password_title", updatePasswordTitle(reason, locale));
                view.addObject("message", message);
            } else {
                throw e;
            }
        }
        return view;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (loginAdaptor == null) {
            loginAdaptor = new DefaultLoginAdaptor();
            applicationContext.getAutowireCapableBeanFactory().autowireBean(loginAdaptor);
        }
    }

    private ILoginAdaptor getLoginAdaptor() {
        return loginAdaptor;
    }

    private String updatePasswordTitle(String reason, Locale locale) {
        String reasonTitle = "";
        switch (reason) {
            case DefaultUserSecurityStrategy.PASSWORD_UPDATE_REASON_EXPIRED:
                reasonTitle = getMessageSource().getMessage("error.user.update_password", null, locale);
                break;
            case DefaultUserSecurityStrategy.PASSWORD_UPDATE_REASON_RESET:
                reasonTitle = getMessageSource().getMessage("sys.config.resetpassword", null, locale);
                break;
            default:
                break;
        }
        return reasonTitle;
    }
}
