package io.choerodon.hap.testext.service.impl;

import io.choerodon.hap.core.annotation.ServiceListener;
import io.choerodon.hap.extensible.base.ServiceListenerAdaptor;
import io.choerodon.hap.extensible.base.ServiceListenerChain;
import io.choerodon.hap.testext.dto.UserDemo;
import io.choerodon.hap.testext.dto.UserDemoExt;
import io.choerodon.hap.testext.dto.UserDemoExt2;
import io.choerodon.web.core.IRequest;
import org.springframework.stereotype.Component;

/**
 * @author shengyang.zhou@hand-china.com
 */
@Component
@ServiceListener(target = UserDemoServiceImpl.class)
public class UserDemoServiceListener extends ServiceListenerAdaptor<UserDemo> {

    @Override
    public void beforeUpdate(IRequest request, UserDemo record, ServiceListenerChain<UserDemo> chain) {
        System.out.println(getClass().getSimpleName() + "::beforeUpdate");
        System.out.println("extension attribute: userPhone:" + ((UserDemoExt) record).getUserPhone());
        System.out.println("extension attribute: endActiveTime:" + ((UserDemoExt2) record).getEndActiveTime());
        chain.beforeUpdate(request, record);
    }

    @Override
    public void afterUpdate(IRequest request, UserDemo record, ServiceListenerChain<UserDemo> chain) {
        System.out.println(getClass().getSimpleName() + "::afterUpdate");
        chain.afterUpdate(request, record);
    }
}
