package io.choerodon.hap.api.application.service.impl;

import com.github.pagehelper.PageHelper;
import io.choerodon.hap.api.application.dto.ApiApplication;
import io.choerodon.hap.api.application.mapper.ApiApplicationMapper;
import io.choerodon.hap.api.application.service.IApiAccessLimitService;
import io.choerodon.hap.api.application.service.IApiApplicationService;
import io.choerodon.hap.api.gateway.dto.ApiServer;
import io.choerodon.hap.api.gateway.service.IApiServerService;
import io.choerodon.hap.core.annotation.StdWho;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.security.oauth.dto.Oauth2ClientDetails;
import io.choerodon.hap.security.oauth.service.IOauth2ClientDetailsService;
import io.choerodon.hap.system.dto.DTOStatus;
import io.choerodon.mybatis.service.BaseServiceImpl;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 应用service - 实现类
 *
 * @author lijian.yin@hand-china.com
 **/

@Service
@Dataset("ApiApplication")
public class ApiApplicationServiceImpl extends BaseServiceImpl<ApiApplication> implements IApiApplicationService, IDatasetService<ApiApplication> {

    @Autowired
    private ApiApplicationMapper applicationMapper;

    @Autowired
    private IOauth2ClientDetailsService clientService;

    @Autowired
    private IApiServerService serverService;

    @Autowired
    private IApiAccessLimitService accessLimitService;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public ApiApplication getById(Long applicationId) {
        ApiApplication application = applicationMapper.getById(applicationId);
        String[] ids = StringUtils.commaDelimitedListToStringArray(application.getClient().getScope());
        if (ids != null && ids.length > 0) {
            application.setServers(serverService.selectByCodes(Arrays.asList(ids)));
        }
        return application;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public List<ApiServer> getService(Long applicationId, int page, int pageSize) {
        List<ApiServer> list = new ArrayList<>();
        ApiApplication application = applicationMapper.getById(applicationId);
        String[] ids = StringUtils.commaDelimitedListToStringArray(application.getClient().getScope());
        if (ids != null && ids.length > 0) {
            PageHelper.startPage(page, pageSize);
            list = serverService.selectByCodes(Arrays.asList(ids));
        }
        return list;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public List<ApiApplication> selectApplications(ApiApplication apiApplication, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return applicationMapper.selectApplications(apiApplication);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public List<ApiServer> selectNotExistsServerByApp(String exitsCodes, ApiServer server, int page, int pageSize) {
        Map<String, Object> params = new HashMap<>(2);
        if (!StringUtils.isEmpty(exitsCodes)) {
            params.put("codeList", StringUtils.commaDelimitedListToStringArray(exitsCodes));
        }
        params.put("server", server);
        PageHelper.startPage(page, pageSize);
        return serverService.selectNotExistsServerByApp(params);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ApiApplication> batchUpdate(List<ApiApplication> list) {
        for (ApiApplication application : list) {
            if (application.getApplicationId() != null) {
                self().updateApplication(application);
            } else {
                self().insertApplication(application);
            }
        }
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiApplication insertApplication(ApiApplication application) {
        Oauth2ClientDetails client = clientService.insertSelective(application.getClient());
        application.setCliId(client.getId());
        applicationMapper.insertSelective(application);
        // 绑定解绑server时，interface级联
        accessLimitService.updateByApplication("", application);
        return application;
    }

    @Override
    public ApiApplication insertApplicationUP(ApiApplication application) {
        Oauth2ClientDetails client = clientService.insertSelective(application.getClient());
        application.setCliId(client.getId());
        applicationMapper.insertSelective(application);
        // 绑定解绑server时，interface级联
        accessLimitService.updateByApplication(application);
        return application;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiApplication updateApplication(@StdWho ApiApplication application) {
        // 修改application
        updateByPrimaryKey(application);
        // 级联操作
        Oauth2ClientDetails oauth2ClientDetails =
                clientService.selectByClientId(application.getClient().getClientId());
//        application.getClient().setApplicationCode(application.getCode());
        clientService.updateClient(application.getClient());
        String before = oauth2ClientDetails.getScope();
        // 解绑时 删除;
        accessLimitService.updateByApplication(before, application);
        //级联修改AccessLimit
        accessLimitService.updateAccessLimit(application);
        return application;
    }

    @Override
    public ApiApplication updateApplicationUP(ApiApplication application) {
        // 修改application
        updateByPrimaryKey(application);
        // 级联操作
        clientService.updateClient(application.getClient());
        // servers绑定解绑处理;s
        accessLimitService.updateByApplication(application);
        //级联修改AccessLimit
        accessLimitService.updateAccessLimit(application);
        return application;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByPrimaryKey(ApiApplication record) {
        Long cliId = record.getCliId();
        Oauth2ClientDetails clientDetails = new Oauth2ClientDetails();
        clientDetails.setId(cliId);
        // 删除application时，删除对应访问权限记录
        accessLimitService.removeByClientId(cliId);
        clientService.deleteByPrimaryKey(clientDetails);
        return applicationMapper.deleteByPrimaryKey(record);
    }

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            ApiApplication apiApplication = new ApiApplication();
            BeanUtils.populate(apiApplication, body);
            if (apiApplication.getApplicationId() != null) {
                List<ApiApplication> list = new ArrayList<>();
                list.add(self().getById(apiApplication.getApplicationId()));
                return list;
            } else {
                return self().selectApplications(apiApplication, page, pageSize);

            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new DatasetException("dataset.error.ApiApplication", e);
        }
    }

    @Override
    public List<ApiApplication> mutations(List<ApiApplication> objs) {
        for (ApiApplication apiApplication : objs) {
            switch (apiApplication.get__status()) {
                case DTOStatus.ADD:
                    self().insertApplicationUP(apiApplication);
                    break;
                case DTOStatus.UPDATE:
                    self().updateApplicationUP(apiApplication);
                    break;
                case DTOStatus.DELETE:
                    super.deleteByPrimaryKey(apiApplication);
                    break;
                default:
                    break;
            }
        }
        return objs;
    }

}