package io.choerodon.hap.function.mapper;

import io.choerodon.hap.function.dto.FunctionResource;
import io.choerodon.hap.function.dto.Resource;
import org.apache.ibatis.annotations.Param;
import io.choerodon.mybatis.common.Mapper;

/**
 * 功能资源mapper.
 *
 * @author wuyichu
 */
public interface FunctionResourceMapper extends Mapper<FunctionResource> {
    /**
     * 根据资源删除功能资源.
     *
     * @param resource 资源
     * @return int
     */
    int deleteByResource(Resource resource);

    /**
     * 根据功能Id删除功能资源.
     *
     * @param functionId 功能Id
     * @return int
     */
    int deleteByFunctionId(Long functionId);

    /**
     * 根据功能Id和资源Id删除功能资源.
     *
     * @param functionId 功能Id
     * @param resourceId 资源Id
     * @return int
     */
    int deleteFunctionResource(@Param(value = "functionId") Long functionId,
                               @Param(value = "resourceId") Long resourceId);
}