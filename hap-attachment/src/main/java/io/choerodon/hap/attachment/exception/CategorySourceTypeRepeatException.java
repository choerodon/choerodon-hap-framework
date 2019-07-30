/*
 * #{copyright}#
 */
package io.choerodon.hap.attachment.exception;

import io.choerodon.base.exception.BaseException;

/**
 * @author hua.xiao@hand-china.com
 *
 *         2016年3月08日
 */
public class CategorySourceTypeRepeatException extends BaseException {

    private static final long serialVersionUID = 9046687211507280533L;
    
    /**
     * 创建附件分类时，sourceType重复错误.
     */
    private static final String ATTACH_CATEGORY_REPEAT = "msg.warning.dto.attachcategory.sourcetype.repeaterror";

    public CategorySourceTypeRepeatException(String code) {
        super(ATTACH_CATEGORY_REPEAT, ATTACH_CATEGORY_REPEAT, new String[]{code});
    }
}
