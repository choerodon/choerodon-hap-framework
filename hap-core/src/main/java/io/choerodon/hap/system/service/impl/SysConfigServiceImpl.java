package io.choerodon.hap.system.service.impl;

import io.choerodon.hap.core.annotation.StdWho;
import io.choerodon.hap.message.components.GlobalProfileSubscriber;
import io.choerodon.hap.system.dto.GlobalProfile;
import io.choerodon.hap.system.dto.SysConfig;
import io.choerodon.hap.system.mapper.SysConfigMapper;
import io.choerodon.hap.system.service.ISysConfigService;
import io.choerodon.base.annotation.CacheSet;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.system.dto.DTOStatus;
import io.choerodon.message.IMessagePublisher;
import io.choerodon.mybatis.service.BaseServiceImpl;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 系统配置服务实现.
 *
 * @author hailin.xu@hand-china.com
 */
@Service
@Dataset("SysConfig")
public class SysConfigServiceImpl extends BaseServiceImpl<SysConfig> implements ISysConfigService, IDatasetService<SysConfig> {

    @Autowired
    private SysConfigMapper configMapper;

    @Autowired
    private IMessagePublisher messagePublisher;

    @Override
    @CacheSet(cache = "config")
    public SysConfig insertSelective(@StdWho SysConfig config) {
        super.insertSelective(config);
        //配置更改时 通知监听者
        messagePublisher.publish(GlobalProfileSubscriber.CONFIG,
                new GlobalProfile(config.getConfigCode(), config.getConfigValue()));
        return config;
    }

    @Override
    @CacheSet(cache = "config")
    public SysConfig updateByPrimaryKeySelective(@StdWho SysConfig config) {
        super.updateByPrimaryKeySelective(config);
        //配置更改时 通知监听者
        messagePublisher.publish(GlobalProfileSubscriber.CONFIG,
                new GlobalProfile(config.getConfigCode(), config.getConfigValue()));
        return config;
    }

    @Override
    public String getConfigValue(String configCode) {
        SysConfig config = configMapper.selectByCode(configCode);
        if (config != null) {
            return config.getConfigValue();
        } else {
            return null;
        }
    }

    @Override
    public String updateSystemImageVersion(String type) {
        String tag = DateFormatUtils.format(new Date(), "yyyyMMddHHmmss");
        messagePublisher.publish(GlobalProfileSubscriber.CONFIG, new GlobalProfile(type, tag));
        return tag;
    }

    @Override
    public SysConfig selectByCode(String configCode) {
        return configMapper.selectByCode(configCode);
    }

    @Override
    public List<SysConfig> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        return super.selectAll();
    }

    @Override
    public List<SysConfig> mutations(List<SysConfig> sysConfigs) {
        for (SysConfig sysConfig : sysConfigs) {
            if (sysConfig.getConfigCode().equals("PASSWORD_MIN_LENGTH")) {
                if (Integer.parseInt(sysConfig.getConfigValue()) < 6) {
                    sysConfig.setConfigValue("6");
                } else if (Integer.parseInt(sysConfig.getConfigValue()) > 16) {
                    sysConfig.setConfigValue("16");
                }
            }
            switch (sysConfig.get__status()) {
                case DTOStatus.ADD:
                    self().insertSelective(sysConfig);
                    break;
                case DTOStatus.UPDATE:
                    self().updateByPrimaryKeySelective(sysConfig);
                    break;
            }
        }
        return sysConfigs;
    }
}
