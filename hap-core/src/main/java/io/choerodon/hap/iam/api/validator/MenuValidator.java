package io.choerodon.hap.iam.api.validator;

import io.choerodon.base.enums.MenuType;
import io.choerodon.hap.iam.exception.MenuException;
import io.choerodon.hap.iam.infra.dto.MenuDTO;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import static io.choerodon.hap.iam.exception.MenuException.ERROR_MENU_CODE_EMPTY;
import static io.choerodon.hap.iam.exception.MenuException.ERROR_MENU_DEFAULT;
import static io.choerodon.hap.iam.exception.MenuException.ERROR_MENU_HAVE_CHILDREN;
import static io.choerodon.hap.iam.exception.MenuException.ERROR_MENU_ICON_EMPTY;
import static io.choerodon.hap.iam.exception.MenuException.ERROR_MENU_NAME_EMPTY;
import static io.choerodon.hap.iam.exception.MenuException.ERROR_MENU_NOT_SELF;
import static io.choerodon.hap.iam.exception.MenuException.ERROR_MENU_PARENT_CODE_EMPTY;

/**
 * @author azengqiang
 */
public class MenuValidator {
    /**
     * 插入菜单校验.
     * 编码不能为空
     * 名称不能为空
     * 图标不能为空
     * 父级编码不能为空
     *
     * @param menu  菜单DTO
     * @param level 资源层级
     */
    public static void insertValidate(MenuDTO menu, String level) throws MenuException {
        menu.setResourceLevel(level);
        menu.setType(MenuType.MENU.value());
        String code = menu.getCode();
        if (StringUtils.isEmpty(code)) {
            throw new MenuException(ERROR_MENU_CODE_EMPTY);
        }
        if (StringUtils.isEmpty(menu.getName())) {
            throw new MenuException(ERROR_MENU_NAME_EMPTY, code);
        }
        if (StringUtils.isEmpty(menu.getIcon())) {
            throw new MenuException(ERROR_MENU_ICON_EMPTY, code);
        }
        if (StringUtils.isEmpty(menu.getParentCode())) {
            throw new MenuException(ERROR_MENU_PARENT_CODE_EMPTY, code);
        }
        if (menu.getSort() == null) {
            menu.setSort(0);
        }
        menu.setDefault(false);
    }

    /**
     * 删除菜单校验.
     * 预设目录不能删除
     * 只有自设目录可以删除
     * 目录下有叶子节点，不能删除
     *
     * @param menu 菜单DTO
     */
    public static void deleteValidate(MenuDTO menu) throws MenuException {
        if (menu.getDefault()) {
            throw new MenuException(ERROR_MENU_DEFAULT);
        }
        if (!MenuType.isMenu(menu.getType())) {
            throw new MenuException(ERROR_MENU_NOT_SELF, menu.getName());
        }
        if (!CollectionUtils.isEmpty(menu.getSubMenus())) {
            throw new MenuException(ERROR_MENU_HAVE_CHILDREN, menu.getName());
        }
    }
}
