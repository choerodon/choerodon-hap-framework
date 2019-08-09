package io.choerodon.hap.intergration.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import io.choerodon.hap.core.components.UserLoginInfoCollection;
import io.choerodon.hap.core.util.ExceptionUtil;
import io.choerodon.hap.intergration.beans.HapInvokeInfo;
import io.choerodon.hap.intergration.controllers.HapInvokeRequestBodyAdvice;
import io.choerodon.hap.intergration.dto.HapInterfaceInbound;
import io.choerodon.hap.intergration.mapper.HapInterfaceInboundMapper;
import io.choerodon.hap.intergration.service.IHapInterfaceInboundService;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.mybatis.entity.Criteria;
import io.choerodon.mybatis.service.BaseServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@Dataset("Inbound")
@Transactional(rollbackFor = Exception.class)
public class HapInterfaceInboundServiceImpl extends BaseServiceImpl<HapInterfaceInbound> implements IHapInterfaceInboundService, IDatasetService<HapInterfaceInbound> {

    @Autowired
    private HapInterfaceInboundMapper inboundMapper;

    @Autowired
    private ObjectMapper objectMapper;


    @Override
    @Deprecated
    public int inboundInvoke(HttpServletRequest request, HapInterfaceInbound inbound, Throwable throwable) {

        // 处理一些请求信息
        String ipAddress = UserLoginInfoCollection.getIpAddress(request);
        inbound.setIp(ipAddress);
        if (inbound.getRequestMethod() == null)
            inbound.setRequestMethod(request.getMethod());
        if (inbound.getInterfaceUrl() == null)
            inbound.setInterfaceUrl(request.getServletPath());
        if (inbound.getRequestHeaderParameter() == null)
            inbound.setRequestHeaderParameter(request.getQueryString());
        if (inbound.getRequestBodyParameter() == null)
            inbound.setRequestBodyParameter(HapInvokeRequestBodyAdvice.getAndRemoveBody());
        inbound.setReferer(StringUtils.abbreviate(request.getHeader("Referer"), 240));
        if (throwable != null) {
            // 获取异常堆栈
            inbound.setStackTrace(ExceptionUtil.getRootCauseStackTrace(throwable));
            inbound.setRequestStatus(HapInvokeInfo.REQUEST_FAILURE);
        }
        return inboundInvoke(inbound);

    }

    @Override
    public int inboundInvoke(HapInterfaceInbound inbound) {
        inbound.setReferer(StringUtils.abbreviate(inbound.getReferer(), 240));
        return inboundMapper.insertSelective(inbound);
    }

    @Override
    public List<HapInterfaceInbound> select(HapInterfaceInbound condition, int pageNum,
                                            int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return inboundMapper.select(condition);
    }

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {

        try {
            HapInterfaceInbound hapInterfaceInbound = null;
            hapInterfaceInbound = objectMapper.readValue(objectMapper.writeValueAsString(body), HapInterfaceInbound.class);
            Criteria criteria = new Criteria().select("inboundId","interfaceName", "interfaceUrl", "requestTime",
                    "requestMethod", "ip", "responseTime", "requestStatus");
            return selectOptions(hapInterfaceInbound, criteria, page, pageSize);
        } catch (IOException e) {
            throw new DatasetException("dataset.error", e);
        }
    }

    @Override
    public List<HapInterfaceInbound> mutations(List<HapInterfaceInbound> objs) {
        return null;
    }
}