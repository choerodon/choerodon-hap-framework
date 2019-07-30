package io.choerodon.hap.flexfield.service.impl;

import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.flexfield.dto.FlexRuleDetail;
import io.choerodon.hap.flexfield.service.IFlexRuleDetailService;
import io.choerodon.hap.system.dto.DTOStatus;
import io.choerodon.mybatis.service.BaseServiceImpl;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

@Service
@Dataset("FlexRuleDetail")
public class FlexRuleDetailServiceImpl extends BaseServiceImpl<FlexRuleDetail> implements IFlexRuleDetailService, IDatasetService<FlexRuleDetail> {

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            FlexRuleDetail flexRuleDetail = new FlexRuleDetail();
            BeanUtils.populate(flexRuleDetail, body);
            return select(flexRuleDetail, page, pageSize);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new DatasetException("dataset.error.FlexRuleDetail", e);
        }
    }

    @Override
    public List<FlexRuleDetail> mutations(List<FlexRuleDetail> objs) {
        for (FlexRuleDetail flexRuleDetail : objs) {
            switch (flexRuleDetail.get__status()) {
                case DTOStatus.ADD:
                    insertSelective(flexRuleDetail);
                    break;
                case DTOStatus.UPDATE:
                    updateByPrimaryKeySelective(flexRuleDetail);
                    break;
                case DTOStatus.DELETE:
                    deleteByPrimaryKey(flexRuleDetail);
                    break;
            }
        }
        return objs;
    }
}