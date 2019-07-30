package io.choerodon.hap.api.gateway.service;

import io.choerodon.hap.api.gateway.dto.ApiServer;
import net.sf.json.JSONObject;

/**
 * 映射接口.
 *
 * @author xiangyu.qi@hand-china.com
 * @since 2017/9/25.
 */

public interface IApiInvokeService {

    /**
     * 当前实现类处理的服务类型.
     *
     * @return 服务类型
     */
    String serverType();

    /**
     * 判断服务类型（REST，SOAP...）是否匹配当前服务.
     *
     * @param serverType 服务类型
     * @return 是否匹配当前服务
     */
    default boolean matchServerType(String serverType) {
        return serverType().equalsIgnoreCase(serverType);
    }

    /**
     * 透传调用具体第三方服务.
     *
     * @param server  服务配置信息
     * @param inbound 传入参数
     * @return 结果对象
     * @throws Exception 返回结果异常
     */
    Object invoke(ApiServer server, JSONObject inbound) throws Exception;

    /**
     * 请求第三方服务.
     *
     * @param server    服务配置信息
     * @param parameter 传入参数
     * @return 结果字符串
     * @throws Exception 返回结果异常
     */
    String apiInvoke(ApiServer server, String parameter) throws Exception;

}
