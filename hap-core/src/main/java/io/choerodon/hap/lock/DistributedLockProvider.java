package io.choerodon.hap.lock;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁操作模板
 *
 * @author Qixiangyu on 2017/1/9.
 */
public interface DistributedLockProvider {
    /**
     * 使用分布式锁，使用锁默认超时时间。
     *
     * @param lockKey
     *            确保唯一
     * @param callback
     * @return    回调函数返回的内容
     */
    <T> T lock(String lockKey, DistributedLockCallback<T> callback);

    /**
     * 使用分布式锁，使用锁默认超时时间。
     *
     * @param lockKey
     *            加锁对象，确保有@ID属性，建议BaseDto
     * @param callback
     * @return 回调函数返回的内容
     */
    <T> T lock(Object lockKey, DistributedLockCallback<T> callback);

    /**
     * 使用分布式锁。自定义锁的超时时间
     *
     * @param lockKey
     *            加锁对象，确保有@ID属性，建议BaseDto
     * @param callback
     * @param leaseTime
     *            锁超时时间。超时后自动释放锁。
     * @param timeUnit
     * @return    回调函数返回的内容
     */
    <T> T lock(Object lockKey, DistributedLockCallback<T> callback, long leaseTime, TimeUnit timeUnit);

    /**
     * 使用分布式锁。自定义锁的超时时间
     *
     * @param lockKey
     *            确保唯一
     * @param callback
     * @param leaseTime
     *            锁超时时间。超时后自动释放锁。
     * @param timeUnit
     * @return    回调函数返回的内容
     */
    <T> T lock(String lockKey, DistributedLockCallback<T> callback, long leaseTime, TimeUnit timeUnit);

    /**
     * 使用分布式锁，使用锁默认超时时间。
     *
     * @param lockKey
     *            确保唯一
     * @param callback
     * @return    回调函数返回的内容
     *
     * @exception Exception 加锁过程的异常，比如锁已被占用，加锁失败
     */
    <T> T tryLock(String lockKey, DistributedLockCallback<T> callback) throws Exception;

    /**
     * 使用分布式锁，使用锁默认超时时间。默认等待时间1秒
     *
     * @param lockKey
     *            加锁对象，确保有@ID属性，建议BaseDto
     * @param callback
     * @return    回调函数返回的内容
     *
     * @exception Exception 加锁过程的异常，比如锁已被占用，加锁失败
     */
    <T> T tryLock(Object lockKey, DistributedLockCallback<T> callback) throws Exception;

    /**
     * 使用分布式锁。自定义锁的超时时间
     *
     * @param lockKey
     *            加锁对象，确保有@ID属性，建议BaseDto
     * @param callback
     * @param waitTime
     *            获取锁等待时间，超过设置时间未获得锁则抛出异常
     * @param leaseTime
     *            锁超时时间。超时后自动释放锁。
     * @param timeUnit
     *            waitTime，leaseTime 时间单位
     * @return    回调函数返回的内容
     *
     * @exception Exception 加锁过程的异常，比如锁已被占用，加锁失败
     */
    <T> T tryLock(Object lockKey, DistributedLockCallback<T> callback, long waitTime, long leaseTime, TimeUnit timeUnit)
            throws Exception;

    /**
     * 使用分布式锁。自定义锁的超时时间
     *
     * @param lockKey
     *            确保唯一
     * @param callback
     * @param waitTime
     *            获取锁等待时间，超过设置时间未获得锁则抛出异常
     * @param leaseTime
     *            锁超时时间。超时后自动释放锁。
     * @param timeUnit
     *            waitTime，leaseTime 的时间单位
     * @return    回调函数返回的内容
     *
     * @exception Exception 加锁过程的异常，比如锁已被占用，加锁失败
     */
    <T> T tryLock(String lockKey, DistributedLockCallback<T> callback, long waitTime, long leaseTime, TimeUnit timeUnit)
            throws Exception;
}
