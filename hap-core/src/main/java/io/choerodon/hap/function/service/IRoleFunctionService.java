package io.choerodon.hap.function.service;

import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.function.dto.Function;
import io.choerodon.hap.function.dto.MenuItem;
import io.choerodon.hap.function.dto.RoleFunction;
import io.choerodon.mybatis.service.IBaseService;

import java.util.List;

/**
 * 角色功能服务接口.
 *
 * @author liuxiawang
 * @author njq.niu@hand-china.com
 */
public interface IRoleFunctionService extends IBaseService<RoleFunction>, ProxySelf<IRoleFunctionService> {

    /**
     * 从缓存中查询角色的所有功能ID的集合.
     *
     * @param roleId 角色Id
     * @return 角色功能Id集合
     */
    Long[] getRoleFunctionById(Long roleId);


    /**
     * 清空角色功能.
     *
     * @param roleId 角色id
     */
    void removeRoleFunctionByRoleId(Long roleId);

    /**
     * 清空数据库角色功能.
     *
     * @param roleId 角色ID
     */
    void removeByRoleId(Long roleId);

    /**
     * 重新加载角色资源缓存.
     *
     * @param roleId 角色Id
     */
    void reloadRoleResourceCache(Long roleId);

    /**
     * 将MenuItem转换为FunctionDTO
     *
     * @param menus
     * @param functionS
     * @param parentId
     */
    void getFunction(List<MenuItem> menus, List<Function> functionS, Long parentId);


    /**
     * 处理勾选状态.
     *
     * @param menus 菜单
     * @param ids   功能Id集合
     */
    void updateMenuCheck(final List<MenuItem> menus, final Long[] ids);
}
