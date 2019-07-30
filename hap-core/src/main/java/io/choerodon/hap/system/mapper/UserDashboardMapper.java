package io.choerodon.hap.system.mapper;

import io.choerodon.hap.system.dto.UserDashboard;
import io.choerodon.mybatis.common.Mapper;

import java.util.List;

public interface UserDashboardMapper extends Mapper<UserDashboard> {
    
    List<UserDashboard>  selectMyDashboardConfig(UserDashboard userDashboard);
}
