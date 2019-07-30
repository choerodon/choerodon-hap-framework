package io.choerodon.hap.api.application.mapper;

import io.choerodon.hap.api.application.dto.ApiApplication;
import io.choerodon.mybatis.common.Mapper;

import java.util.List;

/**
 * 应用 mapper.
 *
 * @author lijian.yin@hand-china.com
 * @since 2017/11/15.
 **/

public interface ApiApplicationMapper extends Mapper<ApiApplication> {

    /**
     * 获取应用.
     *
     * @param applicationId 应用Id
     * @return 应用
     */
    ApiApplication getById(Long applicationId);

    /**
     * 查询应用列表.
     *
     * @param record 应用
     * @return 应用列表
     */
    List<ApiApplication> selectApplications(ApiApplication record);


}