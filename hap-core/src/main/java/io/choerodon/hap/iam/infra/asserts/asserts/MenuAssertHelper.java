package io.choerodon.hap.iam.infra.asserts.asserts;

import io.choerodon.hap.iam.exception.MenuException;
import io.choerodon.hap.iam.infra.dto.MenuDTO;
import io.choerodon.hap.iam.infra.mapper.MenuMapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import static io.choerodon.hap.iam.exception.MenuException.ERROR_MENU_CODE_EXISTED;
import static io.choerodon.hap.iam.exception.MenuException.ERROR_MENU_NOT_EXISTED;

/**
 * 菜单断言帮助类.
 *
 * @author superlee
 */
@Component
public class MenuAssertHelper {
    private MenuMapper menuMapper;

    public MenuAssertHelper(MenuMapper menuMapper) {
        this.menuMapper = menuMapper;
    }

    public void codeExisted(String code) throws MenuException {
        MenuDTO dto = new MenuDTO();
        dto.setCode(code);
        if (CollectionUtils.isNotEmpty(menuMapper.select(dto))) {
            throw new MenuException(ERROR_MENU_CODE_EXISTED);
        }
    }

    public MenuDTO menuNotExisted(Long id) throws MenuException {
        MenuDTO dto = menuMapper.selectByPrimaryKey(id);
        if (dto == null) {
            throw new MenuException(ERROR_MENU_NOT_EXISTED, id);
        }
        return dto;
    }
}
