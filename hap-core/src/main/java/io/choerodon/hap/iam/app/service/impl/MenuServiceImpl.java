package io.choerodon.hap.iam.app.service.impl;

import io.choerodon.base.enums.MenuType;
import io.choerodon.hap.iam.api.validator.MenuValidator;
import io.choerodon.hap.iam.app.service.MenuService;
import io.choerodon.hap.iam.exception.MenuException;
import io.choerodon.hap.iam.infra.asserts.asserts.MenuAssertHelper;
import io.choerodon.hap.iam.infra.dto.MenuDTO;
import io.choerodon.hap.iam.infra.mapper.MenuMapper;
import io.choerodon.hap.security.PermissionVoter;
import io.choerodon.web.core.IRequest;
import io.choerodon.web.core.impl.RequestHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.choerodon.hap.iam.exception.MenuException.ERROR_MENU_CODE_EMPTY;
import static io.choerodon.hap.iam.exception.MenuException.ERROR_MENU_CODE_EXISTED;
import static io.choerodon.hap.iam.exception.MenuException.ERROR_MENU_DEFAULT;
import static io.choerodon.hap.iam.exception.MenuException.ERROR_MENU_TOP_NOT_EXISTED;
import static io.choerodon.mybatis.entity.BaseDTO.STATUS_ADD;
import static io.choerodon.mybatis.entity.BaseDTO.STATUS_DELETE;
import static io.choerodon.mybatis.entity.BaseDTO.STATUS_UPDATE;

/**
 * @author wuguokai
 * @author superlee
 * @author qiang.zeng
 */
@Component
public class MenuServiceImpl implements MenuService {
    private static final Logger logger = LoggerFactory.getLogger(PermissionVoter.class);
    private static final String MEMBER_TYPE_USER = "user";
    private MenuMapper menuMapper;
    private MenuAssertHelper menuAssertHelper;

    public MenuServiceImpl(MenuMapper menuMapper, MenuAssertHelper menuAssertHelper) {
        this.menuMapper = menuMapper;
        this.menuAssertHelper = menuAssertHelper;
    }

    @Override
    public MenuDTO menus(String code, Long sourceId) throws MenuException {
        MenuDTO topMenu = getTopMenuByCode(code);
        String level = topMenu.getResourceLevel();
        IRequest request = RequestHelper.getCurrentRequest();
        Long[] memberRoleIds = request.getAllRoleId();
        //用户角色为空 直接返回顶级菜单
        if (ArrayUtils.isEmpty(memberRoleIds)) {
            logger.warn("The role owned by the user is empty");
            return topMenu;
        }
        Set<MenuDTO> menus = new HashSet<>(menuMapper.selectMenusAfterCheckPermission(MEMBER_TYPE_USER, request.getUserId(), memberRoleIds));
        MenuDTO dto = new MenuDTO();
        dto.setType(MenuType.MENU.value());
        dto.setResourceLevel(level);
        menus.addAll(menuMapper.select(dto));
        toTreeMenu(topMenu, menus, false);
        return topMenu;
    }

