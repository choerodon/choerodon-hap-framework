package io.choerodon.hap.function.service;

import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.core.annotation.StdWho;
import io.choerodon.hap.function.dto.Function;
import io.choerodon.hap.function.dto.MenuItem;
import io.choerodon.hap.function.dto.Resource;
import io.choerodon.hap.function.dto.ResourceItemAssign;
import io.choerodon.hap.function.dto.RoleResourceItem;
import io.choerodon.mybatis.service.IBaseService;

import java.util.List;

/**
 * 角色权限组件服务接口.
 *
 * @author njq.niu@hand-china.com
 * @author qiang.zeng@hand-china.com
 * @since 2016年4月7日
 */
public interface IRoleResourceItemService extends IBaseService<RoleResourceItem>, ProxySelf<IRoleResourceItemService> {
    /**
     * 查询角色拥有的权限组件项.
     *
     * @param roleId     角色Id
     * @param functionId 功能Id
     * @return 权限组件项集合
     */
    List<MenuItem> queryResourceItems(Long roleId, Long functionId);

    /**
     * 查询功能下的HTML资源.
     *
     * @param function 功能
     * @return HTML资源集合
     */
    List<Resource> queryHtmlResources(Function function);

    /**
     * 构建HTML资源菜单.
     *
     * @param resourceList HTML资源集合
     * @return HTML资源菜单集合
     */
    List<MenuItem> createResources(List<Resource> resourceList);

    /**
     * 保存角色拥有的权限组件分配.
     *
     * @param resourceItemAssignList 分配的权限组件集合
     * @param roleId                 角色Id
     * @param functionId             功能Id
     * @return 分配的权限组件集合
     */
    List<ResourceItemAssign> updateResourceItemAssign(@StdWho List<ResourceItemAssign> resourceItemAssignList,
                                                      Long roleId, Long functionId);

    /**
     * 判断角色是否拥有权限项.
     *
     * @param roleId         角色Id
     * @param resourceItemId 权限组件Id
     * @return 返回true:有  返回false:没有
     */
    boolean hasResourceItem(Long roleId, Long resourceItemId);
}
