package io.choerodon.hap.dataset.exception;

import io.choerodon.base.exception.BaseRuntimeException;

public class DatasetException extends BaseRuntimeException {
    public DatasetException(String descriptionKey, Object ...parameters) {
        super("DATASET", descriptionKey, parameters);
    }
}
