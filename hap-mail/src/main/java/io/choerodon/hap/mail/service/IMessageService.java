package io.choerodon.hap.mail.service;

import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.mail.PriorityLevelEnum;
import io.choerodon.hap.mail.dto.Message;
import io.choerodon.hap.mail.dto.MessageReceiver;
import io.choerodon.mybatis.service.IBaseService;
import io.choerodon.web.core.IRequest;

import java.util.List;
import java.util.Map;

/**
 * 消息服务接口.
 *
 * @author qiang.zeng@hand-china.com
 */
public interface IMessageService extends IBaseService<Message>, ProxySelf<IMessageService> {
    /**
     * 发送模板消息.
     *
     * @param iRequest      IRequest
     * @param templateCode  模板编码
     * @param templateData  freemarker模板替换数据
     * @param receivers     收件人列表
     * @param attachmentIds 附件Id列表
     * @return 消息
     * @throws Exception 消息发送异常
     */
    Message sendMessage(IRequest iRequest, String templateCode, Map<String, Object> templateData,
                        List<MessageReceiver> receivers, List<Long> attachmentIds) throws Exception;

    /**
     * 发送自定义消息.
     *
     * @param iRequest      IRequest
     * @param accountCode   账户编码
     * @param subject       消息主题
     * @param content       消息内容
     * @param messageType   消息类型
     * @param sendType      发送类型
     * @param messageSource 消息来源
     * @param receivers     收件人列表
     * @param attachmentIds 附件Id列表
     * @return 消息
     * @throws Exception 消息发送异常
     */
    Message sendMessage(IRequest iRequest, String accountCode, String subject, String content, String messageType,
                        String sendType, String messageSource, List<MessageReceiver> receivers, List<Long> attachmentIds) throws Exception;

    /**
     * 添加模板消息，已弃用.
     *
     * @param userId        用户Id
     * @param accountCode   账户编码
     * @param templateCode  模板编码
     * @param data          freemarker模板替换数据
     * @param attachmentIds 附件Id列表
     * @param receivers     收件人列表
     * @return 消息
     * @throws Exception 邮件添加异常
     * @deprecated {@link  IMessageService#sendMessage(IRequest, String, Map, List, List)}
     */
    Message addEmailMessage(Long userId, String accountCode, String templateCode, Map<String, Object> data,
                            List<Long> attachmentIds, List<MessageReceiver> receivers) throws Exception;

    /**
     * 添加自定义消息，已弃用.
     *
     * @param userId        用户Id
     * @param accountCode   账户编码
     * @param subject       主题
     * @param content       内容
     * @param priority      优先级
     * @param attachmentIds 附件Id列表
     * @param receivers     收件人列表
     * @return 消息
     * @throws Exception 邮件添加异常
     * @deprecated {@link  IMessageService#sendMessage(IRequest, String, String, String, String, String, String, List, List)}
     */
    Message addEmailMessage(Long userId, String accountCode, String subject, String content, PriorityLevelEnum priority,
                            List<Long> attachmentIds, List<MessageReceiver> receivers) throws Exception;

    /**
     * 发送自定义消息，已弃用.
     *
     * @param sender           用户Id
     * @param marketId         市场Id
     * @param emailAccountCode 邮件账户编码
     * @param subject          主题
     * @param content          内容
     * @param priority         优先级
     * @param receivers        收件人列表
     * @param attachmentIds    附件Id列表
     * @return Message
     * @throws Exception 邮件发送异常
     * @deprecated {@link  IMessageService#sendMessage(IRequest, String, String, String, String, String, String, List, List)}
     */
    Message sendEmailMessage(Long sender, Long marketId, String emailAccountCode, String subject, String content,
                             PriorityLevelEnum priority, List<MessageReceiver> receivers, List<Long> attachmentIds) throws Exception;

    /**
     * 发送模板消息，已弃用.
     *
     * @param sender           用户Id
     * @param marketId         市场Id
     * @param templateCode     模板编码
     * @param emailAccountCode 邮件账户编码
     * @param data             freemarker模板替换数据
     * @param receivers        收件人列表
     * @param attachmentIds    附件Id列表
     * @return Message
     * @throws Exception 邮件发送异常
     * @deprecated {@link  IMessageService#sendMessage(IRequest, String, Map, List, List)}
     */
    Message sendEmailMessage(Long sender, Long marketId, String templateCode, String emailAccountCode,
                             Map<String, Object> data, List<MessageReceiver> receivers, List<Long> attachmentIds) throws Exception;

    /**
     * 根据主题查询消息列表,已弃用.
     *
     * @param requestContext IRequest
     * @param message        消息
     * @param page           页码
     * @param pageSize       页数
     * @return 消息列表
     * @deprecated {@link  IMessageService#selectMessages(IRequest, Message, int, int)}
     */
    List<Message> selectMessagesBySubject(IRequest requestContext, Message message, int page, int pageSize);

    /**
     * 条件查询消息.
     *
     * @param requestContext IRequest
     * @param message        消息
     * @param page           页码
     * @param pageSize       页数
     * @return 消息列表
     */
    List<Message> selectMessages(IRequest requestContext, Message message, int page, int pageSize);

    /**
     * 根据消息Id查询消息内容.
     *
     * @param requestContext IRequest
     * @param message        消息
     * @return 消息
     */
    Message selectMessageContent(IRequest requestContext, Message message);

    /**
     * 根据消息Id查询消息接收者.
     *
     * @param requestContext  IRequest
     * @param messageReceiver 消息接收者
     * @param page            页码
     * @param pageSize        页数
     * @return 消息接收者列表
     */
    List<MessageReceiver> selectMessageAddressesByMessageId(IRequest requestContext, MessageReceiver messageReceiver, int page, int pageSize);

    /**
     * 发送消息前 提前插入准备数据.
     *
     * @param iRequest      IRequest
     * @param message       消息
     * @param receivers     收件人列表
     * @param attachmentIds 附件Id列表
     */
    void insertData(IRequest iRequest, Message message, List<MessageReceiver> receivers, List<Long> attachmentIds);
}
