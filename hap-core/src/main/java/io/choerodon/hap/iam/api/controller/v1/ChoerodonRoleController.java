package io.choerodon.hap.iam.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.hap.iam.api.query.RoleQuery;
import io.choerodon.hap.iam.app.service.ChoerodonRoleService;
import io.choerodon.hap.iam.exception.ChoerodonRoleException;
import io.choerodon.hap.iam.infra.dto.RoleDTO;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.constant.PageConstant;
import io.choerodon.web.controller.BaseController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author superlee
 * @author wuguokai
 */
@RestController
@RequestMapping(value = "/v1/roles")
public class ChoerodonRoleController extends BaseController {

    private ChoerodonRoleService roleService;

    public ChoerodonRoleController(ChoerodonRoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * 分页查询角色.
     *
     * @return 查询结果
     */
    @Permission
    @PostMapping(value = "/search")
    public ResponseEntity<PageInfo<RoleDTO>> pagedSearch(@RequestParam(defaultValue = PageConstant.PAGE, required = false) final int page,
                                                         @RequestParam(defaultValue = PageConstant.SIZE, required = false) final int size,
                                                         @RequestBody RoleQuery roleQuery) {
        return new ResponseEntity<>(roleService.pagingSearch(page, size, roleQuery), HttpStatus.OK);
    }

    /**
     * 根据角色id查询角色.
     *
     * @return 查询结果
     */
    @Permission
    @GetMapping(value = "/{id}")
    public ResponseEntity<RoleDTO> queryWithPermissions(@PathVariable Long id) {
        return new ResponseEntity<>(roleService.queryWithPermissions(id), HttpStatus.OK);
    }

    @Permission
    @PostMapping
    public ResponseEntity<RoleDTO> create(@RequestBody @Validated RoleDTO roleDTO) throws ChoerodonRoleException {
        return new ResponseEntity<>(roleService.create(roleDTO), HttpStatus.OK);
    }

    @Permission
    @PutMapping(value = "/{id}")
    public ResponseEntity<RoleDTO> update(@PathVariable Long id,
                                          @RequestBody RoleDTO roleDTO) throws ChoerodonRoleException {
        roleDTO.setId(id);
        return new ResponseEntity<>(roleService.update(roleDTO), HttpStatus.OK);
    }

    @Permission
    @PutMapping(value = "/{id}/enable")
    public ResponseEntity<RoleDTO> enableRole(@PathVariable Long id) throws ChoerodonRoleException {
        return new ResponseEntity<>(roleService.enableRole(id), HttpStatus.OK);
    }

    @Permission
    @PutMapping(value = "/{id}/disable")
    public ResponseEntity<RoleDTO> disableRole(@PathVariable Long id) throws ChoerodonRoleException {
        return new ResponseEntity<>(roleService.disableRole(id), HttpStatus.OK);
    }

    @Permission
    @PostMapping(value = "/check")
    public ResponseEntity check(@RequestBody RoleDTO role) throws ChoerodonRoleException {
        roleService.check(role);
        return new ResponseEntity(HttpStatus.OK);
    }
}
