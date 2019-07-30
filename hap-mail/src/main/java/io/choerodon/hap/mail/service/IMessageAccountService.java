package io.choerodon.hap.mail.service;

import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.core.annotation.StdWho;
import io.choerodon.base.exception.BaseException;
import io.choerodon.hap.mail.dto.MessageAccount;
import io.choerodon.web.core.IRequest;

import java.util.List;

/**
 * 消息账户服务接口.
 *
 * @author Clerifen Li
 */
public interface IMessageAccountService extends ProxySelf<IMessageAccountService> {
    /**
     * 新建消息账户.
     *
     * @param request IRequest
     * @param obj     消息账户
     * @return 消息账户
     * @throws BaseException 基础异常
     */
    MessageAccount createMessageAccount(IRequest request, @StdWho MessageAccount obj) throws BaseException;

    /**
     * 更新消息账户,账户密码不更新.
     *
     * @param request IRequest
     * @param obj     消息账户
     * @return 消息账户
     * @throws BaseException 基础异常
     */
    MessageAccount updateMessageAccount(IRequest request, @StdWho MessageAccount obj);

    /**
     * 只更新消息账户密码.
     *
     * @param request IRequest
     * @param obj     消息账户
     * @return 消息账户
     * @throws BaseException 基础异常
     */
    MessageAccount updateMessageAccountPasswordOnly(IRequest request, MessageAccount obj);

    /**
     * 根据账户Id查询消息账户.
     *
     * @param request IRequest
     * @param objId   账户Id
     * @return 消息账户
     */
    MessageAccount selectMessageAccountById(IRequest request, Long objId);

    /**
     * 条件查询消息账户列表.
     *
     * @param request  IRequest
     * @param obj      消息账户
     * @param page     页码
     * @param pageSize 页数
     * @return 消息账户列表
     */
    List<MessageAccount> selectMessageAccounts(IRequest request, MessageAccount obj, int page, int pageSize);

    /**
     * 删除单个消息账户.
     *
     * @param request IRequest
     * @param obj     消息账户
     * @return int
     */
    int deleteMessageAccount(IRequest request, MessageAccount obj);

    /**
     * 批量删除消息账户.
     *
     * @param request            request
     * @param messageAccountList 消息账户
     * @return int
     * @throws BaseException 基础异常
     */
    int batchDelete(IRequest request, List<MessageAccount> messageAccountList) throws BaseException;

    /**
     * 查询消息账户密码.
     *
     * @param request  IRequest
     * @param obj      消息账户
     * @param page     页码
     * @param pageSize 页数
     * @return 消息账户列表
     */
    List<MessageAccount> selectMessageAccountPassword(IRequest request, MessageAccount obj, int page, int pageSize);

}
