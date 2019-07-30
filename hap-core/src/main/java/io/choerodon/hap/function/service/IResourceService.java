package io.choerodon.hap.function.service;

import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.function.dto.Resource;
import io.choerodon.mybatis.service.IBaseService;
import io.choerodon.web.core.IRequest;

import java.util.List;

/**
 * 资源服务接口.
 *
 * @author wuyichu
 */
public interface IResourceService extends IBaseService<Resource>, ProxySelf<IResourceService> {

    /**
     * 根据资源的url查询资源数据.
     *
     * @param url 资源的路径
     * @return 资源
     */
    Resource selectResourceByUrl(String url);

    /**
     * 根据资源的Id查询资源数据.
     *
     * @param id 资源ID
     * @return 资源
     */
    Resource selectResourceById(Long id);

    /**
     * 批量删除资源记录.
     *
     * @param requestContext IRequest
     * @param resources      资源集合
     * @return int
     */
    int batchDelete(IRequest requestContext, List<Resource> resources);

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
