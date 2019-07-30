package io.choerodon.hap.security.permission.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.security.permission.dto.DataPermissionRule;
import io.choerodon.hap.security.permission.dto.DataPermissionRuleDetail;
import io.choerodon.hap.security.permission.dto.DataPermissionTableRule;
import io.choerodon.hap.security.permission.mapper.DataPermissionRuleDetailMapper;
import io.choerodon.hap.security.permission.mapper.DataPermissionRuleMapper;
import io.choerodon.hap.security.permission.mapper.DataPermissionTableRuleMapper;
import io.choerodon.hap.security.permission.service.IDataPermissionRuleDetailService;
import io.choerodon.hap.security.permission.service.IDataPermissionRuleService;
import io.choerodon.hap.security.permission.service.IDataPermissionTableRuleService;
import io.choerodon.message.IMessagePublisher;
import io.choerodon.mybatis.common.query.Comparison;
import io.choerodon.mybatis.common.query.WhereField;
import io.choerodon.mybatis.entity.BaseDTO;
import io.choerodon.mybatis.entity.Criteria;
import io.choerodon.mybatis.service.BaseServiceImpl;
import io.choerodon.web.core.IRequest;

/**
 * @author jialong.zuo@hand-china.com
 */
@Service
@Dataset("PermissionRule")
@Transactional(rollbackFor = Exception.class)
public class DataPermissionRuleServiceImpl extends BaseServiceImpl<DataPermissionRule> implements IDataPermissionRuleService, IDatasetService<DataPermissionRule> {
    @Autowired
    private DataPermissionRuleMapper dataPermissionRuleMapper;

    @Autowired
    private DataPermissionRuleDetailMapper dataPermissionRuleDetailMapper;

    @Autowired
    private IDataPermissionRuleDetailService iDataPermissionRuleDetailService;

    @Autowired
    private IDataPermissionTableRuleService iDataPermissionTableRuleService;

    @Autowired
    private DataPermissionTableRuleMapper dataPermissionTableRuleMapper;

    @Autowired
    private IMessagePublisher iMessagePublisher;

    @Override
    public List<DataPermissionRule> selectRuleWithoutTableSelect(DataPermissionRule dataPermissionRule, IRequest iRequest, int page, int pageSize) {
        return dataPermissionRuleMapper.selectRuleWithoutTableSelect(dataPermissionRule);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeRuleWithDetail(List<DataPermissionRule> dataMaskRuleManages) {

        dataMaskRuleManages.forEach(v -> {
            DataPermissionRuleDetail detail = new DataPermissionRuleDetail();
            detail.setRuleId(v.getRuleId());
            List<DataPermissionRuleDetail> detailList = dataPermissionRuleDetailMapper.select(detail);
            iDataPermissionRuleDetailService.removeDataMaskRuleDetailWithAssign(detailList);

            DataPermissionTableRule tableRule = new DataPermissionTableRule();
            tableRule.setRuleId(v.getRuleId());
            List<DataPermissionTableRule> ruleList = dataPermissionTableRuleMapper.select(tableRule);
            iDataPermissionTableRuleService.removeRule(ruleList);
        });

        batchDelete(dataMaskRuleManages);

    }

    @Override
    public void removeRule(List<DataPermissionRule> dataPermissionRules) {
        self().removeRuleWithDetail(dataPermissionRules);
        dataPermissionRules.forEach(v -> {
            iMessagePublisher.publish("dataPermission.ruleRemove", v.getRuleId());
        });
    }

    public void deletePostFilter(Long ruleId) {
        iMessagePublisher.publish("dataPermission.ruleRemove", ruleId);
        DataPermissionRuleDetail detail = new DataPermissionRuleDetail();
        detail.setRuleId(ruleId);
        List<DataPermissionRuleDetail> detailList = dataPermissionRuleDetailMapper.select(detail);
        iDataPermissionRuleDetailService.removeDataMaskRuleDetailWithAssign(detailList);
        DataPermissionTableRule tableRule = new DataPermissionTableRule();
        tableRule.setRuleId(ruleId);
        List<DataPermissionTableRule> ruleList = dataPermissionTableRuleMapper.select(tableRule);
        iDataPermissionTableRuleService.removeRule(ruleList);
    }

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            DataPermissionRule example = new DataPermissionRule();
            BeanUtils.populate(example, body);
            Criteria criteria = new Criteria(example);
            criteria.where(new WhereField(DataPermissionRule.FIELD_RULE_CODE, Comparison.LIKE), new WhereField(DataPermissionRule.FIELD_RULE_NAME, Comparison.LIKE));
            return selectOptions(example, criteria, page, pageSize);
        } catch (Exception e) {
            throw new DatasetException("dataset.error", e);
        }
    }

    @Override
    public List<DataPermissionRule> mutations(List<DataPermissionRule> objs) {
        batchUpdate(objs);
        for (DataPermissionRule rule : objs) {
            if (rule.get__status().equals(BaseDTO.STATUS_DELETE)) {
                deletePostFilter(rule.getRuleId());
            }
        }
        return objs;
    }
}