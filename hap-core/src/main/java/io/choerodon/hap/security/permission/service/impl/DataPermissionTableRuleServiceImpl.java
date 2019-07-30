package io.choerodon.hap.security.permission.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import io.choerodon.hap.security.permission.dto.DataPermissionTableRule;
import io.choerodon.hap.security.permission.mapper.DatasetMapper;
import io.choerodon.hap.security.permission.service.IDataPermissionTableRuleService;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.message.IMessagePublisher;
import io.choerodon.mybatis.entity.BaseDTO;
import io.choerodon.mybatis.service.BaseServiceImpl;
import io.choerodon.web.core.IRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author jialong.zuo@hand-china.com
 */
@Service
@Dataset("PermissionTableRule")
public class DataPermissionTableRuleServiceImpl extends BaseServiceImpl<DataPermissionTableRule> implements IDataPermissionTableRuleService, IDatasetService<DataPermissionTableRule> {

    @Autowired
    private IMessagePublisher iMessagePublisher;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DatasetMapper datasetMapper;

    @Override
    public void removeRule(List<DataPermissionTableRule> list) {
        batchDelete(list);
        updateCache(list, "dataPermission.tableRuleRemove");
    }

    @Override
    public List<DataPermissionTableRule> updateRule(IRequest request, List<DataPermissionTableRule> list) {
        List<DataPermissionTableRule> dto = batchUpdate(list);
        updateCache(list, "dataPermission.tableRuleUpdate");
        return dto;
    }

    private void updateCache(List<DataPermissionTableRule> list, String channel) {
        list.forEach(v -> {
            iMessagePublisher.publish(channel, v);
        });
    }

    public void updatePostFilter(DataPermissionTableRule dataPermissionTableRule) {
        iMessagePublisher.publish("dataPermission.tableRuleUpdate", dataPermissionTableRule);
    }

    public void deletePostFilter(DataPermissionTableRule dataPermissionTableRule) {
        iMessagePublisher.publish("dataPermission.tableRuleRemove", dataPermissionTableRule);
    }

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        PageHelper.startPage(page, pageSize);
        return datasetMapper.selectTableRule(body);
    }

    @Override
    public List<DataPermissionTableRule> mutations(List<DataPermissionTableRule> objs) {
        batchUpdate(objs);
        for (DataPermissionTableRule tableRule : objs){
            switch (tableRule.get__status()){
                case BaseDTO.STATUS_ADD:
                    updatePostFilter(tableRule);
                    break;
                case BaseDTO.STATUS_UPDATE:
                    updatePostFilter(tableRule);
                    break;
                case BaseDTO.STATUS_DELETE:
                    deletePostFilter(tableRule);
                    break;
            }
        }
        return objs;
    }
}