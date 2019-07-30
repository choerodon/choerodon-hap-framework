package io.choerodon.hap.attachment.exception;

import io.choerodon.base.exception.BaseException;

/**
 * @author njq.niu@hand-china.com
 */
public class AttachmentException extends BaseException {

    public AttachmentException(String code, String descriptionKey, Object[] parameters) {
        super(code, descriptionKey, parameters);
    }
}
