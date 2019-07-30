package io.choerodon.hap.mail.dto;

import io.choerodon.mybatis.entity.BaseDTO;
import org.hibernate.validator.constraints.Length;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 消息接收者.
 *
 * @author njq.niu@hand-china.com
 * @since 2016年3月2日
 */
@Table(name = "sys_message_receiver")
public class MessageReceiver extends BaseDTO {

    private static final long serialVersionUID = 6189604448869596205L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long receiverId;

    @Length(max = 255)
    private String messageAddress;

    private Long messageId;

    /**
     * 使用此枚举类型ReceiverTypeEnum
     */
    @Length(max = 25)
    private String messageType;

    public String getMessageAddress() {
        return messageAddress;
    }

    public Long getMessageId() {
        return messageId;
    }

    public String getMessageType() {
        return messageType;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setMessageAddress(String messageAddress) {
        this.messageAddress = messageAddress == null ? null : messageAddress.trim();
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType == null ? null : messageType.trim();
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

}