package io.choerodon.hap.task.components;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.filter.ThresholdFilter;
import io.choerodon.hap.core.AppContextInitListener;
import io.choerodon.hap.task.log.TaskAppender;
import io.choerodon.hap.task.log.TaskLogFilter;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * 日志动态配置.
 *
 * @author peng.jiang@hand-china.com
 * @since 2017/11/20.
 **/

@Component
public class LogbackDynamicConfig implements AppContextInitListener {

    @Value("${task.execute.log.level:INFO}")
    private String logLevel;

    @Override
    public void contextInitialized(ApplicationContext applicationContext) {
        try {
            addAppender();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void addAppender() throws IOException {

        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = lc.getLogger("root");

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setCharset(Charset.forName("UTF-8"));
        encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%X{userId}] [%X{requestId}] %logger - %msg%n");
        encoder.setImmediateFlush(true);
        encoder.setContext(lc);
        encoder.start();

        TaskLogFilter taskLogFilter = new TaskLogFilter();
        taskLogFilter.start();

        ThresholdFilter thresholdFilter = new ThresholdFilter();
        thresholdFilter.setLevel(logLevel);
        thresholdFilter.start();


        TaskAppender appender = new TaskAppender();
        appender.setEncoder(encoder);
        appender.setName("TASK");
        appender.addFilter(taskLogFilter);
        appender.addFilter(thresholdFilter);
        appender.setContext(lc);
        appender.start();

        logger.addAppender(appender);
    }

}
