package io.choerodon.hap.mail.dto;

import io.choerodon.mybatis.entity.BaseDTO;
import org.hibernate.validator.constraints.Length;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 消息事务.
 *
 * @author njq.niu@hand-china.com
 * @since 2016年3月2日
 */
@Table(name = "sys_message_transaction")
public class MessageTransaction extends BaseDTO {

    private static final long serialVersionUID = 6726130570559853932L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    private Long messageId;

    @Length(max = 25)
    private String transactionStatus;

    private String transactionMessage;

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public String getTransactionMessage() {
        return transactionMessage;
    }

    public void setTransactionMessage(String transactionMessage) {
        this.transactionMessage = transactionMessage;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

}