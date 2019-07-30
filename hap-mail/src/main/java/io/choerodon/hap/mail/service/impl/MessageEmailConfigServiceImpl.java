package io.choerodon.hap.mail.service.impl;

import com.github.pagehelper.PageHelper;
import io.choerodon.hap.cache.impl.MessageEmailConfigCache;
import io.choerodon.hap.core.exception.EmailException;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.mail.dto.MessageEmailAccount;
import io.choerodon.hap.mail.dto.MessageEmailConfig;
import io.choerodon.hap.mail.dto.MessageEmailProperty;
import io.choerodon.hap.mail.dto.MessageEmailWhiteList;
import io.choerodon.hap.mail.mapper.MessageEmailAccountMapper;
import io.choerodon.hap.mail.mapper.MessageEmailConfigMapper;
import io.choerodon.hap.mail.mapper.MessageEmailPropertyMapper;
import io.choerodon.hap.mail.mapper.MessageEmailWhiteListMapper;
import io.choerodon.hap.mail.service.IMessageEmailConfigService;
import io.choerodon.hap.security.service.IAESClientService;
import io.choerodon.hap.system.dto.DTOStatus;
import io.choerodon.mybatis.service.BaseServiceImpl;
import io.choerodon.base.util.BaseConstants;
import io.choerodon.web.core.IRequest;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * 邮件配置服务接口实现.
 *
 * @author qiang.zeng@hand-china.com
 */
@Service
@Dataset("EmailConfig")
public class MessageEmailConfigServiceImpl extends BaseServiceImpl<MessageEmailConfig> implements IMessageEmailConfigService, BeanFactoryAware, IDatasetService<MessageEmailConfig> {

    /**
     * 选择白名单没有设置名单
     */
    public static final String MSG_MESSAGE_NO_WHITE_LIST = "msg.warning.system.email_message_no_whitelist";


    /**
     * 账号列表为空
     */
    public static final String MSG_MESSAGE_NO_ACCOUNT_LIST = "msg.warning.system.email_message_on_accountlist";

    private BeanFactory beanFactory;

