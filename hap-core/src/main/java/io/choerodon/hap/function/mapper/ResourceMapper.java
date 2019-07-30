package io.choerodon.hap.function.mapper;

import io.choerodon.hap.function.dto.Function;
import io.choerodon.hap.function.dto.Resource;
import io.choerodon.mybatis.common.Mapper;

import java.util.List;

/**
 * 资源mapper.
 *
 * @author wuyichu
 */
public interface ResourceMapper extends Mapper<Resource> {
    /**
     * 根据资源的url查询资源数据.
     *
     * @param url 资源的路径
     * @return 资源
     */
    Resource selectResourceByUrl(String url);

    /**
     * 查询功能下的资源.
     *
     * @param function 功能
     * @return 资源集合
     */
    List<Resource> selectResourcesByFunction(Function function);

    /**
     * 条件查询资源(排除建模页面资源).
     *
     * @param resource 资源
     * @return 资源列表
     */
    List<Resource> selectExcludePageByOptions(Resource resource);

    /**
     * 条件查询建模页面资源.
     *
     * @param resource 资源
     * @return 资源列表
     */
    List<Resource> selectPageByOptions(Resource resource);
}