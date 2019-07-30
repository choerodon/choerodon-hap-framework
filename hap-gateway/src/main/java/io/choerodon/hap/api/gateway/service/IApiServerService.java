package io.choerodon.hap.api.gateway.service;

import io.choerodon.hap.api.gateway.dto.ApiServer;
import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.core.annotation.StdWho;
import io.choerodon.mybatis.service.IBaseService;

import java.util.List;
import java.util.Map;

/**
 * 透传服务接口.
 *
 * @author peng.jiang@hand-china.com
 * @since 2017/9/15.
 */

public interface IApiServerService extends IBaseService<ApiServer>, ProxySelf<IApiServerService> {

    /**
     * 通过代码查询服务.
     *
     * @param codeList 服务编码列表
     * @return 服务列表
     */
    List<ApiServer> selectByCodes(List<String> codeList);

    /**
     * 导入并解析服务.
     *
     * @param dto 服务
     * @return 服务
     */
    ApiServer importServer(ApiServer dto);

    /**
     * 添加服务.
     *
     * @param server 服务
     * @return 服务
     */
    ApiServer insertServer(ApiServer server);

    /**
     * 修改服务.
     *
     * @param server 服务
     * @return 服务
     */
    ApiServer updateServer(@StdWho ApiServer server);

    /**
     * 根据服务路径、接口路径获取服务(缓存).
     *
     * @param serverUrl    服务路径
     * @param interfaceUrl 接口路径
     * @return 服务
     */
    ApiServer getByMappingUrl(String serverUrl, String interfaceUrl);


    /**
     * 查询应用没关联的服务.
     *
     * @param params Map<String, Object>
     * @return 服务列表
     */
    List<ApiServer> selectNotExistsServerByApp(Map<String, Object> params);

}