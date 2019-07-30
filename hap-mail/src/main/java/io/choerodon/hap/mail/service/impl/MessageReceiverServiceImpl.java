package io.choerodon.hap.mail.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.mail.dto.MessageReceiver;
import io.choerodon.hap.mail.mapper.MessageReceiverMapper;
import io.choerodon.hap.mail.service.IMessageReceiverService;
import io.choerodon.mybatis.service.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author jiameng.cao
 * @since 2018/12/14
 */

@Service
@Dataset("MailReceiver")
@Transactional(rollbackFor = Exception.class)
public class MessageReceiverServiceImpl extends BaseServiceImpl<MessageReceiver> implements IMessageReceiverService, IDatasetService<MessageReceiver> {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageReceiverMapper messageReceiverMapper;

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            MessageReceiver messageReceiver = objectMapper.readValue(objectMapper.writeValueAsString(body), MessageReceiver.class);
            PageHelper.startPage(page, pageSize);
            return messageReceiverMapper.selectByMessageId(messageReceiver.getMessageId());
        } catch (Exception e) {
            throw new DatasetException("dataset.error", e);
        }
    }

    @Override
    public List<MessageReceiver> mutations(List<MessageReceiver> objs) {
        return null;
    }
}
