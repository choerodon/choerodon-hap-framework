package io.choerodon.hap.adaptor;

import io.choerodon.base.exception.BaseException;
import io.choerodon.hap.account.dto.User;
import io.choerodon.hap.account.exception.RoleException;
import io.choerodon.hap.iam.infra.dto.RoleDTO;
import io.choerodon.web.dto.ResponseData;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登陆代理接口类.
 *
 * @author njq.niu@hand-china.com
 * @author xiawang.liu@hand-china.com
 * @since 2016年1月19日
 */
public interface ILoginAdaptor {

    /**
     * 超时登陆逻辑.
     *
     * @param account  登陆账号对象
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @return ResponseData
     * @throws RoleException 角色异常
     */
    ResponseData sessionExpiredLogin(User account, HttpServletRequest request, HttpServletResponse response)
            throws RoleException;

    /**
     * 角色选择逻辑.
     *
     * @param role     角色对象
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @return view
     * @throws RoleException 角色异常
     */
    ModelAndView doSelectRole(RoleDTO role, HttpServletRequest request, HttpServletResponse response) throws RoleException;

    /**
     * 登陆界面.
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @return view
     */
    ModelAndView loginView(HttpServletRequest request, HttpServletResponse response);

    /**
     * 显示角色选择界面.
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @return view viewModel
     * @throws BaseException BaseException
     */
    ModelAndView roleView(HttpServletRequest request, HttpServletResponse response) throws BaseException;

}
