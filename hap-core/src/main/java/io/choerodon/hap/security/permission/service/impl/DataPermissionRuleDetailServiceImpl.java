package io.choerodon.hap.security.permission.service.impl;

import io.choerodon.hap.security.permission.dto.DataPermissionRuleAssign;
import io.choerodon.hap.security.permission.dto.DataPermissionRuleDetail;
import io.choerodon.hap.security.permission.mapper.DataPermissionRuleAssignMapper;
import io.choerodon.hap.security.permission.service.IDataPermissionRuleDetailService;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.message.IMessagePublisher;
import io.choerodon.mybatis.entity.BaseDTO;
import io.choerodon.mybatis.service.BaseServiceImpl;
import io.choerodon.web.core.IRequest;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Dataset("PermissionRuleDetailSQL")
public class DataPermissionRuleDetailServiceImpl extends BaseServiceImpl<DataPermissionRuleDetail> implements IDataPermissionRuleDetailService, IDatasetService<DataPermissionRuleDetail> {

    @Autowired
    DataPermissionRuleAssignMapper dataPermissionRuleAssignMapper;

    @Autowired
    IMessagePublisher iMessagePublisher;


    @Override
    public List<DataPermissionRuleDetail> selectRuleManageDetail(DataPermissionRuleDetail dto, int page, int pageSize, IRequest request) throws IllegalAccessException {
        return selectOptions(dto, null, page, pageSize);
    }


    @Transactional(rollbackFor = Exception.class)
    public void removeDataMaskRuleDetailWithAssign(List<DataPermissionRuleDetail> dataMaskRuleManageDetails) {
        batchDelete(dataMaskRuleManageDetails);
        dataMaskRuleManageDetails.forEach(v -> {
            DataPermissionRuleAssign assign = new DataPermissionRuleAssign();
            assign.setDetailId(v.getDetailId());
            dataPermissionRuleAssignMapper.delete(assign);
        });
    }

    public List<DataPermissionRuleDetail> updateDataMaskRuleDetail(IRequest iRequest, List<DataPermissionRuleDetail> dto) {
        List<DataPermissionRuleDetail> dataMaskRuleManageDetails = self().batchUpdate(dto);

        updateCache(dto.get(0).getRuleId());

        return dataMaskRuleManageDetails;
    }

    public void removeDataMaskRuleDetail(List<DataPermissionRuleDetail> dataMaskRuleManageDetails) {
        self().removeDataMaskRuleDetailWithAssign(dataMaskRuleManageDetails);

        updateCache(dataMaskRuleManageDetails.get(0).getRuleId());
    }

    @Override
    public void updateCache(Long ruleId) {
        iMessagePublisher.publish("dataPermission.ruleRefresh", ruleId);
    }

    @Override
    public void deletePostFilter(Long detailId, Long ruleId) {
        DataPermissionRuleAssign assign = new DataPermissionRuleAssign();
        assign.setDetailId(detailId);
        dataPermissionRuleAssignMapper.delete(assign);
        updateCache(ruleId);
    }

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            DataPermissionRuleDetail example = new DataPermissionRuleDetail();
            BeanUtils.populate(example, body);
            return select(example, page, pageSize);
        }catch (Exception e){
            throw new DatasetException("dataset.error", e);
        }
    }

    @Override
    public List<DataPermissionRuleDetail> mutations(List<DataPermissionRuleDetail> objs) {
        batchUpdate(objs);
        for (DataPermissionRuleDetail detail : objs){
            switch (detail.get__status()){
                case BaseDTO.STATUS_ADD:
                    updateCache(detail.getRuleId());
                    break;
                case BaseDTO.STATUS_UPDATE:
                    updateCache(detail.getRuleId());
                    break;
                case BaseDTO.STATUS_DELETE:
                    deletePostFilter(detail.getDetailId(), detail.getRuleId());
                    break;
            }
        }
        return objs;
    }
}