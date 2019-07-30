package io.choerodon.hap.excel;

/**
 * @author zjl
 * @since 2017/1/22
 */
public class ExcelException extends Exception {
    /**
     * 不应该直接实例化,应该定义子类.
     *
     * @param code           异常 code,通常与模块 CODE 对应
     * @param descriptionKey 异常消息代码,系统描述中定义
     * @param parameters     参数数组
     */
    public ExcelException(String code, String descriptionKey, Object[] parameters) {
        super();
    }

    /**
     * 不应该直接实例化,应该定义子类.
     *
     * @param descriptionKey 异常消息代码,系统描述中定义
     */
    public ExcelException(String descriptionKey) {
        this(null, descriptionKey, null);
    }
}