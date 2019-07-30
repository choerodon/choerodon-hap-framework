package io.choerodon.hap.api.logs.components;

import io.choerodon.hap.api.logs.InvokeApiStrategy;
import io.choerodon.hap.api.logs.dto.ApiInvokeRecord;
import io.choerodon.hap.api.logs.service.IApiInvokeRecordService;
import io.choerodon.web.core.IRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 默认api调用记录保存策略.
 *
 * @author peng.jiang@hand-china.com
 * @since 2017/9/23.
 */

@Component
public class DefaultInvokeApiStrategy implements InvokeApiStrategy {

    @Autowired
    private IApiInvokeRecordService apiInvokeRecordService;

    @Override
    public void saveApiInvokeRecord(ApiInvokeRecord invokeRecord) {
        apiInvokeRecordService.insertApiInvokeRecord(invokeRecord);
    }

    @Override
    public List<ApiInvokeRecord> queryInvokeRecord(IRequest request, ApiInvokeRecord condition, int pageNum, int pageSize) {
        return apiInvokeRecordService.selectList(request, condition, pageNum, pageSize);
    }

    @Override
    public List<ApiInvokeRecord> selectById(Long recordId) {
        return apiInvokeRecordService.selectById(recordId);
    }
}
