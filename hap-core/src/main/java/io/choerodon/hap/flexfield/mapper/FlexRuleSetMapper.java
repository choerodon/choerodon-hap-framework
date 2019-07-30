package io.choerodon.hap.flexfield.mapper;

import io.choerodon.hap.flexfield.dto.FlexRuleSet;
import io.choerodon.mybatis.common.Mapper;

import java.util.List;

public interface FlexRuleSetMapper extends Mapper<FlexRuleSet> {


    /** 查询规则集
     * @param model
     * @return
     */
    List<FlexRuleSet> queryFlexRuleSet(FlexRuleSet model);

}