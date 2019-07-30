package io.choerodon.hap.api.gateway.service;

import io.choerodon.hap.api.gateway.dto.ApiInterface;
import io.choerodon.hap.core.ProxySelf;
import io.choerodon.mybatis.service.IBaseService;
import io.choerodon.web.core.IRequest;

import java.util.List;

/**
 * server-interface 接口.
 *
 * @author xiangyu.qi@hand-china.com
 * @since 2017/9/20.
 */

public interface IApiInterfaceService extends IBaseService<ApiInterface>, ProxySelf<IApiInterfaceService> {

    /**
     * 接口查询.
     *
     * @param request     IRequest
     * @param srInterface 接口
     * @return 接口列表
     */
    List<ApiInterface> selectByServerId(IRequest request, ApiInterface srInterface);

    /**
     * 查询服务接口（包括接口限制信息）.
     *
     * @param request  IRequest
     * @param clientId 客户端Id
     * @param serverId 服务Id
     * @return 接口列表
     */
    List<ApiInterface> selectByServerIdWithLimit(IRequest request, String clientId, Long serverId);

    /**
     * 根据服务代码获取接口.
     *
     * @param requestContext IRequest
     * @param clientId       客户端Id
     * @param serverCode     服务代码
     * @return 接口列表
     */
    List<ApiInterface> selectInterfacesByServerCode(IRequest requestContext, String clientId, String serverCode);
}