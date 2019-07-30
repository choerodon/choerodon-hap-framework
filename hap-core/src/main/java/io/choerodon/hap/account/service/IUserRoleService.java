package io.choerodon.hap.account.service;

import io.choerodon.hap.account.dto.UserRole;
import io.choerodon.hap.core.ProxySelf;
import io.choerodon.mybatis.service.IBaseService;

/**
 * 用户角色分配服务接口.
 *
 * @author xiawang.liu@hand-china.com
 */
public interface IUserRoleService extends IBaseService<UserRole>, ProxySelf<IUserRoleService> {

}