package io.choerodon.hap.mail.service.impl;

import com.github.pagehelper.PageHelper;
import io.choerodon.hap.cache.impl.MessageTemplateCache;
import io.choerodon.hap.core.exception.EmailException;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.mail.MessageTypeEnum;
import io.choerodon.hap.mail.PriorityLevelEnum;
import io.choerodon.hap.mail.SendTypeEnum;
import io.choerodon.hap.mail.dto.Message;
import io.choerodon.hap.mail.dto.MessageAttachment;
import io.choerodon.hap.mail.dto.MessageEmailAccount;
import io.choerodon.hap.mail.dto.MessageReceiver;
import io.choerodon.hap.mail.dto.MessageTemplate;
import io.choerodon.hap.mail.mapper.MessageAttachmentMapper;
import io.choerodon.hap.mail.mapper.MessageEmailAccountMapper;
import io.choerodon.hap.mail.mapper.MessageMapper;
import io.choerodon.hap.mail.mapper.MessageReceiverMapper;
import io.choerodon.hap.mail.mapper.MessageTemplateMapper;
import io.choerodon.hap.mail.service.IEmailService;
import io.choerodon.hap.mail.service.IMessageService;
import io.choerodon.mybatis.service.BaseServiceImpl;
import freemarker.template.Configuration;
import freemarker.template.Template;
import io.choerodon.base.util.BaseConstants;
import io.choerodon.message.IMessagePublisher;
import io.choerodon.mybatis.entity.BaseDTO;
import io.choerodon.web.core.IRequest;
import io.choerodon.web.core.impl.RequestHelper;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 消息服务接口实现.
 *
 * @author qiang.zeng@hand-china.com
 */
@Service
@Dataset("Message")
@Transactional(rollbackFor = Exception.class)
public class MessageServiceImpl extends BaseServiceImpl<Message> implements IMessageService, IDatasetService<Message> {
    /**
     * 收件人为空
     */
    private static final String MSG_NO_MESSAGE_RECEIVER = "msg.warning.system.no_message_receiver";

    /**
     * 没有该邮件模板
     */
    private static final String MSG_NO_MESSAGE_TEMPLATE = "msg.warning.system.no_message_template";

    /**
     * 消息类型为空
     */
    private static final String MSG_MESSAGE_TYPE_EMPTY = "msg.warning.system.email_message_type_empty";

    /**
     * 没有设置优先级
     */
    private static final String MSG_MESSAGE_PRIORITY_EMPTY = "msg.warning.system.email_message_priority_empty";

    /**
     * 没有设置发送类型
     */
    private static final String MSG_MESSAGE_SEND_TYPE_EMPTY = "msg.warning.system.message_send_type_empty";

    /**
     * 没有设置邮件账户
     */
    private static final String MSG_MESSAGE_ACCOUNT_EMPTY = "msg.warning.system.message_account_empty";

    /**
     * 账户配置不存在
     */
    private static final String MSG_MESSAGE_CONFIG_NONEXISTENT = "msg.warning.system.message_config_nonexistent";

    /**
     * 账户配置被禁用
     */
    private static final String MSG_MESSAGE_CONFIG_DISABLE = "msg.warning.system.message_config_disable";

    private static final String PRIORITY_VIP = "vip";


    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private MessageAttachmentMapper attachmentMapper;

    @Autowired
    private MessageReceiverMapper receiverMapper;

    @Autowired
    private MessageTemplateMapper templateMapper;

    @Autowired
    private MessageEmailAccountMapper emailAccountMapper;

    @Autowired
    private IMessagePublisher messagePublisher;

    @Autowired
    private IEmailService emailService;
    @Autowired
    private MessageTemplateCache templateCache;

    @Override
    public Message sendMessage(IRequest iRequest, String templateCode, Map<String, Object> templateData,
                               List<MessageReceiver> receivers, List<Long> attachmentIds) throws Exception {
        if (CollectionUtils.isEmpty(receivers)) {
            //收件人为空
            throw new EmailException(MSG_NO_MESSAGE_RECEIVER);
        }
        MessageTemplate template = null;
        template = templateCache.getValue(templateCode);
        if (null == template) {
            template = templateMapper.selectByCode(templateCode);
        }
        //模板校验
        this.validateMessageTemplate(template);
        MessageEmailAccount account = emailAccountMapper.selectByPrimaryKey(template.getAccountId());
        String accountCode = null;
        if (account != null) {
            accountCode = account.getAccountCode();
        }
        String subject = translateData(template.getSubject(), templateData);
        String content = translateData(template.getContent(), templateData);
        String messageType = template.getTemplateType();
        String sendType = template.getSendType();
        this.sendMessage(iRequest, accountCode, subject, content, messageType, sendType, templateCode, receivers, attachmentIds);
        return null;
    }

