package io.choerodon.hap.flexfield.mapper;

import io.choerodon.hap.flexfield.dto.FlexRuleField;
import io.choerodon.mybatis.common.Mapper;

import java.util.List;

public interface FlexRuleFieldMapper extends Mapper<FlexRuleField> {

    /** 根据 rule查询对应ruleField
     * @param flexRuleField
     * @return
     */
    List<FlexRuleField> queryFlexField(FlexRuleField flexRuleField);

}