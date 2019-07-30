package io.choerodon.hap.account.controllers;

import io.choerodon.hap.account.dto.Role;
import io.choerodon.hap.account.dto.RoleExt;
import io.choerodon.hap.account.service.IRoleService;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.base.exception.BaseException;
import io.choerodon.mybatis.entity.Criteria;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.dto.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 角色控制器.
 *
 * @author shengyang.zhou@hand-china.com
 * @since 2016/6/9
 */
@RestController
@RequestMapping(value = {"/sys/role", "/api/sys/role"})
public class RoleController extends BaseController {

    @Autowired
    @Qualifier("roleServiceImpl")
    private IRoleService roleService;

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/queryRoleNotUserRole")
    public ResponseData queryRoleNotUserRoles(HttpServletRequest request, RoleExt roleExt,
                                              @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        return new ResponseData(roleService.selectRoleNotUserRoles(roleExt, page, pagesize));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/query")
    public ResponseData queryRoles(Role role, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                   @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        return new ResponseData(roleService.selectRoles(role, page, pagesize));
    }

    @PostMapping(value = "/queryById")
    public ResponseData queryRolesById(@RequestBody Role role, HttpServletRequest request) {
        Criteria criteria = new Criteria(role);
        criteria.where(Role.FIELD_ROLE_ID);
        return new ResponseData(roleService.selectOptions(role, criteria));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/submit")
    public ResponseData submitRole(@RequestBody List<Role> roles, BindingResult result,
                                   HttpServletRequest request) throws BaseException {
        getValidator().validate(roles, result);
        if (result.hasErrors()) {
            ResponseData rd = new ResponseData(false);
            rd.setMessage(getErrorMessage(result, request));
            return rd;
        }
        return new ResponseData(roleService.batchUpdate(roles));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/remove")
    public ResponseData removeRole(HttpServletRequest request, @RequestBody List<Role> roles) throws BaseException {
        roleService.batchDelete(roles);
        return new ResponseData();
    }
}
