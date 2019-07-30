package io.choerodon.hap.security.permission.service.impl;

import io.choerodon.hap.security.permission.dto.DataPermissionTableRule;
import io.choerodon.web.core.IRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @author jialong.zuo@hand-china.com
 * @since 2017/8/30.
 */
@Component
public class UserMaskRangeFilter extends DataPermissionRangeFilter {
    @Autowired
    private DataPermissionCacheContainer container;

    private static final String maskRange = "user_lov";

    @Override
    public void doFilter(IRequest iRequest, String tableName, Map data) throws ExecutionException {
        if ("-1".equals(iRequest.getUserId().toString())) {
            return;
        }

        List<DataPermissionTableRule> ruleCodeList = container.getTableRule(tableName);
        if (ruleCodeList.size() == 0) {
            return;
        }

        setRuleDetail(ruleCodeList, maskRange, iRequest.getUserId().toString(), container, data);

    }
}
