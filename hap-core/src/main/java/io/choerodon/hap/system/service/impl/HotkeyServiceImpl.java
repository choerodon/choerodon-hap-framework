package io.choerodon.hap.system.service.impl;

import io.choerodon.hap.cache.impl.HotkeyCache;
import io.choerodon.hap.core.annotation.StdWho;
import io.choerodon.hap.system.dto.Hotkey;
import io.choerodon.hap.system.service.IHotkeyService;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.system.dto.DTOStatus;
import io.choerodon.mybatis.service.BaseServiceImpl;
import io.choerodon.web.core.impl.RequestHelper;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
@Transactional(rollbackFor = Exception.class)
@Dataset("Hotkey")
public class HotkeyServiceImpl extends BaseServiceImpl<Hotkey> implements IHotkeyService, IDatasetService<Hotkey> {

    @Autowired
    private HotkeyCache hotkeyCache;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Hotkey insertSelective(@StdWho Hotkey hotkey) {
        if (hotkey == null) {
            return null;
        }
        Hotkey hotkey1 = super.insertSelective(hotkey);
        hotkeyCache.load(hotkey1.getHotkeyLevel() + "_" + hotkey1.getHotkeyLevelId());
        return hotkey1;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Hotkey updateByPrimaryKeySelective(@StdWho Hotkey hotkey) {
        if (hotkey == null) {
            return null;
        }
        Hotkey result = super.updateByPrimaryKeySelective(hotkey);
        hotkeyCache.load(result.getHotkeyLevel() + "_" + result.getHotkeyLevelId());
        return result;
    }

    @Override
    public int batchDelete(List<Hotkey> hotkeys) {
        int c = 0;
        String key = hotkeys.get(0).getHotkeyLevel() + "_" + hotkeys.get(0).getHotkeyLevelId();
        for (Hotkey hotkey : hotkeys) {
            c += self().deleteByPrimaryKey(hotkey);
        }
        hotkeyCache.load(key);
        return c;
    }


    @Override
    public List<Hotkey> preferenceQuery() {
        Hotkey hotkeyS = new Hotkey();
        hotkeyS.setHotkeyLevel("system");
        hotkeyS.setHotkeyLevelId((long) 0);
        List<Hotkey> hotkeySys = super.selectOptions(hotkeyS, null);
        Hotkey hotkeyU = new Hotkey();
        hotkeyU.setHotkeyLevel("user");
        hotkeyU.setHotkeyLevelId(RequestHelper.getCurrentRequest().getUserId());
        List<Hotkey> hotkeyUser = super.selectOptions(hotkeyU, null);
        Map<String, Hotkey> hotkeys = new TreeMap<>();
        if (hotkeySys != null) {
            for (Hotkey hotkey : hotkeySys) {
                hotkeys.put(hotkey.getCode(), hotkey);
            }
        }
        if (hotkeyUser != null) {
            for (Hotkey hotkey : hotkeyUser) {
                Hotkey value = hotkeys.get(hotkey.getCode());
                if (value != null) {
                    hotkey.setDescription(value.getDescription());
                    hotkeys.put(hotkey.getCode(), hotkey);
                }
            }
        }
        List<Hotkey> datas = new ArrayList<>();
        datas.addAll(hotkeys.values());
        return datas;
    }

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            Hotkey hotkey = new Hotkey();
            BeanUtils.populate(hotkey, body);
            hotkey.setHotkeyLevel("system");
            hotkey.setHotkeyLevelId((long) 0);
            return self().selectOptions(hotkey, null);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new DatasetException("dataset.error", e);
        }


    }

    @Override
    public List<Hotkey> mutations(List<Hotkey> objs) {
        for (Hotkey hotkey : objs) {
            switch (hotkey.get__status()) {
                case DTOStatus.ADD: {
                    self().insertSelective(hotkey);
                    break;
                }
                case DTOStatus.UPDATE: {
                    self().updateByPrimaryKeySelective(hotkey);
                    break;
                }
                case DTOStatus.DELETE: {
                    String key = hotkey.getHotkeyLevel() + "_" + hotkey.getHotkeyLevelId();
                    self().deleteByPrimaryKey(hotkey);
                    hotkeyCache.load(key);
                    break;
                }
                default:
                    break;
            }
        }
        return objs;
    }
}