package io.choerodon.hap.system.service.impl;

import io.choerodon.hap.system.dto.ParameterConfig;
import io.choerodon.hap.system.mapper.ParameterConfigMapper;
import io.choerodon.hap.system.service.IParameterConfigService;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.system.dto.DTOStatus;
import io.choerodon.mybatis.service.BaseServiceImpl;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author qiang.zeng
 * @since 2017/11/6
 */
@Service
@Dataset("ParameterConfig")
@Transactional(rollbackFor = Exception.class)
public class ParameterConfigServiceImpl extends BaseServiceImpl<ParameterConfig> implements IParameterConfigService, IDatasetService<ParameterConfig> {
    @Autowired
    private ParameterConfigMapper parameterConfigMapper;

    @Override
    public List<ParameterConfig> selectByReportCode(String reportCode) {
        return parameterConfigMapper.selectByReportCode(reportCode);
    }

    @Override
    public List<ParameterConfig> selectByCodeAndTargetId(String code, Long targetId) {
        return parameterConfigMapper.selectByCodeAndTargetId(code, targetId);
    }

    @Override
    protected boolean useSelectiveUpdate() {
        return false;
    }

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            ParameterConfig parameterConfig = new ParameterConfig();
            BeanUtils.populate(parameterConfig, body);
            return self().selectByCodeAndTargetId(parameterConfig.getCode(), parameterConfig.getTargetId());
        } catch (Exception e) {
            throw new DatasetException("dataset.error", e);
        }
    }

    @Override
    public List<ParameterConfig> mutations(List<ParameterConfig> parameterConfigs) {
        for (ParameterConfig parameterConfig : parameterConfigs) {
            switch (parameterConfig.get__status()) {
                case DTOStatus.ADD:
                    super.insertSelective(parameterConfig);
                    break;
                case DTOStatus.UPDATE:
                    super.updateByPrimaryKey(parameterConfig);
                    break;
                case DTOStatus.DELETE:
                    super.deleteByPrimaryKey(parameterConfig);
                    break;
            }
        }
        return parameterConfigs;
    }
}
