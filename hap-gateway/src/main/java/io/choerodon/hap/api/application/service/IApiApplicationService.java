package io.choerodon.hap.api.application.service;

import io.choerodon.hap.api.application.dto.ApiApplication;
import io.choerodon.hap.api.gateway.dto.ApiServer;
import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.core.annotation.StdWho;
import io.choerodon.mybatis.service.IBaseService;

import java.util.List;

/**
 * 访问限制service - 实现类.
 *
 * @author lijian.yin@hand-china.com
 * @since 2017/11/15.
 **/

public interface IApiApplicationService extends IBaseService<ApiApplication>, ProxySelf<IApiApplicationService> {

    /**
     * 根据应用Id获取应用.
     *
     * @param applicationId 应用Id
     * @return 应用
     */
    ApiApplication getById(Long applicationId);

    /**
     * 添加应用.
     *
     * @param application 应用
     * @return 应用
     */
    ApiApplication insertApplication(ApiApplication application);

    /**
     * 添加应用.hap4.0版本使用
     *
     * @param application 应用
     * @return 应用
     */
    ApiApplication insertApplicationUP(ApiApplication application);

    /**
     * 修改应用.
     *
     * @param application 应用
     * @return 应用
     */
    ApiApplication updateApplication(@StdWho ApiApplication application);

    /**
     * 修改应用.hap4.0
     *
     * @param application 应用
     * @return 应用
     */
    ApiApplication updateApplicationUP(@StdWho ApiApplication application);

    /**
     * 查询应用未关联的服务.
     *
     * @param exitsCodes 应用编码
     * @param srServer   服务
     * @param page       页码
     * @param pageSize   每页显示数量
     * @return 服务列表
     */
    List<ApiServer> selectNotExistsServerByApp(String exitsCodes, ApiServer srServer, int page, int pageSize);

    /**
     * 条件查询应用.
     *
     * @param apiApplication 应用
     * @param page           页码
     * @param pageSize       每页显示数量
     * @return 应用列表
     */
    List<ApiApplication> selectApplications(ApiApplication apiApplication, int page, int pageSize);

    /**
     * 根据应用Id查询服务列表.
     *
     * @param applicationId 应用Id
     * @return 服务列表
     */
    List<ApiServer> getService(Long applicationId, int page, int pageSize);
}