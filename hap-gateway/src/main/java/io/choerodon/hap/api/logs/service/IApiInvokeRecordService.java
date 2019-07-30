package io.choerodon.hap.api.logs.service;

import io.choerodon.hap.api.logs.dto.ApiInvokeRecord;
import io.choerodon.hap.core.ProxySelf;
import io.choerodon.mybatis.service.IBaseService;
import io.choerodon.web.core.IRequest;

import java.util.List;

/**
 * api调用记录service - 接口.
 *
 * @author peng.jiang@hand-china.com
 * @since 2017/10/14.
 */

public interface IApiInvokeRecordService extends IBaseService<ApiInvokeRecord>, ProxySelf<IApiInvokeRecordService> {

    /**
     * 保存调用记录.
     *
     * @param invokeRecord 调用记录
     */
    void insertApiInvokeRecord(ApiInvokeRecord invokeRecord);

    /**
     * 根据Id调用记录列表.
     *
     * @param recordId 调用记录
     * @return 调用记录列表
     */
    List<ApiInvokeRecord> selectById(Long recordId);

    /**
     * 查询调用记录列表.
     *
     * @param request   IRequest
     * @param condition 调用记录
     * @param pageNum   页码
     * @param pageSize  每页显示数量
     * @return 调用记录列表
     */
    List<ApiInvokeRecord> selectList(IRequest request, ApiInvokeRecord condition, int pageNum, int pageSize);

}