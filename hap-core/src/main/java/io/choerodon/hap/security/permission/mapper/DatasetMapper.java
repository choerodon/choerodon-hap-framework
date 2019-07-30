package io.choerodon.hap.security.permission.mapper;

import io.choerodon.hap.security.permission.dto.DataPermissionRuleAssign;
import io.choerodon.hap.security.permission.dto.DataPermissionRuleDetail;
import io.choerodon.hap.security.permission.dto.DataPermissionTableRule;

import java.util.List;
import java.util.Map;

public interface DatasetMapper {
    List<DataPermissionRuleDetail> selectRuleDetailUser(Map<String, Object> param);
    List<DataPermissionRuleDetail> selectRuleDetailRole(Map<String, Object> param);
    List<DataPermissionRuleAssign> selectRuleDetailAssign(Map<String, Object> param);
    List<DataPermissionTableRule> selectTableRule(Map<String, Object> param);
}
