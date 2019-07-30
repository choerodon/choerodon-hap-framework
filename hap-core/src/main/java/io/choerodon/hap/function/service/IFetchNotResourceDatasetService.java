package io.choerodon.hap.function.service;

import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.function.dto.Resource;
import io.choerodon.mybatis.service.IBaseService;

/**
 * 功能资源 未挂靠资源服务接口.
 *
 * @author qiang.zeng
 * @since 2018/11/22.
 */
public interface IFetchNotResourceDatasetService extends IBaseService<Resource>, ProxySelf<IFetchNotResourceDatasetService> {
}
