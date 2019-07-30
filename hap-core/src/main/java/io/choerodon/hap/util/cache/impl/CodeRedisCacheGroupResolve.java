package io.choerodon.hap.util.cache.impl;

import io.choerodon.hap.util.dto.Code;
import io.choerodon.hap.util.dto.CodeValue;
import io.choerodon.mybatis.common.query.JoinCode;
import io.choerodon.redis.CacheResolve;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author jialong.zuo@hand-china.com
 * @since 2017/6/5.
 */
@Component(value = "codeRedisCacheGroupResolve")
public class CodeRedisCacheGroupResolve extends CacheResolve {

    @Autowired
    private SysCodeCache codeCache;

    @Override
    public Object resolve(Object cacheEntity, Object resultMap, String lang) throws NoSuchFieldException, IllegalAccessException {
        Object joinKey = getJoinKey(cacheEntity, resultMap);
        if (joinKey == null) {
            return null;
        }
        JoinCode joinCache = (JoinCode) cacheEntity;
        Code result = codeCache.getValue(lang, joinCache.code());
        if (result == null) {
            throw new RuntimeException("JoinCode failed,result is null!");
        }
        for (CodeValue codeValue : result.getCodeValues()) {
            if (codeValue.getValue().equals(joinKey.toString())) {
                return codeValue.getMeaning();
            }
        }
        return null;
    }


}
