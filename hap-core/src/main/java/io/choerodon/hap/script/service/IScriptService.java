package io.choerodon.hap.script.service;

import javax.script.ScriptException;
import java.io.BufferedReader;
import java.io.Reader;
import java.util.Map;

/**
 * @author qiang.zeng@hand-china.com on 2016/12/8.
 */
public interface IScriptService {
    /**
     * 执行脚本文件
     * @param scriptName  脚本名称
     * @param reader    脚本文件
     * @param contextParameter 自定义的参数 String：参数名 Object：java对象
     * @return Object 脚本文件执行结果
     * @throws Exception
     */
     Object execute(String scriptName,Reader reader, Map<String, Object> contextParameter) throws Exception;

    /**
     * 执行字符串脚本
     * @param scriptName 脚本名称
     * @param script    字符串脚本
     * @param contextParameter 自定义的参数 String：参数名 Object：java对象
     * @return Object 字符串脚本执行结果
     * @throws Exception
     */
     Object execute(String scriptName,String script, Map<String, Object> contextParameter) throws Exception;
}
