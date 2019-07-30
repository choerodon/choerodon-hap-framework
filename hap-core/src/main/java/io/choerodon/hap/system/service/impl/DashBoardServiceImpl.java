package io.choerodon.hap.system.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import io.choerodon.hap.system.dto.DashBoard;
import io.choerodon.hap.system.dto.UserDashboard;
import io.choerodon.hap.system.mapper.DashBoardMapper;
import io.choerodon.hap.system.mapper.UserDashboardMapper;
import io.choerodon.hap.system.service.IDashBoardService;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.system.dto.DTOStatus;
import io.choerodon.mybatis.service.BaseServiceImpl;
import io.choerodon.web.core.IRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Map;


/**
 * @author zhizheng.yang@hand-china.com
 */

@Service
@Dataset("Dashboard")
@Transactional(rollbackFor = Exception.class)
public class DashBoardServiceImpl extends BaseServiceImpl<DashBoard> implements IDashBoardService, IDatasetService<DashBoard> {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DashBoardMapper dashBoardMapper;

    @Autowired
    private UserDashboardMapper userDashboardMapper;

    @Override
    public List<DashBoard> selectDashBoard(IRequest request, DashBoard dashBoard, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return dashBoardMapper.selectDashBoards(dashBoard);
    }

    @Override
    public List<UserDashboard> selectMyDashboardConfig(IRequest request, UserDashboard dashboard) {
        return userDashboardMapper.selectMyDashboardConfig(dashboard);
    }

    @Override
    public void updateMyDashboardConfig(IRequest request, List<UserDashboard> dashboards) {
        dashboards.forEach(d -> {
            int count = userDashboardMapper.updateByPrimaryKeySelective(d);
            checkOvn(count, d);
        });

    }

    @Override
    public void removeDashboard(IRequest request, UserDashboard dashboard) {
        int count = userDashboardMapper.deleteByPrimaryKey(dashboard);
        checkOvn(count, dashboard);
    }

    @Override
    public UserDashboard insertMyDashboard(IRequest request, UserDashboard dashboard) {
        userDashboardMapper.insert(dashboard);
        return dashboard;
    }

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        DashBoard dashBoard = null;
        try {
            dashBoard = objectMapper.readValue(objectMapper.writeValueAsString(body), DashBoard.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this.selectDashBoard(null, dashBoard, page, pageSize);
    }

    @Override
    public List<DashBoard> mutations(List<DashBoard> dashBoards) {
        for (DashBoard dashBoard : dashBoards) {
            switch (dashBoard.get__status()) {
                case DTOStatus.ADD:
                    super.insertSelective(dashBoard);
                    break;
                case DTOStatus.DELETE:
                    super.deleteByPrimaryKey(dashBoard);
                    break;
                case DTOStatus.UPDATE:
                    super.updateByPrimaryKeySelective(dashBoard);
                    break;
            }
        }
        return dashBoards;
    }
}
