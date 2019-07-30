package io.choerodon.hap.mail.service.impl;

import com.github.pagehelper.PageHelper;
import io.choerodon.hap.attachment.dto.SysFile;
import io.choerodon.hap.attachment.service.ISysFileService;
import io.choerodon.hap.cache.impl.MessageEmailConfigCache;
import io.choerodon.hap.mail.EmailStatusEnum;
import io.choerodon.hap.mail.EnvironmentEnum;
import io.choerodon.hap.mail.MailSender;
import io.choerodon.hap.mail.ReceiverTypeEnum;
import io.choerodon.hap.mail.dto.Message;
import io.choerodon.hap.mail.dto.MessageAddress;
import io.choerodon.hap.mail.dto.MessageAttachment;
import io.choerodon.hap.mail.dto.MessageEmailAccount;
import io.choerodon.hap.mail.dto.MessageEmailConfig;
import io.choerodon.hap.mail.dto.MessageEmailProperty;
import io.choerodon.hap.mail.dto.MessageEmailWhiteList;
import io.choerodon.hap.mail.dto.MessageReceiver;
import io.choerodon.hap.mail.dto.MessageTransaction;
import io.choerodon.hap.mail.mapper.MessageAttachmentMapper;
import io.choerodon.hap.mail.mapper.MessageEmailAccountMapper;
import io.choerodon.hap.mail.mapper.MessageEmailConfigMapper;
import io.choerodon.hap.mail.mapper.MessageEmailPropertyMapper;
import io.choerodon.hap.mail.mapper.MessageEmailWhiteListMapper;
import io.choerodon.hap.mail.mapper.MessageMapper;
import io.choerodon.hap.mail.mapper.MessageReceiverMapper;
import io.choerodon.hap.mail.mapper.MessageTransactionMapper;
import io.choerodon.hap.mail.service.IEmailService;
import io.choerodon.base.util.BaseConstants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 邮件服务接口实现.
 *
 * @author qiang.zeng@hand-china.com
 */
@Transactional(rollbackFor = Exception.class)
@Service
public class EmailServiceImpl implements IEmailService, BeanFactoryAware {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final int TWENTY = 20;

    private static final int FIFTY = 50;
    /**
     * 外部传入的文件流附件.
     */
    private static final String ATTACHMENT_FILE = "file";
    /**
     * 系统附件管理上传的附件.
     */
    private static final String ATTACHMENT_SYSTEM = "system";


    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private MessageAttachmentMapper attachmentMapper;

    @Autowired
    private MessageReceiverMapper receiverMapper;

    @Autowired
    private MessageTransactionMapper transactionMapper;

    @Autowired
    private MessageEmailAccountMapper emailAccountMapper;

    @Autowired
    private MessageEmailWhiteListMapper emailWhiteListMapper;

    @Autowired
    private MessageEmailConfigMapper emailConfigMapper;

    private BeanFactory beanFactory;

    @Autowired
    private ISysFileService sysFileService;

    @Autowired
    private MessageEmailPropertyMapper emailPropertyMapper;

    @Autowired
    private MessageEmailConfigCache configCache;

    @Override
    public boolean sendMessages(Map<String, Object> params) throws Exception {
        Integer batch = (Integer) params.get("batch");
        boolean isVipQueue = (boolean) params.get("isVipQueue");

        if (batch == null) {
            batch = TWENTY;
        }
        if (batch == 0) {
            batch = TWENTY;
        }
        PageHelper.startPage(1, batch);

        List<Message> userEmailToSend;
        if (isVipQueue) {
            userEmailToSend = messageMapper.selectVipEmailToSend();
        } else {
            userEmailToSend = messageMapper.selectEmailToSend();
        }
        return sendMessage(userEmailToSend, params);
    }

    @Override
    public boolean reSendMessages(List<Message> messages, Map<String, Object> params) throws Exception {
        return sendMessage(messages, params);
    }

