package io.choerodon.hap.mail.service;

import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.core.annotation.StdWho;
import io.choerodon.base.exception.BaseException;
import io.choerodon.hap.mail.dto.MessageEmailWhiteList;
import io.choerodon.web.core.IRequest;

import java.util.List;

/**
 * 邮件白名单服务接口.
 *
 * @author Clerifen Li
 */
public interface IMessageEmailWhiteListService extends ProxySelf<IMessageEmailWhiteListService> {
    /**
     * 新建邮件白名单.
     *
     * @param request IRequest
     * @param obj     邮件白名单
     * @return 邮件白名单
     * @throws BaseException 基础异常
     */
    MessageEmailWhiteList createMessageEmailWhiteList(IRequest request, @StdWho MessageEmailWhiteList obj) throws BaseException;

    /**
     * 更新邮件白名单.
     *
     * @param request IRequest
     * @param obj     邮件白名单
     * @return 邮件白名单
     * @throws BaseException 基础异常
     */
    MessageEmailWhiteList updateMessageEmailWhiteList(IRequest request, @StdWho MessageEmailWhiteList obj);

    /**
     * 根据邮件白名单Id查询邮件白名单.
     *
     * @param request IRequest
     * @param objId   邮件白名单Id
     * @return 邮件白名单
     */
    MessageEmailWhiteList selectMessageEmailWhiteListById(IRequest request, Long objId);

    /**
     * 条件查询邮件白名单.
     *
     * @param request  IRequest
     * @param obj      邮件白名单
     * @param page     页码
     * @param pageSize 页数
     * @return 邮件白名单列表
     */
    List<MessageEmailWhiteList> selectMessageEmailWhiteLists(IRequest request, MessageEmailWhiteList obj, int page, int pageSize);

    /**
     * 删除单个邮件白名单.
     *
     * @param request IRequest
     * @param obj     邮件白名单
     * @return int
     */
    int deleteMessageEmailWhiteList(IRequest request, MessageEmailWhiteList obj);

    /**
     * 批量删除邮件白名单.
     *
     * @param request IRequest
     * @param objs    邮件白名单列表
     * @return int
     * @throws BaseException 基础异常
     */
    int batchDelete(IRequest request, List<MessageEmailWhiteList> objs) throws BaseException;

}
