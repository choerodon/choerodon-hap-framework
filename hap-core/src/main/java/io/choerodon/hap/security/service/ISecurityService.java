package io.choerodon.hap.security.service;

import io.choerodon.hap.security.dto.User;

/**
 * 权限安全认证服务接口.
 *
 * @author wuyichu
 * @deprecated
 */
public interface ISecurityService {

    /**
     * 设置用户.
     *
     * @param user 用户
     * @return 是否设置成功
     */
    boolean setUser(User user);

    /**
     * 校验用户.
     *
     * @param url      路径
     * @param userName 用户名称
     * @return 校验结果
     */
    boolean verifyUser(String url, String userName);
}
