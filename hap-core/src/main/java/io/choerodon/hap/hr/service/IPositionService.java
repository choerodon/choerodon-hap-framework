package io.choerodon.hap.hr.service;

import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.hr.dto.Position;
import io.choerodon.mybatis.service.IBaseService;

/**
 * 岗位服务接口.
 *
 * @author hailin.xu@hand-china.com
 */
public interface IPositionService extends IBaseService<Position>, ProxySelf<IPositionService> {
}
