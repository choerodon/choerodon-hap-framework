package io.choerodon.hap.function.controllers;

import io.choerodon.hap.core.annotation.CheckToken;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.base.exception.BaseException;
import io.choerodon.hap.function.dto.MenuItem;
import io.choerodon.hap.function.dto.ResourceItemAssign;
import io.choerodon.hap.function.dto.RoleFunction;
import io.choerodon.hap.function.service.IFunctionService;
import io.choerodon.hap.function.service.IRoleFunctionService;
import io.choerodon.hap.function.service.IRoleResourceItemService;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.dto.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 对角色功能分配的操作.
 *
 * @author liuxiawang
 * @author njq.niu@hand-china.com
 * @author qiang.zeng@hand-china.com
 */
@RestController
@RequestMapping(value = {"/sys/rolefunction", "/api/sys/rolefunction"})
public class RoleFunctionController extends BaseController {

    @Autowired
    private IRoleFunctionService roleFunctionService;

    @Autowired
    private IFunctionService functionService;

    @Autowired
    private IRoleResourceItemService roleResourceItemService;

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/query")
    public ResponseData queryRoleFunction(HttpServletRequest request, @RequestParam(required = false) Long roleId)
            throws BaseException {
        List<MenuItem> menus = functionService.selectAllMenus();
        if (roleId != null) {
            Long[] ids = roleFunctionService.getRoleFunctionById(roleId);
            roleFunctionService.updateMenuCheck(menus, ids);

        }
        return new ResponseData(menus);
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/submit")
    @CheckToken(check = false)
    public ResponseData submit(HttpServletRequest request, @RequestBody List<RoleFunction> records)
            throws BaseException {
        return new ResponseData(roleFunctionService.batchUpdate(records));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping("/queryResourceItems")
    public ResponseData queryResourceItems(HttpServletRequest request, @RequestParam(required = false) Long roleId,
                                           @RequestParam(required = false) Long functionId) {
        return new ResponseData(roleResourceItemService.queryResourceItems(roleId, functionId));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping("/submitResourceItems")
    public ResponseData submitResourceItems(HttpServletRequest request,
                                            @RequestBody List<ResourceItemAssign> resourceItemAssignList, @RequestParam(required = false) Long roleId,
                                            @RequestParam(required = false) Long functionId) {
        return new ResponseData(roleResourceItemService.updateResourceItemAssign(resourceItemAssignList,
                roleId, functionId));
    }
}
