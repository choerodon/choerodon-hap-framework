package io.choerodon.hap.function.service;

import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.function.dto.Resource;
import io.choerodon.hap.function.dto.ResourceItem;
import io.choerodon.mybatis.service.IBaseService;
import io.choerodon.web.core.IRequest;

import java.util.List;

/**
 * 权限组件服务接口.
 *
 * @author njq.niu@hand-china.com
 * @since 2016年4月7日
 */
public interface IResourceItemService extends IBaseService<ResourceItem>, ProxySelf<IResourceItemService> {

    /**
     * 查询资源下的权限组件项.
     *
     * @param requestContext IRequest
     * @param resource       资源
     * @return 权限组件集合
     */
    List<ResourceItem> selectResourceItems(IRequest requestContext, Resource resource);

    /**
     * 批量删除权限组件项.
     *
     * @param requestContext IRequest
     * @param resourceItems  权限组件集合
     * @return int
     */
    int batchDelete(IRequest requestContext, List<ResourceItem> resourceItems);

    /**
     * 按照资源ID和权限组件ID查询权限项.
     *
     * @param resourceItem 权限组件
     * @return ResourceItem
     */
    ResourceItem selectResourceItemByResourceIdAndItemId(ResourceItem resourceItem);

    /**
     * 根据资源Id删除权限组件项.
     *
     * @param resource 资源
     * @return int
     */
    int deleteByResourceId(Resource resource);

}
