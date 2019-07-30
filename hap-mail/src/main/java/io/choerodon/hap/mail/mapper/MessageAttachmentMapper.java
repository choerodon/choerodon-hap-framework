package io.choerodon.hap.mail.mapper;

import io.choerodon.hap.mail.dto.MessageAttachment;
import io.choerodon.mybatis.common.Mapper;

import java.util.List;

/**
 * 消息附件Mapper.
 *
 * @author chuangshneg.zhang
 */
public interface MessageAttachmentMapper extends Mapper<MessageAttachment> {
    /**
     * 根据消息Id删除消息附件.
     *
     * @param messageId 消息Id
     * @return int
     */
    int deleteByMessageId(Long messageId);

    /**
     * 根据消息Id查询消息附件.
     *
     * @param messageId 消息Id
     * @return 消息附件列表
     */
    List<MessageAttachment> selectByMessageId(Long messageId);
}