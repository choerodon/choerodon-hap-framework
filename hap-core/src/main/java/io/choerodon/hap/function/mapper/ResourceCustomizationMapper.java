package io.choerodon.hap.function.mapper;

import io.choerodon.hap.function.dto.ResourceCustomization;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 资源合并配置项Mapper.
 *
 * @author zhizheng.yang@hand-china.com
 */
public interface ResourceCustomizationMapper extends Mapper<ResourceCustomization> {
    /**
     * 根据资源Id查询资源合并配置项.
     *
     * @param resourceId 资源Id
     * @return 资源合并配置项集合
     */
    List<ResourceCustomization> selectResourceCustomizationsByResourceId(@Param("resourceId") Long resourceId);

    /**
     * 根据资源Id查询启用状态的资源合并配置项.
     *
     * @param resourceId 资源Id
     * @return 资源合并配置项集合
     */
    List<ResourceCustomization> loadResourceCustomizationsByResourceId(@Param("resourceId") Long resourceId);

}
