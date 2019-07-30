package io.choerodon.hap.system.service;

import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.system.dto.UserDashboard;
import io.choerodon.mybatis.service.IBaseService;

/**
 * 个人首页配置服务接口.
 *
 * @author jiameng.cao
 * @since 2019/1/4
 */
public interface IMyDashBoardService extends IBaseService<UserDashboard>, ProxySelf<IMyDashBoardService> {


}
