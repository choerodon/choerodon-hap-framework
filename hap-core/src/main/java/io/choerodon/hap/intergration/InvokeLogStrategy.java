package io.choerodon.hap.intergration;

import io.choerodon.hap.intergration.dto.HapInterfaceInbound;
import io.choerodon.hap.intergration.dto.HapInterfaceOutbound;
import io.choerodon.web.core.IRequest;

import java.util.List;

/**
 * @author xiangyu.qi@hand-china.com
 * @since 2017/9/23.
 */
public interface InvokeLogStrategy {


    /**
     * 记录入站请求记录.
     *
     * @param inbound 入站请求相关信息
     */
    void logInbound(HapInterfaceInbound inbound);

    /**
     * 记录出产请求记录.
     *
     * @param outbound 出站请求相关信息
     */
    void logOutbound(HapInterfaceOutbound outbound);

    /**
     * 查询入站请求记录.
     *
     * @param request   IRequest
     * @param condition 查询条件
     * @param pageNum   页数
     * @param pageSize  单页条数
     * @return 入站请求记录
     */
    List<HapInterfaceInbound> queryInbound(IRequest request, HapInterfaceInbound condition, int pageNum, int pageSize);


    /**
     * 查询出站请求记录.
     *
     * @param request   IRequest
     * @param condition 查询条件
     * @param pageNum   页数
     * @param pageSize  单页条数
     * @return 出站请求记录
     */
    List<HapInterfaceOutbound> queryOutbound(IRequest request, HapInterfaceOutbound condition, int pageNum, int pageSize);
}
