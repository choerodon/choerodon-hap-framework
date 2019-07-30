package io.choerodon.hap.mail.service.impl;

import com.github.pagehelper.PageHelper;
import io.choerodon.hap.mail.dto.MessageAccount;
import io.choerodon.hap.mail.mapper.MessageAccountMapper;
import io.choerodon.hap.mail.service.IMessageAccountService;
import io.choerodon.hap.security.service.IAESClientService;
import io.choerodon.mybatis.service.BaseServiceImpl;
import io.choerodon.web.core.IRequest;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 消息账户服务接口实现.
 *
 * @author Clerifen Li
 */
@Service
public class MessageAccountServiceImpl extends BaseServiceImpl<MessageAccount> implements IMessageAccountService, BeanFactoryAware {

    private BeanFactory beanFactory;

    @Autowired
    private MessageAccountMapper accountMapper;

    @Autowired
    private IAESClientService aceClientService;

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageAccount createMessageAccount(IRequest request, MessageAccount obj) {
        if (obj == null) {
            return null;
        }
        // aes加密
        obj.setPassword(aceClientService.encrypt(obj.getPassword()));
        accountMapper.insertSelective(obj);
        return obj;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageAccount updateMessageAccount(IRequest request, MessageAccount obj) {
        if (obj == null) {
            return null;
        }
        // 不处理password
        obj.setPassword(null);
        int updateCount = accountMapper.updateByPrimaryKeySelective(obj);
        checkOvn(updateCount, obj);
        return obj;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageAccount updateMessageAccountPasswordOnly(IRequest request, MessageAccount obj) {
        if (obj == null) {
            return null;
        }
        // aes加密
        obj.setPassword(aceClientService.encrypt(obj.getPassword()));
        int updateCount = accountMapper.updateByPrimaryKeySelective(obj);
        checkOvn(updateCount, obj);
        return obj;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public MessageAccount selectMessageAccountById(IRequest request, Long objId) {
        if (objId == null) {
            return null;
        }
        return accountMapper.selectByPrimaryKey(new BigDecimal(objId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteMessageAccount(IRequest request, MessageAccount obj) {
        if (obj.getAccountId() == null) {
            return 0;
        }
        int updateCount = accountMapper.deleteByPrimaryKey(obj);
        checkOvn(updateCount, obj);
        return updateCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDelete(IRequest request, List<MessageAccount> objs) {
        int result = 0;
        if (CollectionUtils.isEmpty(objs)) {
            return result;
        }

        for (MessageAccount obj : objs) {
            self().deleteMessageAccount(request, obj);
            result++;
        }
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<MessageAccount> selectMessageAccounts(IRequest request, MessageAccount example, int page,
                                                      int pageSize) {
        PageHelper.startPage(page, pageSize);
        return accountMapper.select(example);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<MessageAccount> selectMessageAccountPassword(IRequest request, MessageAccount example, int page,
                                                             int pageSize) {
        PageHelper.startPage(page, pageSize);
        return accountMapper.selectMessageAccountPassword(example);
    }

}
