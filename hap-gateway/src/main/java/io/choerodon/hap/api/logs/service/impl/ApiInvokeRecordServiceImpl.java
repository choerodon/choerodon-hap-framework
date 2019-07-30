package io.choerodon.hap.api.logs.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import io.choerodon.hap.api.logs.dto.ApiInvokeRecord;
import io.choerodon.hap.api.logs.mapper.ApiInvokeRecordDetailsMapper;
import io.choerodon.hap.api.logs.mapper.ApiInvokeRecordMapper;
import io.choerodon.hap.api.logs.service.IApiInvokeRecordService;
import io.choerodon.hap.message.components.InvokeApiManager;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.mybatis.service.BaseServiceImpl;
import io.choerodon.web.core.IRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 访问记录Service - 实现类.
 *
 * @author peng.jiang@hand-china.com
 * @since 2017/9/25.
 */
@Service
@Dataset("ApiInvokeRecord")
public class ApiInvokeRecordServiceImpl extends BaseServiceImpl<ApiInvokeRecord> implements IApiInvokeRecordService, IDatasetService<ApiInvokeRecord> {
    private final Logger logger = LoggerFactory.getLogger(ApiInvokeRecordServiceImpl.class);
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private InvokeApiManager invokeApiManager;

    @Autowired
    private ApiInvokeRecordMapper apiInvokeRecordMapper;

    @Autowired
    private ApiInvokeRecordDetailsMapper apiInvokeRecordDetailsMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertApiInvokeRecord(ApiInvokeRecord apiInvokeRecord) {
        apiInvokeRecordMapper.insertSelective(apiInvokeRecord);
        apiInvokeRecord.getApiInvokeRecordDetails().setRecordId(apiInvokeRecord.getRecordId());
        apiInvokeRecordDetailsMapper.insertSelective(apiInvokeRecord.getApiInvokeRecordDetails());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public List<ApiInvokeRecord> selectById(Long recordId) {
        return apiInvokeRecordMapper.selectById(recordId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public List<ApiInvokeRecord> selectList(IRequest request, ApiInvokeRecord condition, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return apiInvokeRecordMapper.selectList(condition);
    }

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            List<ApiInvokeRecord> list;
            ApiInvokeRecord invokeRecord = objectMapper.readValue(objectMapper.writeValueAsString(body), ApiInvokeRecord.class);
            if (invokeRecord.getRecordId() != null) {
                list = invokeApiManager.getInvokeApiStrategy().selectById(invokeRecord.getRecordId());
            } else {
                list = invokeApiManager.getInvokeApiStrategy().queryInvokeRecord(null, invokeRecord, page, pageSize);
            }
            return list;
        } catch (Exception e) {
            throw new DatasetException("dataset.error", e);
        }

    }

    @Override
    public List<ApiInvokeRecord> mutations(List<ApiInvokeRecord> objs) {
        return null;
    }
}