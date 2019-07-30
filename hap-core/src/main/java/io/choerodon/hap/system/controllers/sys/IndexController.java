package io.choerodon.hap.system.controllers.sys;

import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.base.util.BaseConstants;
import io.choerodon.hap.account.dto.User;
import io.choerodon.hap.account.service.IUserService;
import io.choerodon.hap.core.components.SysConfigManager;
import io.choerodon.hap.iam.app.service.ChoerodonRoleService;
import io.choerodon.hap.iam.infra.dto.RoleDTO;
import io.choerodon.hap.iam.infra.enums.MemberType;
import io.choerodon.hap.security.IUserSecurityStrategy;
import io.choerodon.hap.security.service.impl.UserSecurityStrategyManager;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.core.IRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 用户首页控制层.
 *
 * @author qiang.zeng
 */
@Controller
public class IndexController extends BaseController {
    /**
     * 默认的登录页.
     */
    private static final String VIEW_LOGIN = "/login";

    /**
     * 默认角色选择路径.
     */
    private static final String VIEW_ROLE_SELECT = "/role";
    /**
     * 默认主页名.
     */
    private static final String VIEW_INDEX_NAME = "index";

    @Autowired
    private IUserService userService;

    @Autowired
    private ChoerodonRoleService roleService;

    @Autowired
    private SysConfigManager sysConfigManager;

    @Autowired
    private UserSecurityStrategyManager userSecurityStrategyManager;

    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @GetMapping(value = "/index.html")
    public ModelAndView indexHtml(final HttpServletRequest request, final HttpServletResponse response) {
        return indexModelAndView(request, response);
    }

    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @GetMapping(value = "/")
    public ModelAndView indexView(final HttpServletRequest request, final HttpServletResponse response) {
        return indexModelAndView(request, response);
    }

    /**
     * 显示主界面.
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @return view
     */
    private ModelAndView indexModelAndView(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        ModelAndView mav = new ModelAndView(VIEW_INDEX_NAME);
        if (session != null) {
            // 获取user
            String userName = (String) session.getAttribute(User.FIELD_USER_NAME);
            Long userId = (Long) session.getAttribute(User.FIELD_USER_ID);
            if (userName != null) {
                if (session.getAttribute(User.LOGIN_CHANGE_INDEX) != null) {
                    User user = userService.selectByUserName(userName);
                    List<IUserSecurityStrategy> userSecurityStrategies = userSecurityStrategyManager.getUserSecurityStrategyList();
                    for (IUserSecurityStrategy userSecurityStrategy : userSecurityStrategies) {
                        ModelAndView mv = userSecurityStrategy.loginVerifyStrategy(user, request);
                        if (mv != null) {
                            return mv;
                        }
                    }
                    session.removeAttribute(User.LOGIN_CHANGE_INDEX);
                }
            } else {
                return new ModelAndView(BaseConstants.VIEW_REDIRECT + VIEW_LOGIN);
            }
            // 角色选择
            if (!sysConfigManager.getRoleMergeFlag()) {
                Long roleId = (Long) session.getAttribute(IRequest.FIELD_ROLE_ID);
                // 用户没有登录进入选择角色界面，否则直接进入系统
                if (roleId == null) {
                    return new ModelAndView(BaseConstants.VIEW_REDIRECT + VIEW_ROLE_SELECT);
                }
                List<RoleDTO> roles = roleService.selectEnableRolesInfoByMemberIdAndMemberType(userId, MemberType.USER.value());
                mav.addObject("SYS_USER_ROLES", roles);
                mav.addObject("CURRENT_USER_ROLE", roleId);
            }
        }
        mav.addObject("SYS_TITLE", HtmlUtils.htmlEscape(sysConfigManager.getSysTitle()));
        return mav;
    }

}
