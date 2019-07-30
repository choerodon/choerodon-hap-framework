package io.choerodon.hap.flexfield.service;

import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.flexfield.dto.FlexModel;
import io.choerodon.mybatis.service.IBaseService;

import java.util.List;

public interface IFlexModelService extends IBaseService<FlexModel>, ProxySelf<IFlexModelService> {

    /**
     * 删除弹性域模型.
     *
     * @param models 需要删除的FlexModel
     */
    void deleteFlexModel(List<FlexModel> models);

}