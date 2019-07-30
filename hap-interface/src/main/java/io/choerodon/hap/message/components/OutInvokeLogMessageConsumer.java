package io.choerodon.hap.message.components;

import io.choerodon.hap.intergration.beans.HapinterfaceBound;
import io.choerodon.message.IMessageConsumer;
import io.choerodon.message.annotation.QueueMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author xiangyu.qi@hand-china.com on 2017/11/27.
 */
@Component
@QueueMonitor(queue = InvokeLogManager.CHANNEL_OUTBOUND)
public class OutInvokeLogMessageConsumer  implements IMessageConsumer<HapinterfaceBound> {

    @Autowired
    private InvokeLogManager invokeLogManager;

    @Override
    public void onMessage(HapinterfaceBound message, String pattern) {
        invokeLogManager.getInvokeLogStrategy().logOutbound(message.getOutbound());
    }
}
