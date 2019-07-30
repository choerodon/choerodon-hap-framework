package io.choerodon.hap.mail.mapper;

import io.choerodon.hap.mail.dto.Message;
import io.choerodon.mybatis.common.Mapper;

import java.util.List;

/**
 * 消息Mapper.
 *
 * @author Clerifen Li
 * @author xiawang.liu@hand-china.com
 */
public interface MessageMapper extends Mapper<Message> {
    /**
     * 没有发出的邮件列表,普通队列.
     *
     * @return 邮件列表
     */
    List<Message> selectEmailToSend();

    /**
     * 没有发出的邮件列表,优先队列.
     *
     * @return 邮件列表
     */
    List<Message> selectVipEmailToSend();

    /**
     * 根据主题查询消息.
     *
     * @param message 消息
     * @return 消息列表
     */
    List<Message> selectMessagesBySubject(Message message);

    /**
     * 查询发送类型为定时任务的邮件列表.
     *
     * @return 邮件列表
     */
    List<Message> selectEmailSendByJob();

    /**
     * 条件查询消息.
     *
     * @param message 消息
     * @return 消息列表
     */
    List<Message> selectMessages(Message message);

    /**
     * 查询消息内容.
     *
     * @param message 消息
     * @return 消息
     */
    Message selectMessageContent(Message message);
}