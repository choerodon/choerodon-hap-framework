package io.choerodon.hap.account.controllers;

import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.base.util.BaseConstants;
import io.choerodon.hap.account.dto.User;
import io.choerodon.hap.account.dto.UserInfo;
import io.choerodon.hap.account.dto.UserRoleInfo;
import io.choerodon.hap.account.exception.UserException;
import io.choerodon.hap.account.service.IUserInfoService;
import io.choerodon.hap.core.components.SysConfigManager;
import io.choerodon.hap.iam.app.service.ChoerodonRoleService;
import io.choerodon.hap.iam.infra.dto.RoleDTO;
import io.choerodon.hap.iam.infra.enums.MemberType;
import io.choerodon.hap.security.PasswordManager;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.core.IRequest;
import io.choerodon.web.core.impl.RequestHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 用户管理控制器.
 *
 * @author Zhaoqi
 * @author njq.niu@hand-china.com
 */
@RestController
public class UserInfoController extends BaseController {
    private final Logger logger = LoggerFactory.getLogger(UserInfoController.class);

    @Autowired
    private IUserInfoService userInfoService;

    @Autowired
    private PasswordManager passwordManager;

    @Autowired
    private SysConfigManager sysConfigManager;

    @Autowired
    private ChoerodonRoleService roleService;

    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @GetMapping(value = "/sys/um/sys_user_info.html")
    public ModelAndView userInfo(final HttpServletRequest request) throws UserException {
        ModelAndView mv = new ModelAndView(getViewPath() + "/sys/um/sys_user_info");
        IRequest requestContext = RequestHelper.getCurrentRequest();
        User user = userInfoService.selectUserByPrimaryKey(requestContext.getUserId());
        Integer length = passwordManager.getPasswordMinLength();
        String complexity = passwordManager.getPasswordComplexity();
        mv.addObject("user", user);
        mv.addObject("length", length);
        mv.addObject("complexity", complexity);
        return mv;
    }

    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @GetMapping(value = {"/sys/um/user_personal_info", "/api/sys/um/user_personal_info"})
    public UserInfo personalInfo(final HttpServletRequest request) throws UserException {
        IRequest requestContext = RequestHelper.getCurrentRequest();
        if (requestContext.getUserId() == -1L) {
            logger.warn("IRequest is null,can't get user personal info!");
            return null;
        }
        User user = userInfoService.selectUserByPrimaryKey(requestContext.getUserId());
        if (user == null) {
            logger.warn("user {} not found.", requestContext.getUserId());
            return null;
        }
        HttpSession session = request.getSession(false);
        UserInfo info = new UserInfo();
        info.setUserId(user.getUserId());
        info.setEmail(user.getEmail());
        info.setUserName(user.getUserName());
        info.setLocale(requestContext.getLocale());
        if (session != null){
            info.setTimeZone((String) session.getAttribute(BaseConstants.PREFERENCE_TIME_ZONE));
        }
        info.setPhone(user.getPhone());
        info.setToken(user.get_token());
        info.setPasswordMinLength(passwordManager.getPasswordMinLength());
        info.setPasswordComplexity(passwordManager.getPasswordComplexity());
        info.setObjectVersionNumber(user.getObjectVersionNumber());
        return info;
    }


    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @GetMapping(value = "/sys/user/roles")
    public UserRoleInfo userRoleInfo() {
        IRequest requestContext = RequestHelper.getCurrentRequest();
        if (requestContext.getUserId() == -1L) {
            logger.warn("IRequest is null,can't get user role info!");
            return null;
        }
        UserRoleInfo userRoleInfo = new UserRoleInfo();
        userRoleInfo.setRoleMergeFlag(true);
        if (!sysConfigManager.getRoleMergeFlag()) {
            List<RoleDTO> roles = roleService.selectEnableRolesInfoByMemberIdAndMemberType(requestContext.getUserId(), MemberType.USER.value());
            userRoleInfo.setRoleMergeFlag(false);
            userRoleInfo.setActiveUserRoles(roles);
            userRoleInfo.setCurrentUserRoleId(requestContext.getRoleId());
        }
        return userRoleInfo;
    }


}
