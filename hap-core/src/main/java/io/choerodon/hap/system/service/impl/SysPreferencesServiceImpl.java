package io.choerodon.hap.system.service.impl;

import io.choerodon.hap.system.dto.SysPreferences;
import io.choerodon.hap.system.mapper.SysPreferencesMapper;
import io.choerodon.hap.system.service.ISysPreferencesService;
import io.choerodon.mybatis.service.BaseServiceImpl;
import io.choerodon.redis.impl.HashStringRedisCacheGroup;
import io.choerodon.web.core.IRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 系统首选项service.
 *
 * @author zhangYang
 */
@Service
public class SysPreferencesServiceImpl extends BaseServiceImpl<SysPreferences> implements ISysPreferencesService {
    private Logger logger = LoggerFactory.getLogger(SysPreferencesServiceImpl.class);

    @Autowired
    private SysPreferencesMapper sysPreferencesMapper;

    @Autowired
    @Qualifier("preferenceCache")
    private HashStringRedisCacheGroup<SysPreferences> preferenceCache;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<SysPreferences> saveSysPreferences(IRequest requestContext, List<SysPreferences> preferences) {
        if (preferences.isEmpty()) {
            if (logger.isDebugEnabled()) {
                logger.debug("sysPreferences is null");
            }
        }
        for (SysPreferences sysPreferences : preferences) {
            sysPreferences.setUserId(requestContext.getUserId());
            //TODO 检查！！！
            if (sysPreferences.getPreferencesId() == null) {
                int count = sysPreferencesMapper.insertSelective(sysPreferences);
                checkOvn(count, sysPreferences);
                preferenceCache.setValue(sysPreferences.getUserId() + "", sysPreferences.getPreferences(),
                        sysPreferences);
            } else {
                int count = sysPreferencesMapper.updatePreferLine(sysPreferences);
                checkOvn(count, sysPreferences);
                preferenceCache.remove(sysPreferences.getUserId() + "", sysPreferences.getPreferences());
                preferenceCache.setValue(sysPreferences.getUserId() + "", sysPreferences.getPreferences(),
                        sysPreferences);
            }
        }
        return preferences;
    }

    @Override
    public List<SysPreferences> querySysPreferences(IRequest requestContext, SysPreferences preferences) {
        List<SysPreferences> list = preferenceCache.getGroupAll(preferences.getUserId() + "");
        if (list != null && !list.isEmpty()) {
            return list;
        }
        List<SysPreferences> pres = sysPreferencesMapper.select(preferences);
        pres.forEach(pre -> {
            preferenceCache.setValue(pre.getUserId() + "", pre.getPreferences(), pre);
        });
        return pres;
    }

    @Override
    public List<SysPreferences> querySysPreferencesByDb(IRequest requestContext, SysPreferences preferences) {
        return sysPreferencesMapper.select(preferences);
    }

    @Override
    public SysPreferences selectUserPreference(String preference, Long userId) {
        SysPreferences pre = preferenceCache.getValue(userId + "", preference);
        if (pre != null) {
            return pre;
        } else {
            SysPreferences p = new SysPreferences();
            p.setPreferences(preference);
            p.setUserId(userId);
            p = sysPreferencesMapper.selectUserPreference(p);
            if (p != null) {
                preferenceCache.setValue(userId + "", preference, p);
            }
            return p;
        }
    }

}
