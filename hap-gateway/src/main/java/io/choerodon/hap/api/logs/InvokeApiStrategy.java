package io.choerodon.hap.api.logs;

import io.choerodon.hap.api.logs.dto.ApiInvokeRecord;
import io.choerodon.web.core.IRequest;

import java.util.List;

/**
 * api映射策略.
 *
 * @author peng.jiang@hand-china.com
 * @since 2017/10/14.
 */
public interface InvokeApiStrategy {

    /**
     * 记录api调用记录.
     *
     * @param invokeRecord 入站请求相关信息
     */
    void saveApiInvokeRecord(ApiInvokeRecord invokeRecord);


    /**
     * 查询出站请求记录列表.
     *
     * @param request   IRequest
     * @param condition 出站请求记录
     * @param pageNum   页码
     * @param pageSize  每页显示数量
     * @return 出站请求记录列表
     */
    List<ApiInvokeRecord> queryInvokeRecord(IRequest request, ApiInvokeRecord condition, int pageNum, int pageSize);


    /**
     * 查询调用记录列表.
     *
     * @param recordId 调用记录Id
     * @return 调用记录列表
     */
    List<ApiInvokeRecord> selectById(Long recordId);


}
