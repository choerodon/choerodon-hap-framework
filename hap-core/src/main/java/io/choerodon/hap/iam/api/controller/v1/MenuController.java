package io.choerodon.hap.iam.api.controller.v1;

import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.hap.iam.app.service.MenuService;
import io.choerodon.hap.iam.exception.MenuException;
import io.choerodon.hap.iam.infra.dto.MenuDTO;
import io.choerodon.web.controller.BaseController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author wuguokai
 * @author superlee
 * @author qiang.zeng
 */
@RestController
@RequestMapping("/v1/menus")
public class MenuController extends BaseController {

    private MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    /**
     * 获取当前登录用户可访问的菜单.
     *
     * @param code     顶级菜单编码
     * @param sourceId 层级Id
     * @return 菜单DTO
     */
    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @GetMapping
    public ResponseEntity<MenuDTO> menus(@RequestParam String code,
                                         @RequestParam(name = "source_id", defaultValue = "0") Long sourceId) throws MenuException {
        return new ResponseEntity<>(menuService.menus(code, sourceId), HttpStatus.OK);
    }

    /**
     * 菜单配置界面根据层级查菜单(全局 or 个人中心).
     *
     * @param code 顶级菜单编码
     * @return 菜单DTO
     */
    @Permission
    @GetMapping("/menu_config")
    public ResponseEntity<MenuDTO> menuConfig(@RequestParam String code) throws MenuException {
        return new ResponseEntity<>(menuService.menuConfig(code), HttpStatus.OK);
    }

    /**
     * 菜单配置保存.
     *
     * @param code  顶级菜单编码
     * @param menus 菜单DTO列表
     * @return 是否保存成功
     */
    @Permission
    @PostMapping("/menu_config")
    public ResponseEntity saveMenuConfig(@RequestParam String code, @RequestBody List<MenuDTO> menus) throws MenuException {
        menuService.saveMenuConfig(code, menus);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * "根据id删除菜单，只能删非默认菜单.
     *
     * @param id 菜单Id
     * @return 是否删除成功
     */
    @Permission
    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable("id") Long id) throws MenuException {
        menuService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 菜单code是否重复.
     *
     * @param menu 菜单DTO
     * @return 是否校验成功
     */
    @Permission
    @PostMapping(value = "/check")
    public ResponseEntity check(@RequestBody MenuDTO menu) throws MenuException {
        menuService.check(menu);
        return new ResponseEntity(HttpStatus.OK);
    }
}