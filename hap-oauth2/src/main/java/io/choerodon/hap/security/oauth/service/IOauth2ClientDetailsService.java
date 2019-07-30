package io.choerodon.hap.security.oauth.service;

import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.security.oauth.dto.Oauth2ClientDetails;
import io.choerodon.mybatis.service.IBaseService;

/**
 * oauth2客户端服务 - 实现类.
 *
 * @author qixiangyu
 */
public interface IOauth2ClientDetailsService extends IBaseService<Oauth2ClientDetails>, ProxySelf<IOauth2ClientDetailsService> {

    /**
     * 更新客户端.
     *
     * @param clientDetails 客户端
     * @return 客户端
     */
    Oauth2ClientDetails updateClient(Oauth2ClientDetails clientDetails);

    /**
     * 根据客户端ID获取clientDetails.
     *
     * @param clientID 客户端ID
     * @return 客户端
     */
    Oauth2ClientDetails selectByClientId(String clientID);

    /**
     * 生成新的client Secret.
     *
     * @param id 客户端主键ID
     * @return 新密码
     */
    String updatePassword(Long id);

    /**
     * 通过ID获取Oauth2客户端信息.
     *
     * @param id 主键ID
     * @return Oauth2客户端信息
     */
    Oauth2ClientDetails selectById(Long id);
}