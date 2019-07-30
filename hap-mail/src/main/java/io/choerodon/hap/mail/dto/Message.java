package io.choerodon.hap.mail.dto;

import io.choerodon.mybatis.entity.BaseDTO;
import org.hibernate.validator.constraints.Length;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

/**
 * 消息.
 *
 * @author njq.niu@hand-china.com
 * @author xiawang.liu@hand-china.com 2016年3月2日
 */
@Table(name = "sys_message")
public class Message extends BaseDTO {

    private static final long serialVersionUID = -5838987819601451602L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @Length(max = 10)
    private String messageType;

    @Length(max = 255)
    private String messageHost;

    @Length(max = 255)
    private String messageFrom;

    @Length(max = 255)
    private String subject;

    private String content;

    @Length(max = 25)
    private String priorityLevel;

    @Length(max = 1)
    private String sendFlag;

    @Length(max = 255)
    private String messageSource;

    private Date creationDate;

    private Date lastUpdateDate;
    @Transient
    private List<MessageReceiver> receiverList;

    public String getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(String messageSource) {
        this.messageSource = messageSource;
    }

    public String getSendFlag() {
        return sendFlag;
    }

    public void setSendFlag(String sendFlag) {
        this.sendFlag = sendFlag;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType == null ? null : messageType.trim();
    }

    public String getMessageHost() {
        return messageHost;
    }

    public void setMessageHost(String messageHost) {
        this.messageHost = messageHost == null ? null : messageHost.trim();
    }

    public String getMessageFrom() {
        return messageFrom;
    }

    public void setMessageFrom(String messageFrom) {
        this.messageFrom = messageFrom == null ? null : messageFrom.trim();
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject == null ? null : subject.trim();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public String getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(String priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    @Override
    public Date getCreationDate() {
        return creationDate;
    }

    @Override
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    @Override
    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public List<MessageReceiver> getReceiverList() {
        return receiverList;
    }

    public void setReceiverList(List<MessageReceiver> receiverList) {
        this.receiverList = receiverList;
    }
}