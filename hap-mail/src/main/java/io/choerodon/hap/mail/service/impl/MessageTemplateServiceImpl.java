package io.choerodon.hap.mail.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import io.choerodon.hap.cache.impl.MessageTemplateCache;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.mail.dto.MessageTemplate;
import io.choerodon.hap.mail.mapper.MessageTemplateMapper;
import io.choerodon.hap.mail.service.IMessageTemplateService;
import io.choerodon.hap.system.dto.DTOStatus;
import io.choerodon.mybatis.service.BaseServiceImpl;
import io.choerodon.web.core.IRequest;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 消息模板服务接口实现.
 *
 * @author qiang.zeng@hand-china.com
 */
@Service
@Dataset("MailTemplate")
public class MessageTemplateServiceImpl extends BaseServiceImpl<MessageTemplate> implements IMessageTemplateService, IDatasetService<MessageTemplate> {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MessageTemplateMapper templateMapper;
    @Autowired
    private MessageTemplateCache templateCache;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageTemplate createMessageTemplate(IRequest request, MessageTemplate obj) {
        if (obj == null) {
            return null;
        }
        templateMapper.insertSelective(obj);
        templateCache.reload(obj.getTemplateCode());
        return obj;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageTemplate updateMessageTemplate(IRequest request, MessageTemplate obj) {
        if (obj == null) {
            return null;
        }
        int updateCount = templateMapper.updateByPrimaryKeySelective(obj);
        checkOvn(updateCount, obj);
        templateCache.reload(obj.getTemplateCode());
        return obj;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public MessageTemplate selectMessageTemplateById(IRequest request, Long objId) {
        if (objId == null) {
            return null;
        }
        return templateMapper.selectByPrimaryKey(objId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteMessageTemplate(IRequest request, MessageTemplate obj) {
        if (obj.getTemplateId() == null) {
            return 0;
        }
        int result = templateMapper.deleteByPrimaryKey(obj);
        checkOvn(result, obj);
        templateCache.reload(obj.getTemplateCode());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDelete(IRequest request, List<MessageTemplate> objs) {
        int result = 0;
        if (CollectionUtils.isEmpty(objs)) {
            return result;
        }

        for (MessageTemplate obj : objs) {
            self().deleteMessageTemplate(request, obj);
            result++;
        }
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<MessageTemplate> selectMessageTemplates(IRequest request, MessageTemplate example, int page,
                                                        int pageSize) {
        PageHelper.startPage(page, pageSize);
        return templateMapper.selectMessageTemplates(example);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            MessageTemplate messageTemplate = new MessageTemplate();
            BeanUtils.populate(messageTemplate, body);
            return self().selectMessageTemplates(null, messageTemplate, page, pageSize);
        } catch (Exception e) {
            throw new DatasetException("dataset.error", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<MessageTemplate> mutations(List<MessageTemplate> messageTemplates) {
        for (MessageTemplate messageTemplate : messageTemplates) {
            switch (messageTemplate.get__status()) {
                case DTOStatus.ADD:
                    self().createMessageTemplate(null, messageTemplate);
                    break;
                case DTOStatus.UPDATE:
                    self().updateMessageTemplate(null, messageTemplate);
                    break;
                case DTOStatus.DELETE:
                    self().deleteMessageTemplate(null, messageTemplate);
                    break;
            }
        }
        return messageTemplates;
    }
}
