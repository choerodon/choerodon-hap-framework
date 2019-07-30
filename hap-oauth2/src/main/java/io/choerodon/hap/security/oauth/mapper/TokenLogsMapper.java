package io.choerodon.hap.security.oauth.mapper;

import io.choerodon.hap.security.oauth.dto.TokenLogs;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * oauth2 token管理 mapper.
 *
 * @author qixiangyu
 */
public interface TokenLogsMapper extends Mapper<TokenLogs> {

    /**
     * 获取用户最后一次通行时间.
     *
     * @param userId   用户ID
     * @param clientId 客户端ID
     * @return 时间
     */
    Date findLastAccessTime(@Param("userId") Long userId, @Param("clientId") String clientId);

    /**
     * 失效Token.
     *
     * @param tokenValue token值
     * @return 影响行数
     */
    int revokeToken(String tokenValue);

    /**
     * 条件查询失效的token集合.
     *
     * @param logs 条件DTO
     * @return TokenLogs集合
     */
    List<TokenLogs> selectInvalid(TokenLogs logs);
}