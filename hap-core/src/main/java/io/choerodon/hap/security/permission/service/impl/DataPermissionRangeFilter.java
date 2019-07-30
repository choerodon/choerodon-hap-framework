package io.choerodon.hap.security.permission.service.impl;

import io.choerodon.hap.security.permission.dto.DataPermissionTableRule;
import io.choerodon.web.core.IRequest;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * @author jialong.zuo@hand-china.com
 * @since 2017/8/30.
 */
public abstract class DataPermissionRangeFilter {

    public void doFilter(IRequest iRequest, String tableName, Map data) throws ExecutionException {

    }

    protected void setRuleDetail(List<DataPermissionTableRule> ruleCodeList, String maskRange, String maskRangeValue, DataPermissionCacheContainer container, Map data) throws ExecutionException {
        for (DataPermissionTableRule ruleCode : ruleCodeList) {
            List s1 = container.getRuleDetailSet(ruleCode.getRuleId().toString(), maskRange, maskRangeValue);
            if (s1.size() == 0) {
                continue;
            }

            String field = ruleCode.getTableField() == "_PERMISSION_CUSTOM_SQL" ? "_PERMISSION_CUSTOM_SQL" : ruleCode.getTableField();

            Set dataSet = (Set) Optional.ofNullable(data.get(field)).orElseGet(() -> {
                Set set = new HashSet();
                data.put(field, set);
                return set;
            });
            dataSet.addAll(s1);
        }
    }

}
