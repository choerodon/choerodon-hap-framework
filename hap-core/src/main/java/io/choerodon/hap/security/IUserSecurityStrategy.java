package io.choerodon.hap.security;

import io.choerodon.hap.account.dto.User;
import io.choerodon.hap.account.exception.UserException;
import io.choerodon.web.core.IRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * 自定义用户安全策略 ，注意order，默认10.
 *
 * @author Qixiangyu
 * @author njq.niu@hand-china.com
 * @since 2016/12/22.
 */
public interface IUserSecurityStrategy extends Comparable<IUserSecurityStrategy> {

    /**
     * 在登录成功以后，跳转到index页面前对用户作出一些逻辑判断，可以重定向到其他页面.
     *
     * @param user    用户
     * @param request IRequest
     * @return 重定向到modelandview，返回空则正常跳转到index
     */
    ModelAndView loginVerifyStrategy(User user, HttpServletRequest request);

    /**
     * 校验密码策略.
     *
     * @param newPwd      新密码
     * @param newPwdAgain 校验新密码
     * @throws UserException 用户异常
     */
    void validatePassword(String newPwd, String newPwdAgain) throws UserException;

    /**
     * 创建新用户之前，对用户做一些处理.
     *
     * @param request IRequest
     * @param user    用户
     * @return 处理后的user对象, 一定要返回一个user，默认返回传入的user
     */
    default User beforeCreateUser(IRequest request, User user) {
        return user;
    }

    /**
     * 排序权重.
     *
     * @return 排序权重
     */
    default int getOrder() {
        return 10;
    }

    /**
     * 排序规则.
     *
     * @param o IUserSecurityStrategy
     * @return 比较结果
     */
    @Override
    default int compareTo(IUserSecurityStrategy o) {
        return getOrder() - o.getOrder();
    }

}
