package io.choerodon.hap.api.gateway.service;

import io.choerodon.hap.api.gateway.dto.ApiServer;

/**
 * 导入服务.
 *
 * @author xiangyu.qi@hand-china.com
 * @since 2017/9/20.
 */

public interface IApiImportService {

    /**
     * 导入url，解析.
     *
     * @param srServer 服务
     * @return 服务
     */
    ApiServer importServer(ApiServer srServer);

}
