package io.choerodon.hap.hr.service;

import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.hr.dto.HrOrgUnit;
import io.choerodon.mybatis.service.IBaseService;

/**
 * 部门组织服务接口.
 *
 * @author zjl
 * @since 2016/9/16.
 */
public interface IOrgUnitService extends IBaseService<HrOrgUnit>, ProxySelf<IOrgUnitService> {
}
