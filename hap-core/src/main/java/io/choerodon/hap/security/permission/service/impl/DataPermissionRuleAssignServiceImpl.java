package io.choerodon.hap.security.permission.service.impl;

import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.security.permission.dto.DataPermissionRuleAssign;
import io.choerodon.hap.security.permission.service.IDataPermissionRuleAssignService;
import io.choerodon.message.IMessagePublisher;
import io.choerodon.mybatis.entity.BaseDTO;
import io.choerodon.mybatis.service.BaseServiceImpl;
import io.choerodon.web.core.IRequest;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author zuojialong
 */
@Service
@Dataset("PermissionRuleAssign")
public class DataPermissionRuleAssignServiceImpl extends BaseServiceImpl<DataPermissionRuleAssign> implements IDataPermissionRuleAssignService, IDatasetService<DataPermissionRuleAssign> {

    @Autowired
    private IMessagePublisher iMessagePublisher;

    @Override
    public List<DataPermissionRuleAssign> selectRuleAssign(DataPermissionRuleAssign dto, int page,
                                                           int pageSize, IRequest request)
            throws IllegalAccessException {

        return selectOptions(dto, null, page, pageSize);
    }

    @Override
    public void removeDataMaskRuleAssign(List<DataPermissionRuleAssign> dataMaskRuleAssigns) {
        self().batchDelete(dataMaskRuleAssigns);
        updateCache(dataMaskRuleAssigns.get(0).getRuleId());
    }

    @Override
    public List<DataPermissionRuleAssign> updateDataMaskRuleAssign(IRequest request, List<DataPermissionRuleAssign> dataMaskRuleAssigns) {
        List<DataPermissionRuleAssign> dto = self().batchUpdate(dataMaskRuleAssigns);
        updateCache(dataMaskRuleAssigns.get(0).getRuleId());
        return dto;
    }

    public void updateCache(Long ruleId) {
        iMessagePublisher.publish("dataPermission.ruleRefresh", ruleId);
    }

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            DataPermissionRuleAssign example = new DataPermissionRuleAssign();
            BeanUtils.populate(example, body);
            return selectOptions(example, null, page, pageSize);
        } catch (Exception e) {
            throw new DatasetException("dataset.error", e);
        }
    }

    @Override
    public List<DataPermissionRuleAssign> mutations(List<DataPermissionRuleAssign> objs) {
        batchUpdate(objs);
        for (DataPermissionRuleAssign assign : objs) {
            switch (assign.get__status()) {
                case BaseDTO.STATUS_ADD:
                case BaseDTO.STATUS_UPDATE:
                case BaseDTO.STATUS_DELETE:
                    updateCache(assign.getRuleId());
                    break;
            }
        }
        return objs;
    }
}