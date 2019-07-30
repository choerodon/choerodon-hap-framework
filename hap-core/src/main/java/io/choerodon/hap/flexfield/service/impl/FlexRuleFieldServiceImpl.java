package io.choerodon.hap.flexfield.service.impl;

import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.flexfield.dto.FlexRuleField;
import io.choerodon.hap.flexfield.mapper.FlexRuleFieldMapper;
import io.choerodon.hap.flexfield.service.IFlexRuleFieldService;
import io.choerodon.hap.system.dto.DTOStatus;
import io.choerodon.mybatis.service.BaseServiceImpl;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

@Service
@Dataset("FlexRuleField")
public class FlexRuleFieldServiceImpl extends BaseServiceImpl<FlexRuleField> implements IFlexRuleFieldService, IDatasetService<FlexRuleField> {

    @Autowired
    private FlexRuleFieldMapper flexRuleFieldMapper;

    @Override
    public List<FlexRuleField> queryFlexRuleField(FlexRuleField flexRuleField) {
        return flexRuleFieldMapper.queryFlexField(flexRuleField);
    }

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            FlexRuleField flexRuleSet = new FlexRuleField();
            BeanUtils.populate(flexRuleSet, body);
            return queryFlexRuleField(flexRuleSet);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new DatasetException("dataset.error.FlexRuleField", e);
        }
    }

    @Override
    public List<FlexRuleField> mutations(List<FlexRuleField> objs) {
        for (FlexRuleField flexRuleField : objs) {
            switch (flexRuleField.get__status()) {
                case DTOStatus.ADD:
                    insertSelective(flexRuleField);
                    break;
                case DTOStatus.UPDATE:
                    updateByPrimaryKeySelective(flexRuleField);
                    break;
                case DTOStatus.DELETE:
                    deleteByPrimaryKey(flexRuleField);
                    break;
            }
        }
        return objs;
    }
}