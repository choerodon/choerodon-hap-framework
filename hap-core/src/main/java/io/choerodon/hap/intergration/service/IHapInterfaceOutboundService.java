package io.choerodon.hap.intergration.service;

import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.intergration.dto.HapInterfaceOutbound;
import io.choerodon.mybatis.service.IBaseService;

public interface IHapInterfaceOutboundService
        extends IBaseService<HapInterfaceOutbound>, ProxySelf<IHapInterfaceOutboundService> {

    int outboundInvoke(HapInterfaceOutbound outbound);
}