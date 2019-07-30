package io.choerodon.hap.iam.app.service;


import io.choerodon.hap.iam.exception.MenuException;
import io.choerodon.hap.iam.infra.dto.MenuDTO;

import java.util.List;

/**
 * @author wuguokai
 * @author superlee
 * @author qiang.zeng
 */
public interface MenuService {
    /**
     * 查询当前登录用户可访问的菜单列表.
     *
     * @param code     顶级菜单编码
     * @param sourceId site和user都为0
     * @return 菜单DTO
     */
    MenuDTO menus(String code, Long sourceId) throws MenuException;

    /**
     * 菜单配置界面根据层级查询树形菜单，菜单下包含权限信息.
     *
     * @param code 顶级菜单编码
     * @return 菜单DTO
     */
    MenuDTO menuConfig(String code) throws MenuException;

    /**
     * 保存对应层级菜单配置(根据前端传入的树形菜单，更新后端的树形结构).
     *
     * @param code  顶级菜单编码
     * @param menus 菜单DTO列表
     */
    void saveMenuConfig(String code, List<MenuDTO> menus) throws MenuException;

    /**
     * 校验菜单code是否重复.
     *
     * @param menu 菜单DTO
     */
    void check(MenuDTO menu) throws MenuException;

    /**
     * 根据id删除菜单，只能删除非默认的菜单.
     *
     * @param id 菜单Id
     */
    void delete(Long id) throws MenuException;


}
