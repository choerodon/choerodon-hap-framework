package io.choerodon.hap.system.service;

import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.core.annotation.StdWho;
import io.choerodon.hap.system.dto.DashBoard;
import io.choerodon.hap.system.dto.UserDashboard;
import io.choerodon.mybatis.service.IBaseService;
import io.choerodon.web.core.IRequest;

import java.util.List;

/**
 * @author zhizheng.yang@hand-china.com
 */
public interface IDashBoardService extends IBaseService<DashBoard>, ProxySelf<IDashBoardService> {

    /**
     * 仪表盘多功能查询.
     *
     * @param request   IRequest
     * @param dashBoard 仪表盘
     * @param page      页码
     * @param pageSize  每页数量
     * @return 仪表盘列表
     */
    List<DashBoard> selectDashBoard(IRequest request, DashBoard dashBoard, int page, int pageSize);

    /**
     * 查询我的仪表盘配置.
     *
     * @param request   IRequest
     * @param dashboard 用户仪表盘
     * @return 用户仪表盘列表
     */
    List<UserDashboard> selectMyDashboardConfig(IRequest request, UserDashboard dashboard);

    /**
     * 更新仪表盘顺序.
     *
     * @param request    IRequest
     * @param dashboards 用户仪表盘列表
     */
    void updateMyDashboardConfig(IRequest request, @StdWho List<UserDashboard> dashboards);

    /**
     * 增加仪表盘.
     *
     * @param request   IRequest
     * @param dashboard 用户仪表盘
     */
    UserDashboard insertMyDashboard(IRequest request, @StdWho UserDashboard dashboard);

    /**
     * 删除某个仪表盘.
     *
     * @param request   IRequest
     * @param dashboard 用户仪表盘
     */
    void removeDashboard(IRequest request, UserDashboard dashboard);
}
