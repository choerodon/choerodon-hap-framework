package io.choerodon.hap.account.controllers;

import io.choerodon.hap.account.dto.UserRole;
import io.choerodon.hap.account.service.IUserRoleService;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.base.exception.BaseException;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.dto.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户角色分配控制器.
 *
 * @author xiawang.liu@hand-china.com
 */

@RestController
public class UserRoleController extends BaseController {

    @Autowired
    private IUserRoleService userRoleService;

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/sys/userrole/submit")
    public ResponseData submitUserRole(HttpServletRequest request, @RequestBody List<UserRole> userRoles,
                                       BindingResult result) throws BaseException {
        getValidator().validate(userRoles, result);
        if (result.hasErrors()) {
            ResponseData rd = new ResponseData(false);
            rd.setMessage(getErrorMessage(result, request));
            return rd;
        }
        return new ResponseData(userRoleService.batchUpdate(userRoles));
    }

}
