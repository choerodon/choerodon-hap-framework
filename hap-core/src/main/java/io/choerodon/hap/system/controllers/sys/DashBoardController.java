package io.choerodon.hap.system.controllers.sys;

import io.choerodon.hap.system.dto.DashBoard;
import io.choerodon.hap.system.dto.UserDashboard;
import io.choerodon.hap.system.service.IDashBoardService;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.core.IRequest;
import io.choerodon.web.dto.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * DashBoardController.
 *
 * @author zhizheng.yang@hand-china.com
 */

@Controller
public class DashBoardController extends BaseController {

    @Autowired
    private IDashBoardService dashBoardService;

    /**
     * home page.
     *
     * @param request HttpServletRequest
     * @return ModelAndView
     */
    @Permission(type = ResourceType.SITE)
    @GetMapping(value = "/home.html")
    public ModelAndView home(HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        ModelAndView view = new ModelAndView(getViewPath() + "/home");
        UserDashboard userDashboard = new UserDashboard();
        userDashboard.setEnabledFlag("Y");
        List<UserDashboard> dashboards = dashBoardService.selectMyDashboardConfig(requestContext, userDashboard);
        view.addObject("dashboards", dashboards);
        return view;
    }

    /**
     * dashboard page.
     *
     * @param request HttpServletRequest
     * @return ModelAndView
     */
    @Permission(type = ResourceType.SITE)
    @GetMapping(value = "/dashboard.html")
    public ModelAndView dashboard(HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        ModelAndView view = new ModelAndView(getViewPath() + "/dashboard");
        List<UserDashboard> dashboards = dashBoardService.selectMyDashboardConfig(requestContext, new UserDashboard());
        view.addObject("dashboards", dashboards);
        return view;
    }

    /**
     * Add dashboard.
     *
     * @param request HttpServletRequest
     * @return ResponseData
     */
    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/dashboard/add")
    @ResponseBody
    public ResponseData addMyDashboard(HttpServletRequest request, @RequestBody List<UserDashboard> userDashboards) {
        IRequest requestContext = createRequestContext(request);
        for (UserDashboard userDashboard : userDashboards) {
            userDashboard.setUserId(requestContext.getUserId());
            dashBoardService.insertMyDashboard(createRequestContext(request), userDashboard);
        }
        return new ResponseData(dashBoardService.selectMyDashboardConfig(requestContext, new UserDashboard()));
    }

    /**
     * Save dashboard order.
     *
     * @param request        HttpServletRequest
     * @param userDashboards 用户仪表盘列表
     * @return ResponseData
     */
    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/dashboard/update")
    @ResponseBody
    public ResponseData updateMyDashboardConfig(HttpServletRequest request, @RequestBody List<UserDashboard> userDashboards) {
        dashBoardService.updateMyDashboardConfig(createRequestContext(request), userDashboards);
        return new ResponseData();
    }

    /**
     * Remove a dashboard.
     *
     * @param request HttpServletRequest
     * @return ResponseData
     */
    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/dashboard/remove")
    @ResponseBody
    public ResponseData removeDashboard(HttpServletRequest request, @RequestBody List<UserDashboard> userDashboards) {
        IRequest requestContext = createRequestContext(request);
        for (UserDashboard userDashboard : userDashboards) {
            dashBoardService.removeDashboard(requestContext, userDashboard);
        }
        return new ResponseData(dashBoardService.selectMyDashboardConfig(requestContext, new UserDashboard()));
    }

    /**
     * 仪表盘数据展示.
     *
     * @param request   HttpServletRequest
     * @param dashBoard 仪表盘
     * @param page      页码
     * @param pagesize  每页显示数量
     * @return ResponseData
     */
    @Permission(type = ResourceType.SITE)
    @PostMapping(value = {"/sys/dashboard/query", "/api/sys/dashboard/query"})
    @ResponseBody
    public ResponseData query(final DashBoard dashBoard, @RequestParam(defaultValue = DEFAULT_PAGE) final int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) final int pagesize, final HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(dashBoardService.selectDashBoard(requestContext, dashBoard, page, pagesize));
    }

    /**
     * 个人仪表盘数据展示.
     *
     * @param request HttpServletRequest
     * @return ResponseData
     */
    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/dashboard/query")
    @ResponseBody
    public ResponseData queryMyDashBoard(final HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(dashBoardService.selectMyDashboardConfig(requestContext, new UserDashboard()));
    }

    /**
     * 删除/批量删除功能.
     *
     * @param dashBoards dashBoards
     * @param result     BindingResult
     * @param request    HttpServletRequest
     * @return ResponseData
     */
    @Permission(type = ResourceType.SITE)
    @PostMapping(value = {"/sys/dashboard/remove", "/api/sys/dashboard/remove"})
    @ResponseBody
    public ResponseData remove(@RequestBody final List<DashBoard> dashBoards, final BindingResult result,
                               final HttpServletRequest request) {
        dashBoardService.batchDelete(dashBoards);
        return new ResponseData();
    }

    /**
     * 批量更新功能.
     *
     * @param dashBoards dashBoards
     * @param result     BindingResult
     * @param request    HttpServletRequest
     * @return ResponseData
     */
    @Permission(type = ResourceType.SITE)
    @PostMapping(value = {"/sys/dashboard/submit", "/api/sys/dashboard/submit"})
    @ResponseBody
    public ResponseData submit(@RequestBody final List<DashBoard> dashBoards, final BindingResult result,
                               final HttpServletRequest request) {
        getValidator().validate(dashBoards, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(dashBoardService.batchUpdate(dashBoards));
    }

}
