package io.choerodon.hap.flexfield.service.impl;

import com.github.pagehelper.PageHelper;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.flexfield.dto.FlexRule;
import io.choerodon.hap.flexfield.dto.FlexRuleSet;
import io.choerodon.hap.flexfield.mapper.FlexRuleMapper;
import io.choerodon.hap.flexfield.mapper.FlexRuleSetMapper;
import io.choerodon.hap.flexfield.service.IFlexRuleService;
import io.choerodon.hap.flexfield.service.IFlexRuleSetService;
import io.choerodon.hap.system.dto.DTOStatus;
import io.choerodon.mybatis.service.BaseServiceImpl;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

@Service
@Dataset("FlexRuleSet")
public class FlexRuleSetServiceImpl extends BaseServiceImpl<FlexRuleSet> implements IFlexRuleSetService, IDatasetService<FlexRuleSet> {

    @Autowired
    FlexRuleSetMapper flexRuleSetMapper;

    @Autowired
    FlexRuleMapper flexRuleMapper;

    @Autowired
    IFlexRuleService iFlexRuleService;

    @Override
    public List<FlexRuleSet> queryFlexModel(FlexRuleSet model, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return flexRuleSetMapper.queryFlexRuleSet(model);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRuleSet(List<FlexRuleSet> flexRuleSets) {
        for (FlexRuleSet ruleSet : flexRuleSets) {
            FlexRule flexRule = new FlexRule();
            flexRule.setRuleSetId(ruleSet.getRuleSetId());
            List<FlexRule> flexRules = flexRuleMapper.select(flexRule);
            iFlexRuleService.deleteRule(flexRules);
            int updateCount = flexRuleSetMapper.delete(ruleSet);
            checkOvn(updateCount, ruleSet);
        }
    }

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            FlexRuleSet flexRuleSet = new FlexRuleSet();
            BeanUtils.populate(flexRuleSet, body);
            return queryFlexModel(flexRuleSet, page, pageSize);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new DatasetException("dataset.error.FlexRuleSet", e);
        }
    }

    @Override
    public List<FlexRuleSet> mutations(List<FlexRuleSet> objs) {
        for (FlexRuleSet flexRuleSet : objs) {
            switch (flexRuleSet.get__status()) {
                case DTOStatus.ADD:
                    insertSelective(flexRuleSet);
                    break;
                case DTOStatus.UPDATE:
                    updateByPrimaryKeySelective(flexRuleSet);
                    break;
                case DTOStatus.DELETE:
                    deleteByPrimaryKey(flexRuleSet);
                    break;
            }
        }
        return objs;
    }
}