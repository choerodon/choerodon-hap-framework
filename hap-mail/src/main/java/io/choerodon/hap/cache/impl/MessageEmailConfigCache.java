package io.choerodon.hap.cache.impl;

import io.choerodon.hap.mail.dto.MessageEmailAccount;
import io.choerodon.hap.mail.dto.MessageEmailConfig;
import io.choerodon.hap.mail.dto.MessageEmailProperty;
import io.choerodon.hap.mail.dto.MessageEmailWhiteList;
import io.choerodon.hap.mail.mapper.MessageEmailAccountMapper;
import io.choerodon.hap.mail.mapper.MessageEmailConfigMapper;
import io.choerodon.hap.mail.mapper.MessageEmailPropertyMapper;
import io.choerodon.hap.mail.mapper.MessageEmailWhiteListMapper;
import io.choerodon.redis.impl.HashStringRedisCache;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qiang.zeng@hand-china.com
 */
@Component
public class MessageEmailConfigCache extends HashStringRedisCache<MessageEmailConfig> {

    private final Logger logger = LoggerFactory.getLogger(MessageEmailConfigCache.class);

    private String messageEmailConfigQueryAllSqlId = MessageEmailConfigMapper.class.getName() + ".selectAll";
    private String messageEmailAccountQueryAllSqlId = MessageEmailAccountMapper.class.getName() + ".selectAll";
    private String messageEmailWhiteListQueryAllSqlId = MessageEmailWhiteListMapper.class.getName() + ".selectAll";
    private String messageEmailPropertyQueryAllSqlId = MessageEmailPropertyMapper.class.getName() + ".selectAll";
    private String messageEmailAccountQuerySqlId = MessageEmailAccountMapper.class.getName() + ".selectByConfigId";
    private String messageEmailWhiteListQuerySqlId = MessageEmailWhiteListMapper.class.getName() + ".selectByConfigId";
    private String messageEmailPropertyQuerySqlId = MessageEmailPropertyMapper.class.getName() + ".selectByConfigId";

    {
        setLoadOnStartUp(true);
        setType(MessageEmailConfig.class);
        setName("message_email_config");
    }

    @Override
    public MessageEmailConfig getValue(String key) {
        return super.getValue(key);
    }

    @Override
    public void setValue(String key, MessageEmailConfig value) {
        super.setValue(key, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void initLoad() {
        Map<Long, MessageEmailConfig> messageEmailConfigMap = new HashMap<>();
        try (SqlSession sqlSession = getSqlSessionFactory().openSession()) {
            sqlSession.select(messageEmailConfigQueryAllSqlId, (resultContext) -> {
                MessageEmailConfig messageEmailConfig = (MessageEmailConfig) resultContext.getResultObject();
                messageEmailConfigMap.put(messageEmailConfig.getConfigId(), messageEmailConfig);
            });
            sqlSession.select(messageEmailAccountQueryAllSqlId, (resultContext) -> {
                MessageEmailAccount account = (MessageEmailAccount) resultContext.getResultObject();
                MessageEmailConfig config = messageEmailConfigMap.get(account.getConfigId());
                if (config != null) {
                    List<MessageEmailAccount> accounts = config.getEmailAccounts();
                    if (accounts == null) {
                        accounts = new ArrayList<>();
                        config.setEmailAccounts(accounts);
                    }
                    accounts.add(account);
                }
            });
            sqlSession.select(messageEmailWhiteListQueryAllSqlId, (resultContext) -> {
                MessageEmailWhiteList whiteList = (MessageEmailWhiteList) resultContext.getResultObject();
                MessageEmailConfig config = messageEmailConfigMap.get(whiteList.getConfigId());
                if (config != null) {
                    List<MessageEmailWhiteList> whiteLists = config.getWhiteLists();
                    if (whiteLists == null) {
                        whiteLists = new ArrayList<>();
                        config.setWhiteLists(whiteLists);
                    }
                    whiteLists.add(whiteList);
                }
            });
            sqlSession.select(messageEmailPropertyQueryAllSqlId, (resultContext) -> {
                MessageEmailProperty property = (MessageEmailProperty) resultContext.getResultObject();
                MessageEmailConfig config = messageEmailConfigMap.get(property.getConfigId());
                if (config != null) {
                    List<MessageEmailProperty> propertyLists = config.getPropertyLists();
                    if (propertyLists == null) {
                        propertyLists = new ArrayList<>();
                        config.setPropertyLists(propertyLists);
                    }
                    propertyLists.add(property);
                }
            });
            messageEmailConfigMap.forEach((k, v) -> {
                setValue(k.toString(), v);
            });
            if (logger.isDebugEnabled()) {
                logger.debug("successfully loaded all message email config cache");
            }
        } catch (Throwable e) {
            if (logger.isErrorEnabled()) {
                logger.error("init message email config cache exception: ", e);
            }
        }
    }

    public void reload(Long configId) {
        super.remove(configId.toString());
        try (SqlSession sqlSession = getSqlSessionFactory().openSession()) {
            MessageEmailConfig config = sqlSession.selectOne(MessageEmailConfigMapper.class.getName() + ".selectByPrimaryKey", configId);
            List<MessageEmailAccount> accounts = sqlSession.selectList(messageEmailAccountQuerySqlId, configId);
            List<MessageEmailWhiteList> whiteLists = sqlSession.selectList(messageEmailWhiteListQuerySqlId, configId);
            List<MessageEmailProperty> properties = sqlSession.selectList(messageEmailPropertyQuerySqlId, configId);
            config.setEmailAccounts(accounts);
            config.setWhiteLists(whiteLists);
            config.setPropertyLists(properties);
            setValue(config.getConfigId().toString(), config);
        } catch (Throwable e) {
            if (logger.isErrorEnabled()) {
                logger.error("load message email config cache exception: ", e);
            }
        }
    }

    public void remove(Long configId) {
        super.remove(configId.toString());
    }
}
