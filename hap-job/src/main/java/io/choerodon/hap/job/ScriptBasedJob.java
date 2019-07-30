package io.choerodon.hap.job;

import io.choerodon.hap.script.service.IScriptService;
import org.apache.commons.io.IOUtils;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;


/**
 * 处理Script的job
 * Author:qiang.zeng@hand-china.com on 2016/12/13.
 */
public class ScriptBasedJob extends AbstractJob {

    private Logger logger = LoggerFactory.getLogger(ScriptBasedJob.class);
    @Autowired
    private IScriptService scriptService;

    private Exception exception = null;
    /**
     * 初始化文件路径格式
     *
     * @param scriptFile 文件路径
     * @return String 文件路径
     */
    public String initFileFormat(String scriptFile) {
        String sf = scriptFile.trim().replace('\\', '/');
        if (!sf.startsWith("/")) {
            sf = "/" + sf;
        }
        return sf;
    }

    @Override
    public void safeExecute(JobExecutionContext context) throws Exception {
        Object ret = null;

        try {
            String scriptName = context.getMergedJobDataMap().getString("scriptName");
            String script = context.getMergedJobDataMap().getString("script");
            String scriptFile = context.getMergedJobDataMap().getString("scriptFile");
            //脚本执行优先级 script>scriptFile
            if (script != null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("begin execute script,scriptName:{}  Script:{}", scriptName, script);
                }
                ret = scriptService.execute(scriptName, script, null);
            } else if (scriptFile != null) {
                //获取脚本文件流
                InputStream inputStream = ScriptBasedJob.class.getResourceAsStream(initFileFormat(scriptFile));
                if(inputStream!=null){
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    IOUtils.copyLarge(inputStream, baos);
                    IOUtils.closeQuietly(baos);
                    byte[] bytes = baos.toByteArray();
                    String scriptContent = new String(bytes, "UTF-8");
                    IOUtils.closeQuietly(inputStream);
                    ret = scriptService.execute(scriptName, scriptContent, null);
                }else{
                    throw new Exception("scriptFile is blank");
                }
            } else {
                throw new Exception("both script and scriptFile is blank");
            }
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
            }
            exception = e;
            throw e;
        } finally {
            if (exception != null) {
                setExecutionSummary(exception.getClass().getName() + ":" + exception.getMessage());
            } else {
                if (ret != null) {
                    setExecutionSummary("execution result:" + ret);
                }
            }
        }
    }
    @Override
    public boolean isRefireImmediatelyWhenException() {
        return false;
    }

}
