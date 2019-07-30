package io.choerodon.hap.mail.service.impl;

import com.github.pagehelper.PageHelper;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.mail.dto.MessageEmailWhiteList;
import io.choerodon.hap.mail.mapper.MessageEmailWhiteListMapper;
import io.choerodon.hap.mail.service.IMessageEmailWhiteListService;
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
 * 邮件白名单服务接口实现.
 *
 * @author Clerifen Li
 */
@Service
@Dataset("EmailWhiteList")
public class MessageEmailWhiteListServiceImpl extends BaseServiceImpl<MessageEmailWhiteList> implements IMessageEmailWhiteListService, BeanFactoryAware, IDatasetService<MessageEmailWhiteList> {

    private BeanFactory beanFactory;

    @Autowired
    private MessageEmailWhiteListMapper mapper;

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageEmailWhiteList createMessageEmailWhiteList(IRequest request, MessageEmailWhiteList obj) {
        if (obj == null) {
            return null;
        }
        mapper.insertSelective(obj);
        return obj;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageEmailWhiteList updateMessageEmailWhiteList(IRequest request, MessageEmailWhiteList obj) {
        if (obj == null) {
            return null;
        }
        int updateCount = mapper.updateByPrimaryKeySelective(obj);
        checkOvn(updateCount, obj);
        return obj;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public MessageEmailWhiteList selectMessageEmailWhiteListById(IRequest request, Long objId) {
        if (objId == null) {
            return null;
        }
        return mapper.selectByPrimaryKey(objId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteMessageEmailWhiteList(IRequest request, MessageEmailWhiteList obj) {
        if (obj.getId() == null) {
            return 0;
        }
        int updateCount = mapper.deleteByPrimaryKey(obj);
        checkOvn(updateCount, obj);
        return updateCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDelete(IRequest request, List<MessageEmailWhiteList> objs) {
        int result = 0;
        if (CollectionUtils.isEmpty(objs)) {
            return result;
        }

        for (MessageEmailWhiteList obj : objs) {
            self().deleteMessageEmailWhiteList(request, obj);
            result++;
        }
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<MessageEmailWhiteList> selectMessageEmailWhiteLists(IRequest request, MessageEmailWhiteList example, int page,
                                                                    int pageSize) {
        PageHelper.startPage(page, pageSize);
        return mapper.select(example);
    }

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            MessageEmailWhiteList emailAccount = new MessageEmailWhiteList();
            BeanUtils.populate(emailAccount, body);
            return self().selectMessageEmailWhiteLists(null, emailAccount, page, pageSize);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new DatasetException("dataset.error.MessageEmailWhiteList", e);
        }
    }

    @Override
    public List<MessageEmailWhiteList> mutations(List<MessageEmailWhiteList> objs) {
        for (MessageEmailWhiteList emailWhiteList : objs) {
            if (emailWhiteList.get__status().equals(DTOStatus.DELETE)) {
                self().deleteMessageEmailWhiteList(null, emailWhiteList);
            }
        }
        return objs;
    }
}
