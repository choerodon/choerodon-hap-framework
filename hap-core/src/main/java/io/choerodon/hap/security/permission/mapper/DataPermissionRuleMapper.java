package io.choerodon.hap.security.permission.mapper;

import io.choerodon.hap.security.permission.dto.DataPermissionRule;
import io.choerodon.mybatis.common.Mapper;

import java.util.List;

/**
 * @author jialong.zuo@hand-china.com on 2017/12/8
 */
public interface DataPermissionRuleMapper extends Mapper<DataPermissionRule> {

    /** 获取未被选择的规则
     * @param dataPermissionRule
     * @return
     */
    List<DataPermissionRule> selectRuleWithoutTableSelect(DataPermissionRule dataPermissionRule);

}