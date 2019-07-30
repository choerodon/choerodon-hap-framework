package io.choerodon.hap.attachment.service;

import io.choerodon.hap.attachment.dto.Attachment;
import io.choerodon.web.core.IRequest;

/**
 * 附件接口.
 *
 * @author xiaohua
 */
public interface IAttachmentService {

    /**
     * 插入附件.
     *
     * @param requestContext IRequest
     * @param attach         附件头表
     * @return Attachment 头表对象
     */
    Attachment insert(IRequest requestContext, Attachment attach);

    /**
     * 根据sourceType 和sourceKey 查询对应附件.
     *
     * @param requestContext IRequest
     * @param sourceType     业务表code
     * @param sourceKey      业务表主健
     * @return Attachment 头表对象
     */
    Attachment selectAttachByCodeAndKey(IRequest requestContext, String sourceType, String sourceKey);


    /**
     * 删除附件.
     *
     * @param requestContext IRequest
     * @param attach         附件
     * @return 附件
     */
    Attachment deleteAttachment(IRequest requestContext, Attachment attach);

}