    @Override
    public Message sendMessage(IRequest iRequest, String accountCode, String subject, String content, String messageType,
                               String sendType, String messageSource, List<MessageReceiver> receivers, List<Long> attachmentIds) throws Exception {
        this.validateMessageCustom(receivers, accountCode, messageType, sendType);
        if (MessageTypeEnum.EMAIL.getCode().equalsIgnoreCase(messageType)) {
            MessageEmailAccount emailAccount = new MessageEmailAccount();
            emailAccount.setAccountCode(accountCode);
            String messageEmailConfig = emailAccountMapper.selectMessageEmailConfig(emailAccount);
            if (StringUtils.isEmpty(messageEmailConfig)) {
                //消息配置不存在
                throw new EmailException(MSG_MESSAGE_CONFIG_NONEXISTENT);
            } else if (BaseConstants.NO.equals(messageEmailConfig)) {
                //消息配置被禁用
                throw new EmailException(MSG_MESSAGE_CONFIG_DISABLE);
            }
            MessageEmailAccount account = emailAccountMapper.selectByAccountCode(accountCode);

            Message message = new Message();
            message.setMessageType(messageType);
            if (SendTypeEnum.DIRECT.getCode().equalsIgnoreCase(sendType)) {
                message.setSendFlag("P");
            }
            message.setSubject(subject);
            message.setContent(content);
            message.setMessageFrom(account.getAccountCode());
            message.setMessageSource(messageSource);

            self().insertData(iRequest, message, receivers, attachmentIds);

            if (SendTypeEnum.DIRECT.getCode().equalsIgnoreCase(sendType)) {
                emailService.sendSingleEmailMessage(message, null);
            }
        }
        return null;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = Exception.class)
    public void insertData(IRequest iRequest, Message message, List<MessageReceiver> receivers, List<Long> attachmentIds) {
        initStd(message, iRequest);
        messageMapper.insertSelective(message);
        // 附件
        if (CollectionUtils.isNotEmpty(attachmentIds)) {
            for (Long current : attachmentIds) {
                MessageAttachment attachment = new MessageAttachment();
                attachment.setFileId(current);
                attachment.setMessageId(message.getMessageId());
                initStd(attachment, iRequest);

                attachmentMapper.insertSelective(attachment);
            }
        }
        // 收件人(抄送/接收人)
        for (MessageReceiver current : receivers) {
            current.setMessageId(message.getMessageId());
            initStd(current, iRequest);

            receiverMapper.insertSelective(current);
        }
    }

    @Override
    public List<Message> selectMessagesBySubject(IRequest requestContext, Message message, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return messageMapper.selectMessagesBySubject(message);
    }

    @Override
    public List<Message> selectMessages(IRequest requestContext, Message message, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return messageMapper.selectMessages(message);
    }

    @Override
    public Message selectMessageContent(IRequest requestContext, Message message) {
        return messageMapper.selectMessageContent(message);
    }

    @Override
    public List<MessageReceiver> selectMessageAddressesByMessageId(IRequest requestContext,
                                                                   MessageReceiver messageReceiver, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return receiverMapper.selectMessageAddressesByMessageId(messageReceiver);
    }

    @Override
    public Message addEmailMessage(Long userId, String accountCode, String templateCode, Map<String, Object> data,
                                   List<Long> attachmentIds, List<MessageReceiver> receivers) throws Exception {

        MessageTemplate template = templateMapper.selectByCode(templateCode);
        if (template == null) {
            // 没有该模板
            throw new EmailException(MSG_NO_MESSAGE_TEMPLATE);
        }
        if (template.getPriorityLevel() == null) {
            // 没有设置优先级
            throw new EmailException(MSG_MESSAGE_PRIORITY_EMPTY);
        }

        Message message = self().sendEmailMessage(userId, null, templateCode, accountCode, data, receivers, attachmentIds);

        if (PRIORITY_VIP.equals(template.getPriorityLevel())) {
            messagePublisher.message("hap:queue:email:vip", message);
        } else {
            messagePublisher.message("hap:queue:email:normal", message);
        }
        return message;
    }

