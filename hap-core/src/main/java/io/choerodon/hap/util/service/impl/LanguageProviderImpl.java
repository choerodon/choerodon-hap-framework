package io.choerodon.hap.util.service.impl;


import io.choerodon.hap.util.dto.Language;
import io.choerodon.hap.util.service.ILanguageProvider;
import io.choerodon.message.IMessageConsumer;
import io.choerodon.message.annotation.TopicMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;

/**
 * @author shengyang.zhou@hand-china.com
 */
@Component
@TopicMonitor(channel = {LanguageProviderImpl.CACHE_LANGUAGE, LanguageProviderImpl.TOPIC_CACHE_RELOADED})
public class LanguageProviderImpl implements ILanguageProvider, InitializingBean, IMessageConsumer<String> {

    public static final String CACHE_LANGUAGE = "cache.language";

    public static final String TOPIC_CACHE_RELOADED = "topic:cache:reloaded";

    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;

    private List<Language> languages = Collections.emptyList();

    private Logger logger = LoggerFactory.getLogger(LanguageProviderImpl.class);

    @Override
    public List<Language> getSupportedLanguages() {
        if (languages.isEmpty()) {
            reload();
        }
        return languages;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        reload();
    }

    private void reload() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.afterPropertiesSet();
        RowMapper<Language> rowMapper = new BeanPropertyRowMapper<>(Language.class);

        String selectSql = "SELECT LANG_CODE, BASE_LANG, DESCRIPTION FROM SYS_LANG_B";
        languages = jdbcTemplate.query(selectSql, rowMapper);
    }

    @Override
    public void onMessage(String message, String pattern) {
        if (CACHE_LANGUAGE.equals(pattern)) {
            if (logger.isDebugEnabled()) {
                logger.debug("language changed, now reload cache.", message);
            }
            reload();
        } else if (TOPIC_CACHE_RELOADED.equals(pattern) && "language".equals(message)) {
            if (logger.isDebugEnabled()) {
                logger.debug("language reloaded, now reload cache.");
            }
            reload();
        }
    }
}
