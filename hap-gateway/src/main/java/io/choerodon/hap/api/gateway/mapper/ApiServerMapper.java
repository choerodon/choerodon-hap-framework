package io.choerodon.hap.api.gateway.mapper;

import io.choerodon.hap.api.gateway.dto.ApiServer;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 服务 mapper.
 *
 * @author xiangyu.qi@hand-china.com
 * @since 2017/9/21.
 **/

public interface ApiServerMapper extends Mapper<ApiServer> {

    /**
     * 根据服务代码获取服务列表.
     *
     * @param codeList 服务代码列表
     * @return 服务列表
     */
    List<ApiServer> selectByCodes(@Param("codeList") List<String> codeList);

    /**
     * 根据服务Id获取服务（包括接口）.
     *
     * @param serverId 服务Id
     * @return 服务（包括接口）
     */
    ApiServer getServerAndInterfaceByServerId(@Param(value = "serverId") Long serverId);

    /**
     * 查询应用未关联的服务.
     *
     * @param params Map<String, Object>
     * @return 服务列表
     */
    List<ApiServer> selectNotExistsServerByApp(Map<String, Object> params);
}