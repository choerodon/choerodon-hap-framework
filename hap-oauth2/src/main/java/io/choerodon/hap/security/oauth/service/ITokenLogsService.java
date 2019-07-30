package io.choerodon.hap.security.oauth.service;

import io.choerodon.hap.core.ProxySelf;
import io.choerodon.mybatis.service.IBaseService;
import io.choerodon.hap.security.oauth.dto.TokenLogs;

/**
 * @author qixiangyu
 */
public interface ITokenLogsService extends IBaseService<TokenLogs>, ProxySelf<ITokenLogsService> {

    /**
     * 失效Token.
     *
     * @param token token
     * @return 影响行数
     */
    int revokeToken(String token);
}