package io.choerodon.hap.flexfield.service;

import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.flexfield.dto.FlexRuleField;
import io.choerodon.mybatis.service.IBaseService;

import java.util.List;

public interface IFlexRuleFieldService extends IBaseService<FlexRuleField>, ProxySelf<IFlexRuleFieldService> {

    /**
     * 查询弹性域规则Field.
     *
     * @param flexRuleField 查询参数
     * @return 相应的FlexRuleField
     */
    List<FlexRuleField> queryFlexRuleField(FlexRuleField flexRuleField);

}