package io.choerodon.hap.lock.exception;

import io.choerodon.base.exception.BaseException;

public class LockException extends BaseException {

    private static final long serialVersionUID = 1L;


    // 获取lockKey 失败，确保Object能拿到唯一ID
    public static final String ERROR_LOCKKEY_FAILURE = "generate lockKey failure";

    // 回调函数执行异常
    public static final String ERROR_CALLBACK_FAILURE = "callback  process error";

    // 资源已被加锁
    public static final String ERROR_GET_LOCKKEY_FAILURE = "tryLock failure";

    // 加锁失败
    public static final String ERROR_LOCK_FAILURE = "lock failure";


    protected LockException(String code, String descriptionKey, Object[] parameters) {
        super(code, descriptionKey, parameters);
    }

    public LockException(String message, String descriptionKey){
        super(message,descriptionKey,null);
    }
}