    @Autowired
    private MessageEmailConfigMapper mapper;
    @Autowired
    private MessageEmailAccountMapper accountMapper;
    @Autowired
    private MessageEmailWhiteListMapper addressMapper;
    @Autowired
    private MessageEmailPropertyMapper messageEmailPropertyMapper;
    @Autowired
    private IAESClientService aceClientService;
    @Autowired
    private MessageEmailConfigCache configCache;

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageEmailConfig createMessageEmailConfig(IRequest request, MessageEmailConfig obj) {
        if (obj == null) {
            return null;
        }
        obj.setPassword(aceClientService.encrypt(obj.getPassword()));
        mapper.insertSelective(obj);
        return obj;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageEmailConfig updateMessageEmailConfig(IRequest request, MessageEmailConfig obj) {
        if (obj == null) {
            return null;
        }
        MessageEmailConfig config = mapper.selectByPrimaryKey(obj.getConfigId());
        if (config != null) {
            if (config.getPassword() != null && config.getPassword().equals(obj.getPassword())) {
                // 没有修改密码
                obj.setPassword(null);
            } else {
                // aes加密
                obj.setPassword(aceClientService.encrypt(obj.getPassword()));
            }
        }
        int updateCount = mapper.updateByPrimaryKeySelective(obj);
        checkOvn(updateCount, obj);
        return obj;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public MessageEmailConfig selectMessageEmailConfigById(IRequest request, Long objId) {
        if (objId == null) {
            return null;
        }
        return mapper.selectByPrimaryKey(objId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteMessageEmailConfig(IRequest request, MessageEmailConfig obj) {
        if (obj.getConfigId() == null) {
            return 0;
        }
        accountMapper.deleteByConfigId(obj.getConfigId());
        addressMapper.deleteByConfigId(obj.getConfigId());
        messageEmailPropertyMapper.deleteByConfigId(obj.getConfigId());
        int updateCount = mapper.deleteByPrimaryKey(obj);
        checkOvn(updateCount, obj);
        configCache.remove(obj.getConfigId());
        return updateCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDelete(IRequest request, List<MessageEmailConfig> objs) {
        int result = 0;
        if (CollectionUtils.isEmpty(objs)) {
            return result;
        }

        for (MessageEmailConfig obj : objs) {
            self().deleteMessageEmailConfig(request, obj);
            result++;
        }
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<MessageEmailConfig> selectMessageEmailConfigs(IRequest request, MessageEmailConfig example, int page,
                                                              int pageSize) {
        PageHelper.startPage(page, pageSize);
        return mapper.selectMessageEmailConfigs(example);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdate(IRequest requestContext, MessageEmailConfig obj) throws EmailException {
        if (obj == null) {
            return;
        }
        if (CollectionUtils.isEmpty(obj.getEmailAccounts())) {
            // 账号列表为空
            throw new EmailException(MSG_MESSAGE_NO_ACCOUNT_LIST);
        }
        if (BaseConstants.YES.equalsIgnoreCase(obj.getUseWhiteList()) && (CollectionUtils.isEmpty(obj.getWhiteLists()))) {
            // 选择白名单没有设置名单
            throw new EmailException(MSG_MESSAGE_NO_WHITE_LIST);
        }
        if (obj.getConfigId() == null) {
            createMessageEmailConfig(requestContext, obj);
        } else {
            updateMessageEmailConfig(requestContext, obj);
        }

        if (obj.getEmailAccounts() != null) {
            for (MessageEmailAccount current : obj.getEmailAccounts()) {
                if (current.getAccountId() == null) {
                    current.setObjectVersionNumber(0L);
                    current.setConfigId(obj.getConfigId());
                    createEmailAccount(requestContext, current);
                } else {
                    updateEmailAccount(requestContext, current);
                }
            }
        }
        if (obj.getWhiteLists() != null) {
            for (MessageEmailWhiteList current : obj.getWhiteLists()) {
                if (current.getId() == null) {
                    current.setObjectVersionNumber(0L);
                    current.setConfigId(obj.getConfigId());
                    createAddress(requestContext, current);
                } else {
                    updateAddress(requestContext, current);
                }
            }
        }
        if (obj.getPropertyLists() != null) {
            for (MessageEmailProperty current : obj.getPropertyLists()) {
                if (current.getPropertyId() == null) {
                    current.setObjectVersionNumber(0L);
                    current.setConfigId(obj.getConfigId());
                    createProperty(requestContext, current);
                } else {
                    updateProperty(requestContext, current);
                }
            }
        }
        configCache.reload(obj.getConfigId());
    }

    private MessageEmailAccount createEmailAccount(IRequest request, MessageEmailAccount obj) {
        if (obj == null) {
            return null;
        }
        accountMapper.insertSelective(obj);
        return obj;
    }

    private MessageEmailWhiteList createAddress(IRequest request, MessageEmailWhiteList obj) {
        if (obj == null) {
            return null;
        }
        addressMapper.insertSelective(obj);
        return obj;
    }

    private MessageEmailAccount updateEmailAccount(IRequest request, MessageEmailAccount obj) {
        if (obj == null) {
            return null;
        }
        int updateCount = accountMapper.updateByPrimaryKeySelective(obj);
        checkOvn(updateCount, obj);
        return obj;
    }

    private MessageEmailWhiteList updateAddress(IRequest request, MessageEmailWhiteList obj) {
        if (obj == null) {
            return null;
        }
        int updateCount = addressMapper.updateByPrimaryKeySelective(obj);
        checkOvn(updateCount, obj);
        return obj;
    }

    private MessageEmailProperty createProperty(IRequest request, MessageEmailProperty obj) {
        if (obj == null) {
            return null;
        }
        messageEmailPropertyMapper.insertSelective(obj);
        return obj;
    }

    private MessageEmailProperty updateProperty(IRequest request, MessageEmailProperty obj) {
        if (obj == null) {
            return null;
        }
        int updateCount = messageEmailPropertyMapper.updateByPrimaryKeySelective(obj);
        checkOvn(updateCount, obj);
        return obj;
    }

    private int deleteEmailAccount(IRequest request, MessageEmailAccount obj) {
        if (obj.getAccountId() == null) {
            return 0;
        }
        int updateCount = accountMapper.deleteByPrimaryKey(obj);
        checkOvn(updateCount, obj);
        return updateCount;
    }

    private int deletetAddress(IRequest request, MessageEmailWhiteList obj) {
        if (obj.getId() == null) {
            return 0;
        }
        int updateCount = addressMapper.deleteByPrimaryKey(obj);
        checkOvn(updateCount, obj);
        return updateCount;
    }

    private int deleteProperty(IRequest request, MessageEmailProperty obj) {
        if (obj.getPropertyId() == null) {
            return 0;
        }
        int result = messageEmailPropertyMapper.deleteByPrimaryKey(obj);
        checkOvn(result, obj);
        return result;
    }

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            MessageEmailConfig emailConfig = new MessageEmailConfig();
            BeanUtils.populate(emailConfig, body);
            return self().selectMessageEmailConfigs(null, emailConfig, page, pageSize);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new DatasetException("dataset.error.MessageEmailConfig", e);
        }
    }

    @Override
    public List<MessageEmailConfig> mutations(List<MessageEmailConfig> objs) {
        for (MessageEmailConfig emailConfig : objs) {
            try {
                switch (emailConfig.get__status()) {
                    case DTOStatus.ADD:
                        saveEmailConfig(null, emailConfig);
                        break;
                    case DTOStatus.UPDATE:
                        saveEmailConfig(null, emailConfig);
                        break;
                    case DTOStatus.DELETE:
                        self().deleteMessageEmailConfig(null, emailConfig);
                        break;
                    default:
                        break;
                }
            } catch (EmailException e) {
                throw new DatasetException("dataset.error", e);
            }
        }
        return objs;
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveEmailConfig(IRequest request, MessageEmailConfig obj) throws EmailException {
        if (obj != null) {
            if (obj.getConfigId() == null) {
                createMessageEmailConfig(request, obj);
            } else {
                updateMessageEmailConfig(request, obj);
            }

            if (obj.getEmailAccounts() != null) {
                processEmailAccount(obj);
            }
            if (obj.getWhiteLists() != null) {
                processEmailAddress(obj);
            }
            if (obj.getPropertyLists() != null) {
                processEmailProperty(obj);
            }
        }
        configCache.reload(obj.getConfigId());
    }


    @Transactional(rollbackFor = Exception.class)
    public void processEmailAccount(MessageEmailConfig emailConfig) {
        for (MessageEmailAccount emailAccount : emailConfig.getEmailAccounts()) {
            switch (emailAccount.get__status()) {
                case DTOStatus.ADD:
                    emailAccount.setObjectVersionNumber(1L);
                    emailAccount.setConfigId(emailConfig.getConfigId());
                    createEmailAccount(null, emailAccount);
                    break;
                case DTOStatus.DELETE:
                    deleteEmailAccount(null, emailAccount);
                    break;
                case DTOStatus.UPDATE:
                    updateEmailAccount(null, emailAccount);
                    break;
                default:
                    break;
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void processEmailAddress(MessageEmailConfig emailConfig) {
        for (MessageEmailWhiteList emailWhiteList : emailConfig.getWhiteLists()) {
            switch (emailWhiteList.get__status()) {
                case DTOStatus.ADD:
                    emailWhiteList.setObjectVersionNumber(1L);
                    emailWhiteList.setConfigId(emailConfig.getConfigId());
                    createAddress(null, emailWhiteList);
                    break;
                case DTOStatus.DELETE:
                    deletetAddress(null, emailWhiteList);
                    break;
                case DTOStatus.UPDATE:
                    updateAddress(null, emailWhiteList);
                    break;
                default:
                    break;
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void processEmailProperty(MessageEmailConfig emailConfig) {
        for (MessageEmailProperty emailProperty : emailConfig.getPropertyLists()) {
            switch (emailProperty.get__status()) {
                case DTOStatus.ADD:
                    emailProperty.setObjectVersionNumber(1L);
                    emailProperty.setConfigId(emailConfig.getConfigId());
                    createProperty(null, emailProperty);
                    break;
                case DTOStatus.DELETE:
                    deleteProperty(null, emailProperty);
                    break;
                case DTOStatus.UPDATE:
                    updateProperty(null, emailProperty);
                    break;
                default:
                    break;
            }
        }
    }
}
