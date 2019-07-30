package io.choerodon.hap.core.exception;

import io.choerodon.base.exception.BaseException;
import io.choerodon.mybatis.entity.BaseDTO;

/**
 * 通用更新操作,更新失败:记录不存在,OBJECT_VERSION_NUMBER 不匹配。
 *
 * @author shengyang.zhou@hand-china.com
 */
public class UpdateFailedException extends BaseException {

    public static final String MESSAGE_KEY = "error.record_not_exists_or_version_not_match";

    protected UpdateFailedException(String code, String descriptionKey, Object[] parameters) {
        super(code, descriptionKey, parameters);
    }

    public UpdateFailedException() {
        this("SYS", MESSAGE_KEY, null);
    }

    public UpdateFailedException(BaseDTO record) {
        this("SYS", MESSAGE_KEY, new Object[] { record.get__id() });
    }
}
