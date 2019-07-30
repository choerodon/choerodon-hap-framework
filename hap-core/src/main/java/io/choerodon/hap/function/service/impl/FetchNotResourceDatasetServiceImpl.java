package io.choerodon.hap.function.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.function.dto.Function;
import io.choerodon.hap.function.dto.FunctionResource;
import io.choerodon.hap.function.dto.Resource;
import io.choerodon.hap.function.mapper.FunctionMapper;
import io.choerodon.hap.function.service.IFetchNotResourceDatasetService;
import io.choerodon.hap.function.service.IFunctionResourceService;
import io.choerodon.hap.message.components.DefaultRoleResourceListener;
import io.choerodon.mybatis.service.BaseServiceImpl;
import io.choerodon.message.IMessagePublisher;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能资源 未挂靠资源服务接口实现.
 *
 * @author qiang.zeng
 * @since 2018/11/22.
 */
@Service
@Dataset("FetchNotResource")
public class FetchNotResourceDatasetServiceImpl extends BaseServiceImpl<Resource> implements IFetchNotResourceDatasetService, IDatasetService<FunctionResource> {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FunctionMapper functionMapper;

    @Autowired
    private IFunctionResourceService functionResourceService;

    @Autowired
    private IMessagePublisher messagePublisher;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Resource> queries(Map body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            FunctionResourceQuery functionResourceQuery = objectMapper.readValue(objectMapper.writeValueAsString(body), FunctionResourceQuery.class);
            Function function = new Function();
            function.setFunctionId(functionResourceQuery.getFunctionId());
            Resource resource = new Resource();
            resource.setName(functionResourceQuery.getName());
            resource.setUrl(functionResourceQuery.getUrl());
            PageHelper.startPage(page, pageSize);
            Map<String, Object> params = new HashMap<>(1);
            params.put("function", function);
            params.put("resource", resource);
            return functionMapper.selectNotExistsResourcesByFunction(params);
        } catch (IOException e) {
            throw new DatasetException("dataset.error", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<FunctionResource> mutations(List<FunctionResource> functionResources) {
        if (CollectionUtils.isNotEmpty(functionResources)) {
            for (FunctionResource functionResource : functionResources) {
                functionResourceService.insertSelective(functionResource);
            }
            notifyCache(null);
        }
        return functionResources;
    }

    private void notifyCache(Long roleId) {
        messagePublisher.publish(DefaultRoleResourceListener.CACHE_ROLE_RESOURCE, roleId);
    }
}