    @Override
    public Message addEmailMessage(Long userId, String accountCode, String subject, String content,
                                   PriorityLevelEnum priority, List<Long> attachmentIds, List<MessageReceiver> receivers) throws Exception {

        Message message = self().sendEmailMessage(userId, null, accountCode, subject, content, priority, receivers, attachmentIds);
        if (PRIORITY_VIP.equals(priority.getCode())) {
            messagePublisher.message("hap:queue:email:vip", message);
        } else {
            messagePublisher.message("hap:queue:email:normal", message);
        }

        return message;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Message sendEmailMessage(Long sender, Long marketId, String templateCode, String emailAccountCode,
                                    Map<String, Object> data, List<MessageReceiver> receivers, List<Long> attachmentIds) throws Exception {
        MessageTemplate template = this.check(templateCode, emailAccountCode, receivers);
        String subject = translateData(template.getSubject(), data);
        String content = translateData(template.getContent(), data);
        String priorityS = template.getPriorityLevel();
        PriorityLevelEnum priority = PriorityLevelEnum.valueOf(priorityS);
        return this.sendEmailMessage(sender, marketId, emailAccountCode, subject, content, priority,
                receivers, attachmentIds);
    }

    public static String from(Long marketId, String accountCode) {

        StringBuilder sb = new StringBuilder();
        sb.append("I");
        sb.append(marketId);
        sb.append("://");
        sb.append(accountCode);
        return sb.toString();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Message sendEmailMessage(Long sender, Long marketId, String emailAccountCode, String subject, String content,
                                    PriorityLevelEnum priority, List<MessageReceiver> receivers, List<Long> attachmentIds) throws Exception {
        this.check(emailAccountCode, priority, receivers);

        MessageEmailAccount emailAccount = new MessageEmailAccount();
        emailAccount.setAccountCode(emailAccountCode);
        String messageEmailConfig = emailAccountMapper.selectMessageEmailConfig(emailAccount);
        if (StringUtils.isEmpty(messageEmailConfig)) {
            //消息配置不存在
            throw new EmailException(MSG_MESSAGE_CONFIG_NONEXISTENT);
        } else if (BaseConstants.NO.equals(messageEmailConfig)) {
            //消息配置被禁用
            throw new EmailException(MSG_MESSAGE_CONFIG_DISABLE);
        }
        MessageEmailAccount account = emailAccountMapper.selectByAccountCode(emailAccountCode);

        Message message = new Message();
        message.setMessageType(MessageTypeEnum.EMAIL.getCode());
        message.setPriorityLevel(priority.getCode());
        message.setSubject(subject);
        message.setContent(content);
        message.setSendFlag("N");
        if (null == marketId) {
            message.setMessageFrom(account.getAccountCode());
        } else {
            message.setMessageFrom(from(marketId, account.getAccountCode()));
        }

        IRequest iRequest = RequestHelper.newEmptyRequest();
        iRequest.setUserId(sender);
        initStd(message, iRequest);

        messageMapper.insertSelective(message);

        // 附件
        if (attachmentIds != null && attachmentIds.size() > 0) {
            for (Long current : attachmentIds) {
                MessageAttachment attachment = new MessageAttachment();
                attachment.setMessageId(message.getMessageId());
                attachment.setFileId(current);
                initStd(attachment, iRequest);

                attachmentMapper.insertSelective(attachment);
            }
        }
        // 收件人(抄送/接收人)
        for (MessageReceiver current : receivers) {
            current.setMessageId(message.getMessageId());
            initStd(current, iRequest);

            receiverMapper.insertSelective(current);
        }
        return message;
    }

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            Message message = new Message();
            BeanUtils.populate(message, body);
            message.setSortname(sortname);
            message.setSortorder(isDesc ? "desc" : "asc");
            return self().selectMessages(null, message, page, pageSize);
        } catch (Exception e) {
            throw new DatasetException("dataset.error", e);
        }
    }

    @Override
    public List<Message> mutations(List<Message> objs) {
        return null;
    }

    /**
     * 校验消息模板.
     *
     * @param template 消息模板
     * @throws EmailException 邮件校验异常
     */
    private void validateMessageTemplate(MessageTemplate template) throws EmailException {
        if (null == template) {
            throw new EmailException(MSG_NO_MESSAGE_TEMPLATE);
        }
        if (StringUtils.isEmpty(template.getTemplateType())) {
            throw new EmailException(MSG_MESSAGE_TYPE_EMPTY);
        } else if (MessageTypeEnum.EMAIL.getCode().equalsIgnoreCase(template.getTemplateType())) {
            if (StringUtils.isEmpty(template.getSendType())) {
                throw new EmailException(MSG_MESSAGE_SEND_TYPE_EMPTY);
            }
            if (null == template.getAccountId()) {
                throw new EmailException(MSG_MESSAGE_ACCOUNT_EMPTY);
            }
        }
    }

