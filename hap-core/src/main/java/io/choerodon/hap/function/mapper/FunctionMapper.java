package io.choerodon.hap.function.mapper;

import io.choerodon.hap.function.dto.Function;
import io.choerodon.hap.function.dto.Resource;
import io.choerodon.mybatis.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 功能Mapper.
 *
 * @author wuyichu
 */
public interface FunctionMapper extends Mapper<Function> {
    /**
     * 查询功能挂靠的资源.
     *
     * @param params Map<String, Object>
     * @return 资源集合
     */
    List<Resource> selectExistsResourcesByFunction(Map<String, Object> params);

    /**
     * 查询功能没有挂靠的资源.
     *
     * @param params Map<String, Object>
     * @return 资源集合
     */
    List<Resource> selectNotExistsResourcesByFunction(Map<String, Object> params);

    /**
     * 根据父功能Id查询子功能Id.
     *
     * @param parentId 父功能Id
     * @return 子功能集合
     */
    List<Function> selectFunctionIdByParentId(Long parentId);

    /**
     * 根据功能Id查询功能数据(更新功能的缓存使用).
     *
     * @return 功能信息
     */
    Function selectForReloadCache(Function function);
}