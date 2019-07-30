package io.choerodon.hap.lock;

/**
 * 分布式锁回调接口
 *
 * @author Qixiangyu on 2017/1/9.
 */
public interface DistributedLockCallback<T> {

    /**
     * 调用者必须在此方法中实现需要加分布式锁的业务逻辑
     *
     * @return
     */
     T process() throws Exception;

}
