package io.choerodon.hap.api.gateway.service.impl;

import io.choerodon.hap.api.ApiConstants;
import io.choerodon.hap.api.gateway.dto.ApiInterface;
import io.choerodon.hap.api.gateway.dto.ApiServer;
import io.choerodon.hap.api.gateway.mapper.ApiInterfaceMapper;
import io.choerodon.hap.api.gateway.mapper.ApiServerMapper;
import io.choerodon.hap.api.gateway.service.IApiImportService;
import io.choerodon.hap.api.gateway.service.IApiServerService;
import io.choerodon.hap.cache.impl.ApiServerCache;
import io.choerodon.hap.core.annotation.StdWho;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.intergration.service.IHapAuthenticationService;
import io.choerodon.hap.system.dto.DTOStatus;
import io.choerodon.mybatis.service.BaseServiceImpl;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 透传服务 Service - 实现类.
 *
 * @author peng.jiang@hand-china.com
 * @since 2017/9/15.
 */
@Service
@Dataset("ApiServer")
public class ApiServerServiceImpl extends BaseServiceImpl<ApiServer> implements IApiServerService, IDatasetService<ApiServer> {

    @Autowired
    private ApiServerMapper serverMapper;

    @Autowired
    private ApiInterfaceMapper interfaceMapper;

    @Resource(name = "restImportServer")
    private IApiImportService restImportServer;

    @Resource(name = "soapImportServer")
    private IApiImportService soapImportServer;

    @Autowired
    private ApiServerCache serverCache;

    @Autowired
    private IHapAuthenticationService authenticationService;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public List<ApiServer> selectByCodes(List<String> codeList) {
        return serverMapper.selectByCodes(codeList);
    }

    @Override
    public ApiServer importServer(ApiServer srServer) {

        String importUrl = srServer.getImportUrl();
        String[] s = importUrl.split("\\?");
        if (s.length == ApiConstants.NUMBER_2 && ApiConstants.SOAP_WSDL.equals(s[1].toLowerCase())) {
            srServer = soapImportServer.importServer(srServer);
        } else {
            srServer = restImportServer.importServer(srServer);
        }
        return srServer;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ApiServer> batchUpdate(List<ApiServer> servers) {
        for (ApiServer server : servers) {
            if (server.getServerId() == null) {
                self().insertServer(server);
            } else {
                self().updateServer(server);
            }
        }
        return servers;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiServer insertServer(ApiServer server) {
        serverMapper.insertSelective(server);
        if (server.getInterfaces() != null) {
            processInterfaces(server);
        }
        serverCache.reload(server);
        return server;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiServer updateServer(@StdWho ApiServer server) {
        ApiServer preServer = selectByPrimaryKey(server);
        int count = serverMapper.updateByPrimaryKey(server);
        checkOvn(count, server);
        if (server.getInterfaces() != null) {
            processInterfaces(server);
        }
        serverCache.remove(preServer);
        serverCache.reload(server);
        if (ApiConstants.AUTH_TYPE_OAUTH2.equalsIgnoreCase(server.getAuthType())) {
            authenticationService.removeToken(server);
        }
        return server;
    }

    private void processInterfaces(ApiServer server) {
        for (ApiInterface srInterface : server.getInterfaces()) {
            if (srInterface.getInterfaceId() == null) {
                // 设置头ID跟行ID一致
                srInterface.setServerId(server.getServerId());
                interfaceMapper.insertSelective(srInterface);
            } else {
                int count = interfaceMapper.updateByPrimaryKey(srInterface);
                checkOvn(count, srInterface);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDelete(List<ApiServer> servers) {
        for (ApiServer server : servers) {
            self().deleteByPrimaryKey(server);
        }
        return servers.size();
    }

    @Override
    public int deleteByPrimaryKey(ApiServer server) {
        interfaceMapper.removeByServerId(server.getServerId());
        int count = serverMapper.deleteByPrimaryKey(server);
        checkOvn(count, server);
        serverCache.remove(server);
        return count;
    }

    @Override
    public ApiServer getByMappingUrl(String serverUrl, String interfaceUrl) {
        return serverCache.getValue(serverUrl, interfaceUrl);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public List<ApiServer> selectNotExistsServerByApp(Map<String, Object> params) {
        return serverMapper.selectNotExistsServerByApp(params);
    }

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            ApiServer apiServer = new ApiServer();
            BeanUtils.populate(apiServer, body);
            return super.select(apiServer, page, pageSize);
        } catch (Exception e) {
            throw new DatasetException("dataset.error", e);
        }
    }

    @Override
    public List<ApiServer> mutations(List<ApiServer> apiServerList) {
        for (ApiServer server : apiServerList) {
            switch (server.get__status()) {
                case DTOStatus.ADD:
                    self().insertServer(server);
                    break;
                case DTOStatus.UPDATE:
                    self().updateServer(server);
                    break;
                case DTOStatus.DELETE:
                    self().deleteByPrimaryKey(server);
                    break;
                default:
                    break;
            }
        }
        return apiServerList;
    }
}