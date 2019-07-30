/*
 * #{copyright}#
 */
package io.choerodon.hap.mail.dto;

import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 消息附件.
 *
 * @author njq.niu@hand-china.com
 * @author 2016年3月2日
 */
@Table(name = "sys_message_attachment")
public class MessageAttachment extends BaseDTO {

    private static final long serialVersionUID = -8831715672578562022L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attachmentId;

    private Long fileId;

    private Long messageId;

    public Long getAttachmentId() {
        return attachmentId;
    }

    public Long getFileId() {
        return fileId;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setAttachmentId(Long attachmentId) {
        this.attachmentId = attachmentId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

}