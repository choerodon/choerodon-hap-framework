package io.choerodon.hap.mail.service.impl;

import com.github.pagehelper.PageHelper;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.mail.dto.MessageEmailAccount;
import io.choerodon.hap.mail.mapper.MessageEmailAccountMapper;
import io.choerodon.hap.mail.service.IMessageEmailAccountService;
import io.choerodon.hap.security.service.IAESClientService;
import io.choerodon.hap.system.dto.DTOStatus;
import io.choerodon.mybatis.service.BaseServiceImpl;
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
 * 邮件账号服务接口实现.
 *
 * @author Clerifen Li
 */

@Service
@Dataset("EmailAccount")
public class MessageEmailAccountServiceImpl extends BaseServiceImpl<MessageEmailAccount> implements IMessageEmailAccountService, BeanFactoryAware, IDatasetService<MessageEmailAccount> {

    private BeanFactory beanFactory;

    @Autowired
    private MessageEmailAccountMapper mapper;

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
    public MessageEmailAccount createMessageEmailAccount(IRequest request, MessageEmailAccount obj) {
        if (obj == null) {
            return null;
        }
        obj.setPassword(aceClientService.encrypt(obj.getPassword()));
        mapper.insertSelective(obj);
        return obj;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageEmailAccount updateMessageEmailAccount(IRequest request, MessageEmailAccount obj) {
        if (obj == null) {
            return null;
        }
        // 不处理password
        obj.setPassword(null);
        int updateCount = mapper.updateByPrimaryKeySelective(obj);
        checkOvn(updateCount, obj);
        return obj;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageEmailAccount updateMessageEmailAccountPasswordOnly(IRequest request, MessageEmailAccount obj) {
        if (obj == null) {
            return null;
        }
        obj.setPassword(aceClientService.encrypt(obj.getPassword()));
        int updateCount = mapper.updateByPrimaryKeySelective(obj);
        checkOvn(updateCount, obj);
        return obj;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public MessageEmailAccount selectMessageEmailAccountById(IRequest request, Long objId) {
        if (objId == null) {
            return null;
        }
        return mapper.selectByPrimaryKey(objId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteMessageEmailAccount(IRequest request, MessageEmailAccount obj) {
        if (obj.getAccountId() == null) {
            return 0;
        }
        int updateCount = mapper.deleteByPrimaryKey(obj);
        checkOvn(updateCount, obj);
        return updateCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDelete(IRequest request, List<MessageEmailAccount> objs) {
        int result = 0;
        if (CollectionUtils.isEmpty(objs)) {
            return result;
        }

        for (MessageEmailAccount obj : objs) {
            self().deleteMessageEmailAccount(request, obj);
            result++;
        }
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<MessageEmailAccount> selectMessageEmailAccounts(IRequest request, MessageEmailAccount example, int page,
                                                                int pageSize) {
        PageHelper.startPage(page, pageSize);
        return mapper.selectMessageEmailAccounts(example);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<MessageEmailAccount> selectMessageEmailAccountWithPassword(IRequest request, MessageEmailAccount example, int page,
                                                                           int pageSize) {
        PageHelper.startPage(page, pageSize);
        return mapper.selectMessageEmailAccountPassword(example);
    }

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            MessageEmailAccount emailAccount = new MessageEmailAccount();
            BeanUtils.populate(emailAccount, body);
            return self().selectMessageEmailAccounts(null, emailAccount, page, pageSize);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new DatasetException("dataset.error.MessageEmailAccount", e);
        }
    }

    @Override
    public List<MessageEmailAccount> mutations(List<MessageEmailAccount> objs) {
        for (MessageEmailAccount emailAccount : objs) {
            if (emailAccount.get__status().equals(DTOStatus.DELETE)) {
                self().deleteMessageEmailAccount(null, emailAccount);
            }
        }
        return objs;
    }

}
