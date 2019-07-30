package io.choerodon.hap.cache.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.choerodon.hap.account.dto.User;
import io.choerodon.redis.impl.HashStringRedisCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisCallback;

import java.util.Iterator;
import java.util.Set;

/**
 * 用户缓存.
 * 用户登录时，缓存； 用户信息修改、分配角色时，删除
 *
 * @author lijian.yin@hand-china.com
 * @since 2016/8/1
 */

public class UserCache<T> extends HashStringRedisCache<User> {

    private final Logger logger = LoggerFactory.getLogger(UserCache.class);

    private final Long EXPIRE_TIME = 60 * 12 * 30L;

    /**
     * 项目启动是否清空用户redis缓存
     * 默认不清
     */
    @Value("${sys.userCache.clearCacheOnStartUp:false}")
    private boolean CLEAR_USER_CACHE;

    {
        setLoadOnStartUp(false);
        setType(User.class);
    }

    @Override
    public void init() {
        super.init();
        if (CLEAR_USER_CACHE) {
            this.clearAll();
        }
    }

    @Override
    public User getValue(String key) {
        return getRedisTemplate().execute((RedisCallback<User>) (connection) -> {
            byte[] keyBytes = strSerializer.serialize(getFullKey(null) + ":" + key.toLowerCase());
            byte[] valueBytes = connection.get(keyBytes);
            try {
                if (null != valueBytes)
                    return getObjectMapper().readValue(valueBytes, User.class);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            return null;
        });
    }

    public void setValue(String key, User user) {
        try {
            byte[] keyBytes = strSerializer.serialize(getFullKey(null) + ":" + key.toLowerCase());
            String value = getObjectMapper().writeValueAsString(user);
            getRedisTemplate().execute((RedisCallback<Object>) (connection) -> {
                connection.setEx(keyBytes, EXPIRE_TIME, strSerializer.serialize(value));
                return null;
            });
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void remove(String key) {
        getRedisTemplate().execute((RedisCallback<T>) (connection) -> {
            byte[] keyBytes = strSerializer.serialize(getFullKey(null) + ":" + key.toLowerCase());
            connection.del(keyBytes);
            return (T) null;
        });
    }

    /**
     * 清空所有用户redis缓存
     */
    public void clearAll() {
        getRedisTemplate().execute((RedisCallback<T>) (connection) -> {
            byte[] keyBytes = strSerializer.serialize(getFullKey(null) + ":*");
            Set<byte[]> keys = connection.keys(keyBytes);
            Iterator<byte[]> iterable = keys.iterator();
            while (iterable.hasNext()) {
                connection.del(iterable.next());
            }
            return (T) null;
        });
    }
}
