package io.choerodon.hap.account.controllers;

import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.base.exception.BaseException;
import io.choerodon.hap.account.dto.User;
import io.choerodon.hap.account.exception.UserException;
import io.choerodon.hap.account.service.IRoleService;
import io.choerodon.hap.account.service.IUserInfoService;
import io.choerodon.hap.account.service.IUserService;
import io.choerodon.hap.function.dto.ResourceItemAssign;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.core.IRequest;
import io.choerodon.web.dto.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

/**
 * 用户控制器.
 *
 * @author njq.niu@hand-china.com
 * @since 2016/1/29
 */
@RestController
@RequestMapping(value = {"/sys/user", "/api/sys/user"})
public class UserController extends BaseController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IRoleService roleService;

    @Autowired
    private IUserInfoService userInfoService;

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/submitResourceItems")
    public ResponseData submitResourceItems(HttpServletRequest request,
                                            @RequestBody List<ResourceItemAssign> resourceItemAssignList,
                                            @RequestParam(required = false) Long userId,
                                            @RequestParam(required = false) Long functionId) {
        return new ResponseData(userService.updateResourceItemAssign(resourceItemAssignList, userId, functionId));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/deleteResourceItems")
    public ResponseData removeResourceItems(@RequestParam(required = false) Long userId,
                                            @RequestParam(required = false) Long functionId) {
        userService.deleteResourceItems(userId, functionId);
        return new ResponseData();
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/queryResourceItems")
    public ResponseData queryResourceItems(HttpServletRequest request, @RequestParam(required = false) Long userId,
                                           @RequestParam(required = false) Long functionId) {

        return new ResponseData(userService.queryResourceItems(userId, functionId));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/queryFunction")
    public ResponseData queryFunction(HttpServletRequest request, @RequestParam(required = false) Long userId) {

        return new ResponseData(userService.queryFunction(userId));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/query")
    public ResponseData queryUsers(HttpServletRequest request,
                                   @ModelAttribute User user,
                                   @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                   @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        return new ResponseData(userService.selectUsers(user, page, pagesize));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/submit")
    public ResponseData submitUsers(@RequestBody List<User> users,
                                    BindingResult result,
                                    HttpServletRequest request) throws BaseException {
        getValidator().validate(users, result);
        if (result.hasErrors()) {
            ResponseData rd = new ResponseData(false);
            rd.setMessage(getErrorMessage(result, request));
            return rd;
        }
        return new ResponseData(userService.batchUpdate(users));
    }

    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @PostMapping(value = "/update")
    public ResponseData updateUserInfo(HttpServletRequest request, @RequestBody User user) throws BaseException {
        IRequest iRequest = createRequestContext(request);
        userInfoService.update(user);
        return new ResponseData(Collections.singletonList(user));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/remove")
    public ResponseData remove(@RequestBody List<User> users) throws BaseException {
        userService.batchDelete(users);
        return new ResponseData(users);
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/{userId}/roles")
    public ResponseData queryUserAndRoles(HttpServletRequest request, @PathVariable Long userId) {

        ResponseData rd = new ResponseData();
        User user = new User();
        user.setUserId(userId);
        rd.setRows(roleService.selectRolesByUser(user));
        return rd;
    }

    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @PostMapping(value = "/password/reset")
    public ResponseData updatePassword(HttpServletRequest request, String password,
                                       String passwordAgain, Long userId) throws UserException {
        User user = new User();
        user.setUserId(userId);
        user.setPassword(password);
        userService.resetPassword(user, passwordAgain);
        return new ResponseData(true);
    }

    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @PostMapping(value = "/password/update")
    public ResponseData updatePassword(HttpServletRequest request, @RequestParam("oldPwd") String oldPwd,
                                       @RequestParam("newPwd") String newPwd, @RequestParam("newPwdAgain") String newPwdAgain) throws UserException {
        userService.updateOwnerPassword(oldPwd, newPwd, newPwdAgain);
        return new ResponseData(true);
    }

}
