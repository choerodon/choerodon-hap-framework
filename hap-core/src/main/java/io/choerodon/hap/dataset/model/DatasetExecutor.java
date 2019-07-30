package io.choerodon.hap.dataset.model;

import java.util.List;
import java.util.Map;

/**
 * @author xausky
 */
public interface DatasetExecutor {
    /**
     * 查询方法
     */
    List<Object> queries(Map<String, Object> body, Integer pageNum, Integer pageSize, String sortname, Boolean isDesc);

    /**
     * 提交方法
     */
    List<Object> mutations(String json, Map.Entry<String, Object> parentKey);

    Map<String, Object> languages(Map<String, Object> body);

    /**
     * 校验方法
     */
    List<Boolean> validate(Map<String, Object> body, Map.Entry<String, Object> parentKey);
}