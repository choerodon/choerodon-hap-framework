package io.choerodon.hap.function.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.function.dto.Function;
import io.choerodon.hap.function.dto.FunctionResource;
import io.choerodon.hap.function.dto.Resource;
import io.choerodon.hap.function.mapper.FunctionMapper;
import io.choerodon.hap.function.mapper.FunctionResourceMapper;
import io.choerodon.hap.function.service.IFetchResourceDatasetService;
import io.choerodon.hap.message.components.DefaultRoleResourceListener;
import io.choerodon.mybatis.service.BaseServiceImpl;
import io.choerodon.message.IMessagePublisher;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能资源 已挂靠资源服务接口实现.
 *
 * @author qiang.zeng
 * @since 2018/11/22.
 */
@Service
@Dataset("FetchResource")
public class FetchResourceServiceImpl extends BaseServiceImpl<Resource> implements IFetchResourceDatasetService, IDatasetService<FunctionResource> {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FunctionMapper functionMapper;

    @Autowired
    private FunctionResourceMapper functionResourceMapper;

    @Autowired
    private IMessagePublisher messagePublisher;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Resource> queries(Map body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            Function function = objectMapper.readValue(objectMapper.writeValueAsString(body), Function.class);
            Map<String, Object> params = new HashMap<>(1);
            params.put("function", function);
            params.put("resource", new Resource());
            return functionMapper.selectExistsResourcesByFunction(params);
        } catch (IOException e) {
            throw new DatasetException("dataset.error", e);
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<FunctionResource> mutations(List<FunctionResource> functionResources) {
        if (CollectionUtils.isNotEmpty(functionResources)) {
            for (FunctionResource functionResource : functionResources) {
                functionResourceMapper.deleteFunctionResource(functionResource.getFunctionId(),
                        functionResource.getResourceId());
            }
            notifyCache(null);
        }
        return new ArrayList<>();
    }

    private void notifyCache(Long roleId) {
        messagePublisher.publish(DefaultRoleResourceListener.CACHE_ROLE_RESOURCE, roleId);
    }
}
