package io.choerodon.hap.api.gateway.service.impl;

import com.github.pagehelper.PageHelper;
import io.choerodon.hap.api.ApiConstants;
import io.choerodon.hap.api.application.dto.ApiAccessLimit;
import io.choerodon.hap.api.gateway.dto.ApiInterface;
import io.choerodon.hap.api.gateway.dto.ApiServer;
import io.choerodon.hap.api.gateway.mapper.ApiInterfaceMapper;
import io.choerodon.hap.api.gateway.mapper.ApiServerMapper;
import io.choerodon.hap.api.gateway.service.IApiInterfaceService;
import io.choerodon.hap.cache.impl.ApiServerCache;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.mybatis.service.BaseServiceImpl;
import io.choerodon.web.core.IRequest;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 接口Service - 实现类.
 *
 * @author lijian.yin@hand-china.com
 **/

@Service
@Dataset("ApiInterface")
public class ApiInterfaceServiceImpl extends BaseServiceImpl<ApiInterface> implements IApiInterfaceService, IDatasetService<ApiInterface> {

    @Autowired
    private ApiInterfaceMapper mapper;

    @Autowired
    private ApiServerMapper serverMapper;

    @Autowired
    private ApiServerCache serverCache;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<ApiInterface> selectByServerId(IRequest request, ApiInterface srInterface) {
        return mapper.selectByServerId(srInterface);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<ApiInterface> selectByServerIdWithLimit(IRequest request, String clientId, Long serverId) {
        List<ApiInterface> apiInterfaces = mapper.selectByServerIdWithLimit(clientId, serverId);

        for (ApiInterface apiInterface : apiInterfaces) {
            if (null == apiInterface.getApiAccessLimit()) {
                apiInterface.setApiAccessLimit(new ApiAccessLimit());
            }
            if (null == apiInterface.getApiAccessLimit().getAccessFlag()) {
                apiInterface.getApiAccessLimit().setAccessFlag(ApiConstants.ENABLE_FLAG_Y);
            }
        }
        return apiInterfaces;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<ApiInterface> selectInterfacesByServerCode(IRequest requestContext, String clientId, String serverCode) {
        List<ApiInterface> apiInterfaces = mapper.selectInterfacesByServerCode(serverCode);
        apiInterfaces.forEach(apiInterface -> {
            ApiAccessLimit apiAccessLimit = new ApiAccessLimit();
            apiAccessLimit.setAccessFlag("Y");
            apiAccessLimit.setClientId(clientId);
            apiAccessLimit.setServerCode(serverCode);
            apiAccessLimit.setInterfaceCode(apiInterface.getCode());
            apiInterface.setApiAccessLimit(apiAccessLimit);
        });
        return apiInterfaces;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public int batchDelete(List<ApiInterface> list) {
        int count = super.batchDelete(list);
        if (count > 0) {
            ApiServer server = serverMapper.selectByPrimaryKey(list.get(0).getServerId());
            serverCache.removeInterface(server, list);
        }
        return count;
    }

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            ApiInterface apiInterface = new ApiInterface();
            BeanUtils.populate(apiInterface, body);
            if (apiInterface.getServerId() != null && apiInterface.getServerId() != -1L) {
                PageHelper.startPage(page, pageSize);
                return selectByServerId(null, apiInterface);
            }
            return null;
        } catch (Exception e) {
            throw new DatasetException("dataset.error", e);
        }
    }

    @Override
    public List<ApiInterface> mutations(List<ApiInterface> objs) {
        self().batchDelete(objs);
        return objs;
    }
}