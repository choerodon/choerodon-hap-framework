package io.choerodon.hap.lock.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.choerodon.hap.lock.exception.LockException;
import io.choerodon.hap.lock.util.LockKeyUtil;
import org.redisson.RedissonRedLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import io.choerodon.hap.lock.DistributedLockCallback;
import io.choerodon.hap.lock.DistributedLockProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Multiple Instance mode 分布式锁模板
 *
 * @author qixiangyu
 */
public class MultipleDistributedLockProvider implements DistributedLockProvider {

    private static final Logger logger = LoggerFactory.getLogger(MultipleDistributedLockProvider.class);
    private static final long DEFAULT_TIMEOUT = 60 * 10;
    private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.SECONDS;
    private static final long DEFAULT_WAIT_TIME = 1;

    private List<RedissonClient> redissons;

    public MultipleDistributedLockProvider() {
    }

    public MultipleDistributedLockProvider(List<RedissonClient> redissons) {
        this.redissons = redissons;
    }

    @Override
    public <T> T lock(String lockKey, DistributedLockCallback<T> callback) {
        return lock(lockKey, callback, DEFAULT_TIMEOUT, DEFAULT_TIME_UNIT);
    }

    @Override
    public <T> T lock(Object lockKey, DistributedLockCallback<T> callback) {
        return lock(LockKeyUtil.getLockKey(lockKey), callback, DEFAULT_TIMEOUT, DEFAULT_TIME_UNIT);
    }

    @Override
    public <T> T lock(Object lockKey, DistributedLockCallback<T> callback, long leaseTime, TimeUnit timeUnit) {
        return lock(LockKeyUtil.getLockKey(lockKey), callback, leaseTime, timeUnit);
    }

    @Override
    public <T> T lock(String lockKey, DistributedLockCallback<T> callback, long leaseTime, TimeUnit timeUnit) {
        RedissonRedLock lock = null;
        try {
            lock = new RedissonRedLock(getRlocks(lockKey));
            lock.lock(leaseTime, timeUnit);
            return callback.process();
        } catch (Exception e) {
            logger.error("callback  process error", e);
            throw new RuntimeException(e);
        } finally {
            if (lock != null) {
                lock.unlock();
            }
        }
    }

    @Override
    public <T> T tryLock(String lockKey, DistributedLockCallback<T> callback) throws Exception {
        return tryLock(lockKey, callback, DEFAULT_WAIT_TIME, DEFAULT_TIMEOUT, DEFAULT_TIME_UNIT);
    }

    @Override
    public <T> T tryLock(Object lockKey, DistributedLockCallback<T> callback) throws Exception {
        return tryLock(LockKeyUtil.getLockKey(lockKey), callback, DEFAULT_WAIT_TIME, DEFAULT_TIMEOUT,
                DEFAULT_TIME_UNIT);
    }

    @Override
    public <T> T tryLock(Object lockKey, DistributedLockCallback<T> callback, long waitTime, long leaseTime,
            TimeUnit timeUnit) throws Exception {
        return tryLock(LockKeyUtil.getLockKey(lockKey), callback, waitTime, leaseTime, timeUnit);
    }

    @Override
    public <T> T tryLock(String lockKey, DistributedLockCallback<T> callback, long waitTime, long leaseTime,
            TimeUnit timeUnit) throws Exception {
        RedissonRedLock lock = null;
        try {
            lock = new RedissonRedLock(getRlocks(lockKey));
            if (lock.tryLock(waitTime, leaseTime, timeUnit)) {
                return callback.process();
            } else {
                throw new LockException(LockException.ERROR_GET_LOCKKEY_FAILURE,null);
            }
        } finally {
            if (lock != null) {
                lock.unlock();
            }
        }
    }

    public void setRedisson(List<RedissonClient> redissons) {
        this.redissons = redissons;
    }

    private RLock[] getRlocks(String lockKey) {
        RLock[] rLock = new RLock[redissons.size()];
        for (int i = 0; i < redissons.size(); i++) {
            RedissonClient redisson = redissons.get(i);
            rLock[i] = redisson.getLock(lockKey);
        }
        return rLock;
    }

}
