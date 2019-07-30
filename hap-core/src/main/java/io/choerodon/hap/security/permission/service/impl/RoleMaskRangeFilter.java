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
public class RoleMaskRangeFilter extends DataPermissionRangeFilter {

    @Autowired
    private DataPermissionCacheContainer container;

    private static final String maskRange = "LOV_ROLE";

    @Override
    public void doFilter(IRequest iRequest, String tableName, Map data) throws ExecutionException {
        if (iRequest.getAllRoleId().length == 0) {
            return;
        }

        List<DataPermissionTableRule> ruleCodeList = container.getTableRule(tableName);
        if (ruleCodeList.size() == 0) {
            return;
        }
        Long[] roleIds = iRequest.getAllRoleId();
        for (Long roleId : roleIds) {
            setRuleDetail(ruleCodeList, maskRange, roleId.toString(), container, data);
        }
    }

}
