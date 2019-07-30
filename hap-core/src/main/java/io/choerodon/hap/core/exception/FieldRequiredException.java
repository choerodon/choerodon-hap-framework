package io.choerodon.hap.core.exception;

import io.choerodon.base.exception.BaseException;

/**
 * Created by jialong.zuo@hand-china.com on 2016/12/14.
 */
public class FieldRequiredException extends BaseException {

    public FieldRequiredException(String descriptionKey, Object[] parameters) {
        this(null, descriptionKey, parameters);
    }

    /**
     * 不应该直接实例化,应该定义子类.
     *
     * @param code
     *            异常 code,通常与模块 CODE 对应
     * @param descriptionKey
     *            异常消息代码,系统描述中定义
     * @param parameters
     */
    protected FieldRequiredException(String code, String descriptionKey, Object[] parameters) {
        super(code, descriptionKey, parameters);
    }
}
