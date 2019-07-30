package io.choerodon.hap.flexfield.service.impl;

import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.flexfield.dto.FlexModel;
import io.choerodon.hap.flexfield.dto.FlexModelColumn;
import io.choerodon.hap.flexfield.dto.FlexRuleSet;
import io.choerodon.hap.flexfield.mapper.FlexModelColumnMapper;
import io.choerodon.hap.flexfield.mapper.FlexModelMapper;
import io.choerodon.hap.flexfield.mapper.FlexRuleSetMapper;
import io.choerodon.hap.flexfield.service.IFlexModelService;
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
@Dataset("FlexModel")
public class FlexModelServiceImpl extends BaseServiceImpl<FlexModel> implements IFlexModelService, IDatasetService<FlexModel> {

    @Autowired
    private FlexModelColumnMapper modelColumnMapper;

    @Autowired
    private FlexModelMapper modelMapper;


    @Autowired
    private IFlexRuleSetService setService;

    @Autowired
    private FlexRuleSetMapper flexRuleSetMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFlexModel(List<FlexModel> models) {
        for (FlexModel flexModel : models) {
            int updateCount = modelMapper.deleteByPrimaryKey(flexModel);
            checkOvn(updateCount, flexModel);
            modelColumnMapper.delete(new FlexModelColumn(flexModel.getModelId()));
        }

        models.forEach(v -> {
            FlexRuleSet flexModelSet = new FlexRuleSet();
            flexModelSet.setModelId(v.getModelId());
            setService.deleteRuleSet(flexRuleSetMapper.select(flexModelSet));
        });

    }

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            FlexModel flexModel = new FlexModel();
            BeanUtils.populate(flexModel, body);
            return select(flexModel, page, pageSize);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new DatasetException("dataset.error.FlexModel", e);
        }
    }

    @Override
    public List<FlexModel> mutations(List<FlexModel> objs) {
        for (FlexModel flexModel : objs) {
            switch (flexModel.get__status()) {
                case DTOStatus.ADD:
                    insertSelective(flexModel);
                    break;
                case DTOStatus.UPDATE:
                    updateByPrimaryKeySelective(flexModel);
                    break;
                case DTOStatus.DELETE:
                    deleteByPrimaryKey(flexModel);
                    break;
            }
        }
        return objs;
    }
}