package io.choerodon.hap.api.application.service;

import io.choerodon.hap.api.application.dto.ApiAccessLimit;
import io.choerodon.hap.api.application.dto.ApiApplication;
import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.core.annotation.StdWho;
import io.choerodon.mybatis.service.IBaseService;

/**
 * 服务访问限制.
 *
 * @author lijian.yin@hand-china.com
 **/
public interface IApiAccessLimitService extends IBaseService<ApiAccessLimit>, ProxySelf<IApiAccessLimitService> {

    /**
     * 应用绑定、解绑服务时 同步访问限制记录.
     *
     * @param before         范围
     * @param apiApplication 应用
     */
    void updateByApplication(String before, @StdWho ApiApplication apiApplication);

    /**
     * 应用绑定 解绑服务4.0版本使用
     *
     * @param apiApplication 应用
     */
    void updateByApplication(@StdWho ApiApplication apiApplication);

    /**
     * 根据客户端ID删除记录.
     *
     * @param id 客户端ID
     * @return int
     */
    int removeByClientId(Long id);

    /**
     * 修改访问限制.
     *
     * @param apiApplication 应用
     * @return int
     */
    int updateAccessLimit(ApiApplication apiApplication);
}