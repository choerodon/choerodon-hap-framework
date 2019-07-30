package io.choerodon.hap.function.mapper;

import io.choerodon.hap.function.dto.ResourceItemElement;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 权限组件元素Mapper.
 *
 * @author qiang.zeng
 */
public interface ResourceItemElementMapper extends Mapper<ResourceItemElement> {
    /**
     * 根据权限组件Id查询权限组件元素.
     *
     * @param element 权限组件元素
     * @return 权限组件元素集合
     */
    List<ResourceItemElement> selectByResourceItemId(ResourceItemElement element);

    /**
     * 根据权限组件Id删除权限组件元素.
     *
     * @param resourceItemId 权限组件Id
     * @return int
     */
    int deleteByResourceItemId(@Param("resourceItemId") Long resourceItemId);
}