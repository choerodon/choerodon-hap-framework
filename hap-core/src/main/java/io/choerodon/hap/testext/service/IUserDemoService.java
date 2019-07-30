package io.choerodon.hap.testext.service;

import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.testext.dto.UserDemo;
import io.choerodon.mybatis.service.IBaseService;

public interface IUserDemoService extends IBaseService<UserDemo>, ProxySelf<IUserDemoService>{

}