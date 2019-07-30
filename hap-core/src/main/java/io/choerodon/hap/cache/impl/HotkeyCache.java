package io.choerodon.hap.cache.impl;

import io.choerodon.hap.system.dto.Hotkey;
import io.choerodon.hap.system.mapper.HotkeyMapper;
import io.choerodon.redis.impl.HashStringRedisCache;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author zhizheng.yang@hand-china.com
 * @since 2017/10/17.
 */
@Component(value = "hotkeyCache")
public class HotkeyCache extends HashStringRedisCache<Hotkey[]> {

    private Logger logger = LoggerFactory.getLogger(HotkeyCache.class);

    private String selectAll = HotkeyMapper.class.getName() + ".queryAll";
    private String selectByKey = HotkeyMapper.class.getName() + ".select";

    {
        setLoadOnStartUp(true);
        setType(Hotkey[].class);
        setName("hotkey");
    }


    @Override
    public Hotkey[] getValue(String key) {
        return super.getValue(key);
    }

    @Override
    public void setValue(String key, Hotkey[] values) {
        super.setValue(key, values);
    }


    public void load(String key) {
        super.remove(key);
        Map<String, List<Hotkey>> hotkeys = new HashMap<>();
        try (SqlSession sqlSession = getSqlSessionFactory().openSession()) {
            sqlSession.select(selectByKey, setHotkey(key), (resultContext) -> {
                Hotkey value = (Hotkey) resultContext.getResultObject();
                List<Hotkey> sets = hotkeys.computeIfAbsent(key, k -> new ArrayList<>());
                sets.add(value);
            });

            hotkeys.forEach((k, v) -> {
                setValue(k, v.toArray(new Hotkey[v.size()]));
            });
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("load hotkey cache exception: ", e);
            }
        }
    }

    @Override
    protected void initLoad() {
        Map<String, List<Hotkey>> hotkeys = new HashMap<>();
        try (SqlSession sqlSession = getSqlSessionFactory().openSession()) {
            sqlSession.select(selectAll, (resultContext) -> {
                Hotkey value = (Hotkey) resultContext.getResultObject();
                String key = value.getHotkeyLevel() + "_" + value.getHotkeyLevelId().toString();
                List<Hotkey> items = hotkeys.computeIfAbsent(key, k -> new ArrayList<>());
                items.add(value);
            });

            hotkeys.forEach((k, v) -> {
                setValue(k, v.toArray(new Hotkey[v.size()]));
            });
            if (logger.isDebugEnabled()) {
                logger.debug("successfully loaded all hotkey cache");
            }
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("init hotkey cache exception: ", e);
            }
        }
    }

    private Hotkey setHotkey(String key) {
        Hotkey hotkey = new Hotkey();
        if (key != null && key.contains("_")) {
            hotkey.setHotkeyLevelId(Long.parseLong(StringUtils.substringAfterLast(key, "_")));
            hotkey.setHotkeyLevel(StringUtils.substringBeforeLast(key, "_"));
        }
        return hotkey;
    }


}