    /**
     * 校验自定义消息.
     *
     * @param receivers   收件人列表
     * @param accountCode 账户编码
     * @param messageType 消息类型
     * @param sendType    发送类型
     * @throws EmailException 邮件校验异常
     */
    private void validateMessageCustom(List<MessageReceiver> receivers, String accountCode, String messageType, String sendType) throws EmailException {
        if (CollectionUtils.isEmpty(receivers)) {
            throw new EmailException(MSG_NO_MESSAGE_RECEIVER);
        }
        if (StringUtils.isEmpty(messageType)) {
            throw new EmailException(MSG_MESSAGE_TYPE_EMPTY);
        } else if (MessageTypeEnum.EMAIL.getCode().equalsIgnoreCase(messageType)) {
            if (StringUtils.isEmpty(sendType)) {
                throw new EmailException(MSG_MESSAGE_SEND_TYPE_EMPTY);
            }
            if (StringUtils.isEmpty(accountCode)) {
                throw new EmailException(MSG_MESSAGE_ACCOUNT_EMPTY);
            }
        }
    }

    /**
     * 转换模板数据[freemarker].
     *
     * @param content 模板内容
     * @param data    替换模板的数据
     * @return String
     * @throws Exception 模板转换异常
     */
    private String translateData(String content, Map data) throws Exception {
        if (content == null) {
            return "";
        }
        try (StringWriter out = new StringWriter()) {
            Configuration config = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
            config.setDefaultEncoding("UTF-8");
            // classic compatible,是${abc}允许出现空值的
            config.setClassicCompatible(true);
            Template template = new Template(null, content, config);
            template.process(data, out);
            return out.toString();
        }
    }

    /**
     * 初始化WHO字段的信息.
     *
     * @param dto      BaseDTO
     * @param iRequest IRequest
     */
    private void initStd(BaseDTO dto, IRequest iRequest) {
        dto.setObjectVersionNumber(1L);
        dto.setCreationDate(new Date());
        dto.setLastUpdateDate(new Date());
        if (iRequest != null) {
            dto.setCreatedBy(iRequest.getUserId());
            dto.setLastUpdatedBy(iRequest.getUserId());
        } else {
            dto.setCreatedBy(null);
            dto.setLastUpdatedBy(null);
        }
    }

    /**
     * 校验消息模板.
     *
     * @param templateCode 模板编码
     * @param accountCode  账户编码
     * @param receivers    收件人列表
     * @return 消息模板
     * @throws EmailException 邮件校验异常
     */
    private MessageTemplate check(String templateCode, String accountCode, List<MessageReceiver> receivers) throws EmailException {
        if (CollectionUtils.isEmpty(receivers)) {
            // 没有设置收件人,报错
            throw new EmailException(MSG_NO_MESSAGE_RECEIVER);
        }
        MessageTemplate record = new MessageTemplate();
        record.setTemplateCode(templateCode);
        List<MessageTemplate> selectMessageTemplates = templateMapper.select(record);

        if (CollectionUtils.isEmpty(selectMessageTemplates)) {
            // 没有该模板
            throw new EmailException(MessageServiceImpl.MSG_NO_MESSAGE_TEMPLATE);
        }
        MessageTemplate template = selectMessageTemplates.get(0);

        if (template == null) {
            throw new EmailException(MessageServiceImpl.MSG_NO_MESSAGE_TEMPLATE);
        }

        if (template.getPriorityLevel() == null) {
            // 没有设置优先级
            throw new EmailException(MSG_MESSAGE_PRIORITY_EMPTY);
        }
        if (accountCode == null) {
            throw new EmailException(MSG_MESSAGE_ACCOUNT_EMPTY);
        }
        return template;
    }

    /**
     * 校验自定义消息.
     *
     * @param accountCode 账户编码
     * @param priority    优先级
     * @param receivers   收件人列表
     * @throws EmailException 邮件校验异常
     */
    private void check(String accountCode, PriorityLevelEnum priority, List<MessageReceiver> receivers) throws EmailException {
        if (CollectionUtils.isEmpty(receivers)) {
            // 没有设置收件人,报错
            throw new EmailException(MSG_NO_MESSAGE_RECEIVER);
        }
        if (priority == null) {
            // 没有设置优先级
            throw new EmailException(MSG_MESSAGE_PRIORITY_EMPTY);
        }

        for (MessageReceiver messageReceiver : receivers) {
            String messageAddress = messageReceiver.getMessageAddress();
            if (messageAddress == null || "".equals(messageAddress.trim())) {
                throw new EmailException(MSG_NO_MESSAGE_RECEIVER);
            }
        }
        if (accountCode == null) {
            throw new EmailException(MSG_MESSAGE_ACCOUNT_EMPTY);
        }
    }
}