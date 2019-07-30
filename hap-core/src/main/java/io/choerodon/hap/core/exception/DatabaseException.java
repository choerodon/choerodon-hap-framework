/*
 * Copyright Hand China Co.,Ltd.
 */

package io.choerodon.hap.core.exception;

import io.choerodon.base.exception.BaseException;

/**
 * @author shengyang.zhou@hand-china.com
 */
public class DatabaseException extends BaseException {
    /**
     * 不应该直接实例化,应该定义子类.
     *
     * @param code
     *            异常 code,通常与模块 CODE 对应
     * @param descriptionKey
     *            异常消息代码,系统描述中定义
     * @param parameters
     */
    protected DatabaseException(String code, String descriptionKey, Object[] parameters) {
        super(code, descriptionKey, parameters);
    }

    public DatabaseException(String errorCode, String message) {
        this(errorCode, message, null);
    }
}
