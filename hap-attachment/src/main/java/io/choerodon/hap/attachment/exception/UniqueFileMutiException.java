/*
 * #{copyright}#
 */
package io.choerodon.hap.attachment.exception;

import io.choerodon.base.exception.BaseException;

/**
 * @author hua.xiao@hand-china.com
 *
 *         2016年3月03日
 */
public class UniqueFileMutiException extends BaseException {

    private static final long serialVersionUID = 9046687211507280533L;
    
    /**
     * 此分类下有多个文件异常.
     */
    private static final String FILE_MUTI_ERROR = "msg.error.system.attach.file_muti_error";

    /**
     * 构造函数.
     */
    public UniqueFileMutiException() {
        super(FILE_MUTI_ERROR, FILE_MUTI_ERROR, new Object[0]);
    }
}
