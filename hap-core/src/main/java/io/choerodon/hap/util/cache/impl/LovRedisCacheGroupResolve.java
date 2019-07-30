package io.choerodon.hap.util.cache.impl;

import io.choerodon.base.helper.ApplicationContextHelper;
import io.choerodon.hap.util.dto.Lov;
import io.choerodon.hap.util.service.ILovService;
import io.choerodon.mybatis.common.query.JoinLov;
import io.choerodon.redis.CacheResolve;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jialong.zuo@hand-china.com
 * @since 2018/1/10
 */
@Component(value = "lovRedisCacheGroupResolve")
public class LovRedisCacheGroupResolve extends CacheResolve {

    private LovCache lovCache;
    private ILovService lovService;

    @Override
    @SuppressWarnings("unchecked")
    public Object resolve(Object cacheEntity, Object resultMap, String lang) throws NoSuchFieldException, IllegalAccessException {

        if (lovCache == null) {
            lovCache = (LovCache) ApplicationContextHelper.getApplicationContext().getBean("lovCache");
        }
        if (lovService == null) {
            lovService = ApplicationContextHelper.getApplicationContext().getBean(ILovService.class);
        }
        JoinLov joinCode = (JoinLov) cacheEntity;
        Field joinKeyField = resultMap.getClass().getDeclaredField(joinCode.joinKey());
        joinKeyField.setAccessible(true);
        Object joinKey = joinKeyField.get(resultMap);

        if (joinKey == null || joinKey == "") {
            return null;
        }


        Field lovCodeField;
        String lovCode;
        if (!joinCode.dynamicLovColumn().isEmpty()) {
            lovCodeField = resultMap.getClass().getDeclaredField(joinCode.dynamicLovColumn());
            lovCodeField.setAccessible(true);
            Object o = lovCodeField.get(resultMap);
            if (o == null) {
                return null;
            }
            lovCode = o.toString();
        } else if (!joinCode.lovCode().isEmpty()) {
            lovCode = joinCode.lovCode();
        } else {
            throw new RuntimeException("dynamicLovColumn 或 lovCode 不能同时为空");

        }


        Lov lov = lovCache.getValue(lovCode);
        Map map = new HashMap();
        map.put(lov.getValueField(), joinKey);
        List<?> list = lovService.selectDatas(lovCode, map, 1, 10);

        if (CollectionUtils.isNotEmpty(list)) {
            Object o1 = list.get(0);
            if (o1 instanceof Map) {
                Map o = (Map) o1;
                return o.get(lov.getTextField());
            } else {
                Field field = o1.getClass().getDeclaredField(lov.getTextField());
                field.setAccessible(true);
                return field.get(o1);
            }
        } else {
            return null;
        }
    }
}
