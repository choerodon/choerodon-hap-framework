package io.choerodon.hap.attachment.service.impl;

import io.choerodon.hap.attachment.dto.Attachment;
import io.choerodon.hap.attachment.dto.SysFile;
import io.choerodon.hap.attachment.mapper.AttachmentMapper;
import io.choerodon.hap.attachment.mapper.SysFileMapper;
import io.choerodon.hap.attachment.service.IAttachmentService;
import io.choerodon.hap.attachment.service.ISysFileService;
import io.choerodon.hap.core.annotation.StdWho;
import io.choerodon.web.core.IRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 附件service.
 *
 * @author hua.xiao@hand-china.com
 */
@Service
@Transactional
public class AttachmentServiceImpl implements IAttachmentService {

    @Autowired
    private AttachmentMapper attachmentMapper;

    @Autowired
    private SysFileMapper sysFileMapper;

    @Autowired
    private ISysFileService sysFileService;


    @Override
    public Attachment insert(IRequest requestContext, @StdWho Attachment attach) {
        attachmentMapper.insertSelective(attach);
        return attach;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Attachment selectAttachByCodeAndKey(IRequest requestContext, String sourceType, String sourceKey) {
        Attachment attachment = new Attachment();
        attachment.setSourceKey(sourceKey);
        attachment.setSourceType(sourceType);
        return attachmentMapper.selectAttachment(attachment);
    }

    @Override
    public Attachment deleteAttachment(IRequest requestContext, Attachment attach) {

        SysFile file = new SysFile();
        file.setAttachmentId(attach.getAttachmentId());
        List<SysFile> files = sysFileMapper.select(file);
        for (SysFile f : files) {
            sysFileService.delete(requestContext, f);
        }
        attachmentMapper.delete(attach);
        return attach;
    }

}
