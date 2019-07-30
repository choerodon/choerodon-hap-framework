package io.choerodon.hap.api.application.mapper;

import io.choerodon.hap.api.application.dto.ApiAccessLimit;
import io.choerodon.mybatis.common.Mapper;

import java.util.List;

/**
 * 访问限制 mapper.
 *
 * @author lijian.yin@hand-china.com
 * @since 2017/11/15.
 **/

public interface ApiAccessLimitMapper extends Mapper<ApiAccessLimit> {

    /**
     * 根据客户端ID和服务代码删除访问限制.
     *
     * @param apiAccessLimit 访问限制
     * @return 影响条数
     */
    int removeByClientIdAndServerCode(ApiAccessLimit apiAccessLimit);

    /**
     * 根据客户端ID和服务代码查询访问限制列表.
     *
     * @param apiAccessLimit 访问限制
     * @return 访问限制列表
     */
    List<ApiAccessLimit> selectByClientIdAndServerCode(ApiAccessLimit apiAccessLimit);

    /**
     * 查询服务接口的访问限制列表.
     *
     * @param apiAccessLimit 访问限制
     * @return 访问限制列表
     */
    List<ApiAccessLimit> selectList(ApiAccessLimit apiAccessLimit);
}