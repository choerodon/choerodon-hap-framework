package io.choerodon.hap.function.mapper;

import io.choerodon.hap.function.dto.Function;
import io.choerodon.hap.function.dto.Resource;
import io.choerodon.hap.function.dto.ResourceItem;
import io.choerodon.mybatis.common.Mapper;

import java.util.List;

/**
 * 权限组件Mapper.
 *
 * @author qiang.zeng
 */
public interface ResourceItemMapper extends Mapper<ResourceItem> {
    /**
     * 根据资源Id查询权限组件.
     *
     * @param resource 资源
     * @return 权限组件集合
     */
    List<ResourceItem> selectResourceItemsByResourceId(Resource resource);

    /**
     * 根据功能Id查询权限组件.
     *
     * @param function 功能
     * @return 权限组件集合
     */
    List<ResourceItem> selectResourceItemsByFunctionId(Function function);

    /**
     * 根据资源Id和权限组件Id查询权限组件.
     *
     * @param resourceItem 权限组件
     * @return 权限组件
     */
    ResourceItem selectResourceItemByResourceIdAndItemId(ResourceItem resourceItem);

    /**
     * 缓存查询全部权限组件.
     *
     * @return 权限组件集合
     */
    List<ResourceItem> selectForCache();
}