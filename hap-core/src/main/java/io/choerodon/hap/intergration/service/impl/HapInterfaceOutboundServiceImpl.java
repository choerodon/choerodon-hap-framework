package io.choerodon.hap.intergration.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.intergration.dto.HapInterfaceOutbound;
import io.choerodon.hap.intergration.mapper.HapInterfaceOutboundMapper;
import io.choerodon.hap.intergration.service.IHapInterfaceOutboundService;
import io.choerodon.mybatis.entity.Criteria;
import io.choerodon.mybatis.service.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@Dataset("Outbound")
@Transactional(rollbackFor = Exception.class)
public class HapInterfaceOutboundServiceImpl extends BaseServiceImpl<HapInterfaceOutbound> implements IHapInterfaceOutboundService, IDatasetService<HapInterfaceOutbound> {

    @Autowired
    private HapInterfaceOutboundMapper outboundMapper;

    @Autowired
    private ObjectMapper objectMapper;


    @Override
    public List<HapInterfaceOutbound> select(HapInterfaceOutbound condition, int pageNum,
                                             int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return outboundMapper.select(condition);
    }

    @Override
    public int outboundInvoke(HapInterfaceOutbound outbound) {
        return outboundMapper.insertSelective(outbound);
    }

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            HapInterfaceOutbound hapInterfaceOutbound = null;
            hapInterfaceOutbound = objectMapper.readValue(objectMapper.writeValueAsString(body), HapInterfaceOutbound.class);
            Criteria criteria = new Criteria().select("outboundId","interfaceName",
                    "interfaceUrl", "requestTime", "responseCode", "responseTime", "requestStatus");
            return selectOptions(hapInterfaceOutbound, criteria, page, pageSize);
        } catch (IOException e) {
            throw new DatasetException("dataset.error", e);
        }
    }

    @Override
    public List<HapInterfaceOutbound> mutations(List<HapInterfaceOutbound> objs) {
        return null;
    }
}