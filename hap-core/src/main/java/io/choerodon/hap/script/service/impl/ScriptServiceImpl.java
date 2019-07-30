package io.choerodon.hap.script.service.impl;

import io.choerodon.hap.script.service.IScriptService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.Reader;
import java.util.Map;

/**
 * 在java中执行js文件的服务接口实现
 * @author qiang.zeng@hand-china.com on 2016/12/8.
 */
@Service
public class ScriptServiceImpl implements IScriptService {
    @Autowired
    private ApplicationContext applicationContext;

    private final Logger logger = LoggerFactory.getLogger(ScriptServiceImpl.class);

    /**
     * 初始化脚本引擎，默认参数以及自定义参数
     *
     * @param contextParameter 自定义参数
     * @return 脚本引擎
     */
    private ScriptEngine initScriptEngine(Map<String, Object> contextParameter) {
        ScriptEngineManager sem = new ScriptEngineManager();
        ScriptEngine engine = sem.getEngineByName("JavaScript");
        engine.put("applicationContext", applicationContext);
        engine.put("out", System.out);
        engine.put("logger", logger);
        if (contextParameter != null) {
            contextParameter.forEach((k, v) -> engine.put(k, v));
        }
        return engine;
    }
    @Override
    public Object execute(String scriptName, Reader reader, Map<String, Object> contextParameter) throws Exception {
        if (null == reader) {
            throw new Exception("reader is blank");
        }
        StringBuilder stringBuilder = new StringBuilder();
        char[] buff = new char[1024];
        int len = -1;
        while ((len = IOUtils.read(reader, buff)) != -1 && len!=0) {
            stringBuilder.append(buff, 0, len);
        }
        IOUtils.closeQuietly(reader);
        Object result = execute(scriptName, stringBuilder.toString(), contextParameter);
        return result;
    }

    @Override
    public Object execute(String scriptName, String script, Map<String, Object> contextParameter) throws Exception {
        if (StringUtils.isBlank(script)) {
            throw new Exception("script is blank");
        }
        Object result = null;
        long starTime = System.currentTimeMillis();
        ScriptException scriptException = null;
        try {
            result = initScriptEngine(contextParameter).eval(script);
        } catch (ScriptException e) {
            scriptException = e;
            throw e;
        } finally {
            long endTime = System.currentTimeMillis();
            if (logger.isDebugEnabled()) {
                if (null == scriptException) {
                    logger.debug("The script {} running time: {} ms", scriptName, endTime - starTime);
                }
            }
        }
        return result;
    }
}
