package io.choerodon.hap.security.oauth.mapper;

import io.choerodon.mybatis.common.Mapper;
import io.choerodon.hap.security.oauth.dto.Oauth2ClientDetails;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * oauth2客户端mapper.
 *
 * @author qixiangyu
 */
public interface Oauth2ClientDetailsMapper extends Mapper<Oauth2ClientDetails> {

    /**
     * 根据客户端ID查询Oauth2客户端.
     *
     * @param clientId 客户端ID
     * @return Oauth2客户端
     */
    Oauth2ClientDetails selectByClientId(String clientId);

    /**
     * 修改密码.
     *
     * @param id          Oauth2客户端主键ID
     * @param passwordNew 新密码
     * @return 影响行数
     */
    int updatePassword(@Param("id") Long id, @Param("clientSecret") String passwordNew);

    /**
     * 获取所有Oauth2客戶端.
     *
     * @return 客戶端集合
     */
    List<Oauth2ClientDetails> selectAllClient();
}