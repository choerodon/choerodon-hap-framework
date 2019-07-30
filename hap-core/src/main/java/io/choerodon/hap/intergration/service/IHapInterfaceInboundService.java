package io.choerodon.hap.intergration.service;

import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.intergration.dto.HapInterfaceInbound;
import io.choerodon.mybatis.service.IBaseService;

import javax.servlet.http.HttpServletRequest;

public interface IHapInterfaceInboundService
        extends IBaseService<HapInterfaceInbound>, ProxySelf<IHapInterfaceInboundService> {

    @Deprecated
    int inboundInvoke(HttpServletRequest request, HapInterfaceInbound inbound, Throwable throwable);

    int inboundInvoke(HapInterfaceInbound inbound);
}