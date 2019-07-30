package io.choerodon.hap.core.util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

/**
 * freemarker 工具类.
 *
 * @author peng.jiang@hand-china.com
 * @since 2017/9/29.
 */
public class FreemarkerUtil {

    private static Configuration config;

    /**
     * Configuration单例化
     *
     * @return Configuration
     */
    public static Configuration getConfiguration() {
        if (config == null) {
            synchronized (Configuration.class) {
                if (config == null) {
                    config = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
                    init(config);
                }
            }
        }
        return config;
    }

    private static void init(Configuration config) {
        config.setDefaultEncoding("UTF-8");
        config.setNumberFormat("#");
        config.setDateFormat("yyyy-MM-dd");
        config.setTimeFormat("HH:mm:ss");
        config.setDateTimeFormat("yyyy-MM-dd HH:mm:ss");
        // classic compatible,是${abc}允许出现空值的
        config.setClassicCompatible(true);
    }

    /**
     * 转换模板数据[freemarker].
     *
     * @param content 内容
     * @param data    数据
     * @return 转换后数据
     * @throws IOException       IO异常
     * @throws TemplateException 模板转换异常
     */
    public static String translateData(String content, Map data) throws IOException, TemplateException {
        if (content == null) {
            return "";
        }
        Configuration config = getConfiguration();
        StringWriter out = new StringWriter();
        Template template = new Template(null, content, config);
        template.process(data, out);
        return out.toString();
    }

}
