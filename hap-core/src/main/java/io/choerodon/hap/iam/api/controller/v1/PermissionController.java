package io.choerodon.hap.iam.api.controller.v1;

import io.choerodon.hap.iam.api.dto.CheckPermissionDTO;
import io.choerodon.hap.iam.app.service.PermissionService;
import io.choerodon.hap.iam.infra.dto.PermissionDTO;
import io.choerodon.base.annotation.Permission;
import io.choerodon.web.controller.BaseController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

/**
 * @author wuguokai
 */
@RestController
@RequestMapping("/v1/permissions")
public class PermissionController extends BaseController {

    private PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping(value = "/checkPermission")
    @Permission(permissionLogin = true)
    public ResponseEntity<List<CheckPermissionDTO>> checkPermission(@RequestBody List<CheckPermissionDTO> checkPermissions) {
        return new ResponseEntity<>(permissionService.checkPermission(checkPermissions), HttpStatus.OK);
    }

    @Permission
    @PostMapping
    public ResponseEntity<Set<PermissionDTO>> queryByRoleIds(@RequestBody List<Long> roleIds) {
        return new ResponseEntity<>(permissionService.queryByRoleIds(roleIds), HttpStatus.OK);
    }

}