    @Override
    public MenuDTO menuConfig(String code) throws MenuException {
        MenuDTO menu = getTopMenuByCode(code);
        String level = menu.getResourceLevel();
        Set<MenuDTO> menus = new HashSet<>(menuMapper.selectMenusWithPermission(level));
        toTreeMenu(menu, menus, true);
        return menu;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveMenuConfig(String code, List<MenuDTO> menus) throws MenuException {
        MenuDTO topMenu = getTopMenuByCode(code);
        String level = topMenu.getResourceLevel();
        // 传入的菜单列表
        List<MenuDTO> submitMenuList = menuTreeToList(menus);
        for (MenuDTO menuDTO : submitMenuList) {
            String status = menuDTO.get__status();
            if (!StringUtils.isEmpty(status)) {
                switch (status) {
                    case STATUS_ADD:
                        insertMenu(menuDTO, level);
                        break;
                    case STATUS_UPDATE:
                        updateMenu(menuDTO);
                        break;
                    case STATUS_DELETE:
                        deleteMenu(menuDTO);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void check(MenuDTO menu) throws MenuException {
        if (StringUtils.isEmpty(menu.getCode())) {
            throw new MenuException(ERROR_MENU_CODE_EMPTY);
        }
        MenuDTO dto = new MenuDTO();
        dto.setCode(menu.getCode());
        Boolean existed = menuMapper.selectOne(dto) != null;
        if (existed) {
            throw new MenuException(ERROR_MENU_CODE_EXISTED);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) throws MenuException {
        MenuDTO dto = menuAssertHelper.menuNotExisted(id);
        if (dto.getDefault()) {
            throw new MenuException(ERROR_MENU_DEFAULT);
        }
        menuMapper.deleteByPrimaryKey(id);
    }

    /**
     * 根据顶级菜单编码获取顶级菜单.
     *
     * @param code 顶级菜单编码
     * @return 顶级菜单
     */
    private MenuDTO getTopMenuByCode(String code) throws MenuException {
        MenuDTO dto = new MenuDTO();
        dto.setCode(code);
        MenuDTO menu = menuMapper.selectOne(dto);
        if (menu == null) {
            throw new MenuException(ERROR_MENU_TOP_NOT_EXISTED);
        }
        return menu;
    }

    /**
     * 转换树形菜单.
     * 情况1：用户菜单不显示空目录
     * 情况2：菜单配置显示空目录
     *
     * @param parentMenu      父级菜单
     * @param menus           所有菜单列表
     * @param isShowEmptyMenu 是否显示空目录
     */
    private void toTreeMenu(MenuDTO parentMenu, Set<MenuDTO> menus, Boolean isShowEmptyMenu) {
        String code = parentMenu.getCode();
        List<MenuDTO> subMenus = new ArrayList<>();
        for (MenuDTO menu : menus) {
            if (code.equalsIgnoreCase(menu.getParentCode())) {
                // 如果是叶子菜单 直接放到父级目录的子菜单列表里面
                if (MenuType.isMenuItem(menu.getType())) {
                    subMenus.add(menu);
                }
                if (MenuType.isMenu(menu.getType())) {
                    toTreeMenu(menu, menus, isShowEmptyMenu);
                    if (isShowEmptyMenu) {
                        subMenus.add(menu);
                    } else {
                        // 目录有叶子菜单 放到父级目录的子目录里面
                        if (!menu.getSubMenus().isEmpty()) {
                            subMenus.add(menu);
                        }
                    }
                }
            }
        }
        subMenus.sort(Comparator.comparing(MenuDTO::getSort));
        parentMenu.setSubMenus(subMenus);
    }

    /**
     * 树形菜单转换为List菜单.
     *
     * @param menus 树形菜单
     * @return List菜单
     */
    private List<MenuDTO> menuTreeToList(List<MenuDTO> menus) {
        List<MenuDTO> menuList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(menus)) {
            doProcessMenu(menus, menuList);
        }
        return menuList;
    }

    /**
     * 递归解析树形菜单为List菜单.
     *
     * @param menus    树形菜单
     * @param menuList List菜单
     */
    private void doProcessMenu(List<MenuDTO> menus, List<MenuDTO> menuList) {
        for (MenuDTO menuDTO : menus) {
            menuList.add(menuDTO);
            if (menuDTO.getSubMenus() != null) {
                doProcessMenu(menuDTO.getSubMenus(), menuList);
            }
        }
    }

    /**
     * 插入菜单.
     *
     * @param insertMenu 菜单DTO
     * @param level      菜单层级
     * @throws MenuException 菜单插入异常
     */
    private void insertMenu(MenuDTO insertMenu, String level) throws MenuException {
        MenuValidator.insertValidate(insertMenu, level);
        menuAssertHelper.codeExisted(insertMenu.getCode());
        menuMapper.insertSelective(insertMenu);
    }

    /**
     * 更新菜单.
     * 预设目录和叶子菜单只能更新排序以及父级编码
     * 自设目录可以更新名称、图标、排序以及父级编码
     *
     * @param updateMenu 菜单DTO
     */
    private void updateMenu(MenuDTO updateMenu) {
        boolean isNotDefault = MenuType.isMenu(updateMenu.getType()) && updateMenu.getDefault() != null && !updateMenu.getDefault();
        // only self menu can update name and icon
        MenuDTO menuDTO = new MenuDTO();
        if (isNotDefault) {
            menuDTO.setName(updateMenu.getName());
            menuDTO.setIcon(updateMenu.getIcon());
        }
        menuDTO.setSort(updateMenu.getSort());
        menuDTO.setParentCode(updateMenu.getParentCode());
        menuDTO.setId(updateMenu.getId());
        menuDTO.setObjectVersionNumber(updateMenu.getObjectVersionNumber());
        menuMapper.updateByPrimaryKeySelective(menuDTO);
    }

    /**
     * 删除菜单.
     *
     * @param deleteMenu 菜单DTO
     * @throws MenuException 菜单删除异常
     */
    private void deleteMenu(MenuDTO deleteMenu) throws MenuException {
        MenuValidator.deleteValidate(deleteMenu);
        menuMapper.deleteByPrimaryKey(deleteMenu);
    }

}
