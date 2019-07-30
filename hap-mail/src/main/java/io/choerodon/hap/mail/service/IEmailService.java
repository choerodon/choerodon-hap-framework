package io.choerodon.hap.mail.service;

import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.mail.dto.Message;
import io.choerodon.hap.mail.dto.MessageTransaction;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 邮件服务接口.
 *
 * @author qiang.zeng@hand-china.com
 */
public interface IEmailService extends ProxySelf<IEmailService> {
    /**
     * 定时器定时发送邮件.
     *
     * @param params 计划任务传入的参数Map
     * @return true：邮件已全部发送
     * @throws Exception 邮件发送过程异常
     */
    boolean sendMessages(Map<String, Object> params) throws Exception;

    /**
     * 重新发送邮件.
     *
     * @param messages 邮件列表
     * @param params   计划任务传入的参数Map
     * @return true：邮件已全部发送
     * @throws Exception 邮件发送过程异常
     */
    boolean reSendMessages(List<Message> messages, Map<String, Object> params) throws Exception;

    /**
     * 保存邮件发送结果.
     *
     * @param message 邮件
     * @param obj     消息事务
     */
    void saveTransaction(Message message, MessageTransaction obj);

    /**
     * 消息队列发送邮件.
     *
     * @param message 邮件
     * @param params  计划任务传入的参数Map
     * @return true:邮件已发送
     * @throws Exception 邮件发送过程异常
     */
    boolean sendMessageByReceiver(Message message, Map<String, Object> params) throws Exception;

    /**
     * 批量发送邮件，附件由系统附件管理上传.
     *
     * @param userEmailToSend 邮件列表
     * @param params          计划任务传入的参数Map
     * @return true：邮件已全部发送
     * @throws Exception 邮件发送过程异常
     */
    boolean sendEmailMessage(List<Message> userEmailToSend, Map<String, Object> params) throws Exception;

    /**
     * 批量发送邮件,附件由外部传入文件流.
     *
     * @param userEmailToSend 邮件列表
     * @param fileList        文件流列表
     * @return true：邮件已全部发送
     * @throws Exception 邮件发送过程异常
     */
    boolean sendEmailMessageWithFile(List<Message> userEmailToSend, List<File> fileList) throws Exception;

    /**
     * 发送单个邮件，附件由系统附件管理上传.
     *
     * @param message 邮件
     * @param params  计划任务传入的参数Map
     * @return true:邮件已发送 false:邮件未发送
     * @throws Exception 邮件发送过程异常
     */
    boolean sendSingleEmailMessage(Message message, Map<String, Object> params) throws Exception;

    /**
     * 发送单个邮件，附件由外部传入文件流.
     *
     * @param message  邮件
     * @param fileList 文件流列表
     * @return true:邮件已发送 false:邮件未发送
     * @throws Exception 邮件发送过程异常
     */
    boolean sendSingleEmailMessageWithFile(Message message, List<File> fileList) throws Exception;

}