    @Override
    public boolean sendMessageByReceiver(Message message, Map<String, Object> params) throws Exception {
        List<Message> messages = new ArrayList<>();
        messages.add(message);
        return sendMessage(messages, params);
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public boolean sendEmailMessage(List<Message> userEmailToSend, Map<String, Object> params) throws Exception {
        sendEmail(userEmailToSend, null, params, ATTACHMENT_SYSTEM);
        return true;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public boolean sendEmailMessageWithFile(List<Message> userEmailToSend, List<File> fileList) throws Exception {
        sendEmail(userEmailToSend, fileList, null, ATTACHMENT_FILE);
        return true;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public boolean sendSingleEmailMessage(Message currentMessage, Map<String, Object> params) throws Exception {
        return sendSingleEmail(currentMessage, params, null, ATTACHMENT_SYSTEM);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public boolean sendSingleEmailMessageWithFile(Message message, List<File> fileList) throws Exception {
        return sendSingleEmail(message, null, fileList, ATTACHMENT_FILE);
    }

    public boolean sendMessage(List<Message> userEmailToSend, Map<String, Object> params) throws Exception {

        Map<String, MailSender> senders = new HashMap<>(1);

        int success = 0;

        messageSetPending(userEmailToSend);

        try {
            for (Message currentMessage : userEmailToSend) {

                if (StringUtils.isAnyBlank(currentMessage.getSubject(), currentMessage.getContent())) {
                    error(currentMessage, "subject or content is null");
                    continue;
                }
                List<MessageAttachment> attachments = attachmentMapper.selectByMessageId(currentMessage.getMessageId());

                String messageFrom = currentMessage.getMessageFrom();
                MessageAddress address = MessageAddress.toAddressObject(messageFrom);
                messageFrom = address.getAddress();
                MailSender mailSender = senders.get(messageFrom);
                if (mailSender == null) {
                    mailSender = (MailSender) beanFactory.getBean("mailSender");

                    MessageEmailAccount record = new MessageEmailAccount();
                    record.setAccountCode(messageFrom);
                    List<MessageEmailAccount> selectMessageEmailAccounts = emailAccountMapper
                            .selectMessageEmailAccounts(record);

                    if (CollectionUtils.isEmpty(selectMessageEmailAccounts)) {
                        error(currentMessage, "email account is no more exists:" + messageFrom);
                        continue;
                    }
                    MessageEmailAccount mailAccount = selectMessageEmailAccounts.get(0);

                    // email账号被删除的情况
                    if (mailAccount == null) {
                        error(currentMessage, "email account is no more exists:" + messageFrom);
                        continue;
                    }

                    MessageEmailConfig config = emailConfigMapper.selectByPrimaryKey(mailAccount.getConfigId());
                    mailSender.setHost(config.getHost());
                    mailSender.setPort(Integer.parseInt(config.getPort()));
                    if (config.getTryTimes() != null) {
                        mailSender.setTryTimes(config.getTryTimes().intValue());
                    }
                    mailSender.setMessageAccount(mailAccount.getUserName());
                    setAuthUserNameAndPassword(mailSender, mailAccount, config);

                    // 白名单
                    if (BaseConstants.YES.equalsIgnoreCase(config.getUseWhiteList())) {
                        List<MessageEmailWhiteList> whitelist = emailWhiteListMapper.selectByConfigId(config.getConfigId());
                        List<String> stringList = new ArrayList<>();
                        for (MessageEmailWhiteList current : whitelist) {
                            stringList.add(current.getAddress());
                        }
                        mailSender.setWhiteList(stringList);
                    }
                    senders.put(messageFrom, mailSender);
                }

                // 创建多元化邮件
                MimeMessage mimeMessage = generateMimeMessage(currentMessage, null, attachments, mailSender);
                if (ArrayUtils.isEmpty(mimeMessage.getAllRecipients())) {
                    error(currentMessage, "The recipient is empty!");
                    continue;
                }

                MessageTransaction obj = generateMessageTransaction(currentMessage);

                // 失败,重试次数
                for (int i = 0; i < mailSender.getTryTimes(); i++) {
                    try {
                        mailSender.send(mimeMessage);
                        success++;
                        if (log.isDebugEnabled()) {
                            log.debug("Send mail success, {}.", i);
                        }
                        obj.setTransactionStatus(EmailStatusEnum.SUCCESS.getCode());
                        currentMessage.setSendFlag("Y");
                        this.trySaveTransaction(currentMessage, obj);
                        break;
                    } catch (Exception e) {
                        if (i == mailSender.getTryTimes() - 1) {
                            saveFailMessageTransaction(currentMessage, obj, e);
                        } else {
                            Thread.sleep(FIFTY);
                        }
                    }
                }
            }
        } catch (Exception e) {
            params.put("ERROR_MESSAGE", e.getMessage());
            for (Message message : userEmailToSend) {
                error(message, e.getMessage());
            }
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
            throw e;
        }
        prepareSummary(userEmailToSend, params, success);
        return true;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = Exception.class)
    public void saveTransaction(Message message, MessageTransaction obj) {
        if (message != null) {
            messageMapper.updateByPrimaryKeySelective(message);
        }
        if (obj != null) {
            transactionMapper.insertSelective(obj);
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    /**
     * 更新消息状态为失败.
     *
     * @param message 消息
     * @param msg     异常信息
     */
    private void error(Message message, String msg) {
        MessageTransaction obj = generateMessageTransaction(message);
        obj.setTransactionStatus(EmailStatusEnum.ERROR.getCode());
        obj.setTransactionMessage(msg);
        message.setSendFlag("F");
        this.trySaveTransaction(message, obj);
    }

    /**
     * 更新消息状态为发送中.
     *
     * @param messages 消息列表
     */
    private void messageSetPending(List<Message> messages) {
        for (Message current : messages) {
            current.setSendFlag("P");
            this.trySaveTransaction(current, null);
        }
    }

    /**
     * 设置邮件账户的用户名和密码.
     *
     * @param mailSender          消息服务
     * @param messageEmailAccount 邮件账户
     * @param config              邮件配置
     */
    private void setAuthUserNameAndPassword(MailSender mailSender, MessageEmailAccount messageEmailAccount, MessageEmailConfig config) {
        if (StringUtils.isEmpty(messageEmailAccount.getPassword())) {
            mailSender.setUsername(config.getUserName());
            mailSender.setPassword(config.getPassword());
        } else {
            mailSender.setUsername(messageEmailAccount.getUserName());
            mailSender.setPassword(messageEmailAccount.getPassword());
        }
    }

    /**
     * 发送单个邮件.
     *
     * @param message  邮件
     * @param params   计划任务传入的参数Map
     * @param fileList 文件列表
     * @return true:邮件已发送 false:邮件未发送
     * @throws MessagingException 多元化邮件初始化异常
     */
    private boolean sendSingleEmail(Message message, Map<String, Object> params, List<File> fileList, String type) throws MessagingException, UnsupportedEncodingException {
        if (StringUtils.isAnyBlank(message.getSubject(), message.getContent())) {
            error(message, "subject or content is null");
            return false;
        }
        //初始化MailSender
        MailSender mailSender = generateMailSender(message);
        MimeMessage mimeMessage;
        try {
            //初始化MimeMessage
            mimeMessage = generateMimeMessage(message, fileList, getMessageAttachments(message, type), mailSender);
            if (ArrayUtils.isEmpty(mimeMessage.getAllRecipients())) {
                error(message, "The recipient is empty!");
                return false;
            }
        } catch (Exception e) {
            if (params != null) {
                params.put("ERROR_MESSAGE", e.getMessage());
            }
            error(message, e.getMessage());
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
            throw e;
        }
        MessageTransaction obj = generateMessageTransaction(message);
        try {
            mailSender.send(mimeMessage);
            if (log.isDebugEnabled()) {
                log.debug("Send mail success.");
            }
            obj.setTransactionStatus(EmailStatusEnum.SUCCESS.getCode());
            message.setSendFlag("Y");
            this.trySaveTransaction(message, obj);
        } catch (Exception e) {
            saveFailMessageTransaction(message, obj, e);
            throw e;
        }
        //调用job发送邮件时 记录下job运行详情信息放入Summary变量中
        if (params != null) {
            List<Message> messageList = new ArrayList<>();
            messageList.add(message);
            prepareSummary(messageList, params, 1);
        }
        return true;
    }

    /**
     * 发送批量邮件.
     *
     * @param userEmailToSend 邮件列表
     * @param fileList        文件列表
     * @param params          计划任务传入的参数Map
     * @param type            附件类型
     * @throws MessagingException 多元化邮件初始化异常
     */
    private void sendEmail(List<Message> userEmailToSend, List<File> fileList, Map<String, Object> params, String type) throws MessagingException, UnsupportedEncodingException {
        int success = 0;
        Map<String, MailSender> senders = new HashMap<>(1);
        try {
            for (Message currentMessage : userEmailToSend) {
                if (StringUtils.isAnyBlank(currentMessage.getSubject(), currentMessage.getContent())) {
                    error(currentMessage, "subject or content is null");
                    continue;
                }
                String messageFrom = currentMessage.getMessageFrom();
                MessageAddress address = MessageAddress.toAddressObject(messageFrom);
                messageFrom = address.getAddress();
                MailSender mailSender = senders.computeIfAbsent(messageFrom, k -> generateMailSender(currentMessage));
                // 创建多元化邮件
                MimeMessage mimeMessage = generateMimeMessage(currentMessage, fileList, getMessageAttachments(currentMessage, type), mailSender);
                if (ArrayUtils.isEmpty(mimeMessage.getAllRecipients())) {
                    error(currentMessage, "The recipient is empty!");
                    continue;
                }
                MessageTransaction obj = generateMessageTransaction(currentMessage);
                try {
                    mailSender.send(mimeMessage);
                    success++;
                    if (log.isDebugEnabled()) {
                        log.debug("Send mail success.");
                    }
                    obj.setTransactionStatus(EmailStatusEnum.SUCCESS.getCode());
                    currentMessage.setSendFlag("Y");
                    this.trySaveTransaction(currentMessage, obj);
                } catch (Exception e) {
                    saveFailMessageTransaction(currentMessage, obj, e);
                }
            }
        } catch (Exception e) {
            if (params != null) {
                params.put("ERROR_MESSAGE", e.getMessage());
            }
            for (Message message : userEmailToSend) {
                error(message, e.getMessage());
            }
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
            throw e;
        }
        //调用job发送邮件时 记录下job运行详情信息放入Summary变量中
        if (params != null) {
            prepareSummary(userEmailToSend, params, success);
        }
    }

    /**
     * 创建消息服务.
     *
     * @param message 消息
     * @return 消息服务
     */
    private MailSender generateMailSender(Message message) {
        MailSender mailSender = (MailSender) beanFactory.getBean("mailSender");
        MessageEmailAccount messageEmailAccount = emailAccountMapper.selectByAccountCode(message.getMessageFrom());
        //从redis缓存读取邮件账户配置 如果缓存不存在 从数据库再查一遍
        MessageEmailConfig config = configCache.getValue(messageEmailAccount.getConfigId().toString());
        if (null == config) {
            config = emailConfigMapper.selectByPrimaryKey(messageEmailAccount.getConfigId());
            config.setWhiteLists(emailWhiteListMapper.selectByConfigId(config.getConfigId()));
            config.setPropertyLists(emailPropertyMapper.selectByConfigId(config.getConfigId()));
        }
        mailSender.setHost(config.getHost());
        mailSender.setPort(Integer.parseInt(config.getPort()));
        mailSender.setMessageAccount(messageEmailAccount.getUserName());
        setAuthUserNameAndPassword(mailSender, messageEmailAccount, config);

        // 白名单
        if (BaseConstants.YES.equalsIgnoreCase(config.getUseWhiteList())) {
            List<MessageEmailWhiteList> whitelist = config.getWhiteLists();
            List<String> stringList = new ArrayList<>();
            for (MessageEmailWhiteList current : whitelist) {
                stringList.add(current.getAddress());
            }
            mailSender.setWhiteList(stringList);
        }
        //邮件服务器属性
        if (config.getPropertyLists() != null) {
            List<MessageEmailProperty> propertyLists = config.getPropertyLists();
            Properties props = new Properties();
            for (MessageEmailProperty emailProperty : propertyLists) {
                props.put(emailProperty.getPropertyName(), emailProperty.getPropertyCode());
            }
            mailSender.setJavaMailProperties(props);
        }
        return mailSender;
    }

    /**
     * 创建多元化邮件.
     *
     * @param currentMessage 邮件
     * @param fileList       文件列表
     * @param attachments    附件列表
     * @param mailSender     消息服务
     * @return 多元化邮件
     * @throws MessagingException           多元化邮件初始化异常
     * @throws UnsupportedEncodingException 文件名称编码异常
     */
    private MimeMessage generateMimeMessage(Message currentMessage, List<File> fileList, List<MessageAttachment> attachments, MailSender mailSender) throws MessagingException, UnsupportedEncodingException {
        List<MessageReceiver> receivers = receiverMapper.selectByMessageId(currentMessage.getMessageId());
        // 生成多元化邮件
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        // 创建 mimeMessage 帮助类，用于封装信息至 mimeMessage
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        // 发送者地址，必须填写正确的邮件格式，否者会发送失败
        helper.setFrom(mailSender.getMessageAccount());
        // 邮件主题
        helper.setSubject(this.setEmailSubject(mailSender, currentMessage.getSubject()));
        //邮件内容
        if (CollectionUtils.isNotEmpty(fileList)) {
            // 外部传入的文件流附件
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(currentMessage.getContent(), "text/html; charset=UTF-8");
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            FileDataSource fileDataSource;
            for (File file : fileList) {
                messageBodyPart = new MimeBodyPart();
                fileDataSource = new FileDataSource(file);
                messageBodyPart.setDataHandler(new DataHandler(fileDataSource));
                messageBodyPart.setFileName(MimeUtility.encodeText(file.getName()));
                multipart.addBodyPart(messageBodyPart);
            }
            mimeMessage.setContent(multipart);
        } else {
            helper.setText(currentMessage.getContent(), true);
            // 系统附件管理上传的文件
            if (CollectionUtils.isNotEmpty(attachments)) {
                List<String> attachmentIds = new ArrayList<>();
                for (MessageAttachment attachment : attachments) {
                    attachmentIds.add(attachment.getFileId().toString());
                }
                List<SysFile> fileNames = sysFileService.selectByIds(null, attachmentIds);
                for (SysFile sysFile : fileNames) {
                    File file = new File(sysFile.getFilePath());
                    if (file.isFile()) {
                        helper.addAttachment(FilenameUtils.getName(sysFile.getFileName()), file);
                    }
                }
            }
        }

        //收件人
        for (MessageReceiver receiver : receivers) {
            if (receiver.getMessageAddress() == null) {
                continue;
            }
            // 白名单过滤
            if (CollectionUtils.isNotEmpty(mailSender.getWhiteList())
                    && !mailSender.getWhiteList().contains(receiver.getMessageAddress())) {
                continue;
            }
            if (ReceiverTypeEnum.NORMAL.getCode().equalsIgnoreCase(receiver.getMessageType())) {
                // 收件人
                helper.addTo(receiver.getMessageAddress());
            } else if (ReceiverTypeEnum.CC.getCode().equalsIgnoreCase(receiver.getMessageType())) {
                // 抄送
                helper.addCc(receiver.getMessageAddress());
            } else if (ReceiverTypeEnum.BCC.getCode().equalsIgnoreCase(receiver.getMessageType())) {
                // 密送
                helper.addBcc(receiver.getMessageAddress());
            }
        }
        return mimeMessage;
    }

    /**
     * 获取邮件系统附件.
     *
     * @param message 邮件
     * @param type    附件类型
     * @return 系统附件列表
     */
    private List<MessageAttachment> getMessageAttachments(Message message, String type) {
        List<MessageAttachment> attachments = null;
        //判断是否是系统附件 如果是 查系统附件表
        if (ATTACHMENT_SYSTEM.equals(type)) {
            attachments = attachmentMapper.selectByMessageId(message.getMessageId());
        }
        return attachments;
    }

    /**
     * 创建消息事务.
     *
     * @param message 消息
     * @return 消息事务
     */
    private MessageTransaction generateMessageTransaction(Message message) {
        MessageTransaction obj = new MessageTransaction();
        Date time = new Date();
        obj.setCreatedBy(-1L);
        obj.setLastUpdatedBy(-1L);
        obj.setCreationDate(time);
        obj.setLastUpdateDate(time);
        obj.setMessageId(message.getMessageId());
        obj.setObjectVersionNumber(0L);
        return obj;
    }

    /**
     * 邮件发送失败，更新邮件状态.
     *
     * @param currentMessage 消息
     * @param obj            消息事务
     * @param e              异常
     */
    private void saveFailMessageTransaction(Message currentMessage, MessageTransaction obj, Exception e) {
        obj.setTransactionMessage(getExceptionStack(e));
        obj.setTransactionStatus(EmailStatusEnum.ERROR.getCode());
        currentMessage.setSendFlag("F");
        this.trySaveTransaction(currentMessage, obj);
        if (log.isErrorEnabled()) {
            log.error("Send mail failed.", e);
        }
    }

    /**
     * set summary to param map, SendMessageJob will use it.
     *
     * @param messages emails to send
     * @param param    execution param
     */
    private void prepareSummary(List<Message> messages, Map<String, Object> param, int success) {
        StringBuilder sb = new StringBuilder();
        if (messages.isEmpty()) {
            sb.append("No Email To Send.");
        } else {
            sb.append("Send ").append(messages.size()).append(" Emails. ");
            sb.append("  Success : ").append(success);
            Object object = param.get("ERROR_MESSAGE");
            if (object != null) {
                sb.append("  Error :  ").append(object);
            }
        }
        param.put("summary", sb.toString());
    }

    /**
     * 更新邮件状态.
     *
     * @param message 消息
     * @param obj     消息事务
     */
    private void trySaveTransaction(Message message, MessageTransaction obj) {
        int dataBaseTryTime = 3;
        for (int i = 0; i < dataBaseTryTime; i++) {
            try {
                self().saveTransaction(message, obj);
                return;
            } catch (Exception e) {
                if (i == dataBaseTryTime - 1) {
                    if (log.isErrorEnabled()) {
                        log.error("save transaction failed.", e);
                    }
                }
            }
        }
    }

    /**
     * 当发出邮箱带有sit,uat等标记的时候,发出邮件主题上加入[sit],[uat]等标记.
     *
     * @param mailSender 消息服务
     * @param subject    主题
     * @return String
     */
    private String setEmailSubject(MailSender mailSender, String subject) {
        if ((EnvironmentEnum.SIT.getCode().equals(mailSender.getEnvironment())
                || EnvironmentEnum.UAT.getCode().equals(mailSender.getEnvironment()))) {
            return "[" + mailSender.getEnvironment() + "] " + subject;
        }
        return subject;
    }

    /**
     * 获取异常堆栈信息.
     *
     * @param th Throwable
     * @return String
     */
    private String getExceptionStack(Throwable th) {
        return ExceptionUtils.getStackTrace(th);
    }
}
