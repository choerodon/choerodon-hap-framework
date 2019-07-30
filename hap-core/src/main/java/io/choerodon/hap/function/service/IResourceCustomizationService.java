package io.choerodon.hap.function.service;

import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.function.dto.ResourceCustomization;
import io.choerodon.mybatis.service.IBaseService;

import java.util.List;

/**
 * 资源合并配置服务接口.
 *
 * @author zhizheng.yang@hand-china.com
 */
public interface IResourceCustomizationService extends IBaseService<ResourceCustomization>, ProxySelf<IResourceCustomizationService> {

    /**
     * 根据资源Id查询资源合并配置项.
     *
     * @param resourceId 资源Id
     * @return 资源合并配置项集合
     */
    List<ResourceCustomization> selectResourceCustomizationsByResourceId(Long resourceId);

    /**
     * 根据资源Id删除资源合并配置项.
     *
     * @param resourceId 资源Id
     * @return int
     */
    int deleteByResourceId(Long resourceId);
}
