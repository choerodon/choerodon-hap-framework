package io.choerodon.hap.account.service;

import io.choerodon.hap.account.dto.SendRetrieve;
import io.choerodon.hap.account.exception.UserException;
import io.choerodon.web.core.IRequest;

/**
 * 发送次数限制服务接口.
 *
 * @author Zhao
 */
public interface ISendRetrieveService {

    /**
     * insert发送次数记录.
     *
     * @param request      统一上下文
     * @param sendRetrieve 发送记录dto
     * @return 记录
     * @throws UserException 用户异常
     */
    Integer insert(IRequest request, SendRetrieve sendRetrieve) throws UserException;
}
