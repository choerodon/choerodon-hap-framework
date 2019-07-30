package io.choerodon.hap.system.service.impl;

import io.choerodon.hap.system.dto.UserDashboard;
import io.choerodon.hap.system.service.IDashBoardService;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.system.dto.DTOStatus;
import io.choerodon.mybatis.service.BaseServiceImpl;
import io.choerodon.web.core.impl.RequestHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 个人首页配置服务接口实现.
 *
 * @author jiameng.cao
 * @since 2019/1/4
 */

@Service
@Dataset("MyDashboard")
public class MyDashBoardServiceImpl extends BaseServiceImpl<UserDashboard> implements IDatasetService<UserDashboard> {
    @Autowired
    private IDashBoardService dashBoardService;

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            return dashBoardService.selectMyDashboardConfig(null, new UserDashboard());
        } catch (Exception e) {
            throw new DatasetException("dataset.error", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<UserDashboard> mutations(List<UserDashboard> dashBoards) {
        for (UserDashboard userDashboard : dashBoards) {
            switch (userDashboard.get__status()) {
                case DTOStatus.ADD:
                    userDashboard.setUserId(RequestHelper.getCurrentRequest().getUserId());
                    dashBoardService.insertMyDashboard(null, userDashboard);
                    dashBoards = dashBoardService.selectMyDashboardConfig(null, new UserDashboard());
                    break;
                case DTOStatus.UPDATE:
                    dashBoardService.updateMyDashboardConfig(null, Collections.singletonList(userDashboard));
                    break;
                case DTOStatus.DELETE:
                    dashBoardService.removeDashboard(null, userDashboard);
                    dashBoards = dashBoardService.selectMyDashboardConfig(null, new UserDashboard());
                    break;
            }
        }
        return dashBoards;
    }
}
