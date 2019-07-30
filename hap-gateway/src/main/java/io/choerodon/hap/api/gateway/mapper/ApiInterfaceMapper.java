package io.choerodon.hap.api.gateway.mapper;

import io.choerodon.hap.api.gateway.dto.ApiInterface;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 接口 mapper.
 *
 * @author peng.jiang@hand-china.com
 * @since 2017/9/21.
 **/

public interface ApiInterfaceMapper extends Mapper<ApiInterface> {

    /**
     * 根据服务Id删除服务.
     *
     * @param serverId 服务Id
     * @return int
     */
    int removeByServerId(@Param("serverId") Long serverId);

    /**
     * 根据服务Id获取接口列表.
     *
     * @param srInterface 接口对象
     * @return 接口列表
     */
    List<ApiInterface> selectByServerId(ApiInterface srInterface);

    /**
     * 根据客户端Id和服务Id查询接口列表.
     *
     * @param clientId 客户端Id
     * @param serverId 服务Id
     * @return 接口列表
     */
    List<ApiInterface> selectByServerIdWithLimit(@Param("clientId") String clientId,
                                                 @Param("serverId") Long serverId);

    /**
     * 根据服务代码查询接口列表.
     *
     * @param serverCode 服务代码
     * @return 接口列表
     */
    List<ApiInterface> selectInterfacesByServerCode(String serverCode);
}