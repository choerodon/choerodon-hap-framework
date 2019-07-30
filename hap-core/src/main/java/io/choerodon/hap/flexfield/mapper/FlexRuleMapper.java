package io.choerodon.hap.flexfield.mapper;

import io.choerodon.hap.flexfield.dto.FlexRule;
import io.choerodon.mybatis.common.Mapper;

import java.util.List;

public interface FlexRuleMapper extends Mapper<FlexRule> {

    /** 匹配规则
     * @param ruleSetCode
     * @return
     */
    List<FlexRule> matchingRule(String ruleSetCode);

}