package io.choerodon.hap.message.components;

import io.choerodon.hap.intergration.InvokeLogStrategy;
import io.choerodon.hap.intergration.beans.HapinterfaceBound;
import io.choerodon.hap.intergration.components.DefaultInvokeLogStrategy;
import io.choerodon.message.IMessageConsumer;
import io.choerodon.message.annotation.QueueMonitor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author xiangyu.qi@hand-china.com on 2017/9/23.
 */
@Component
@QueueMonitor(queue = InvokeLogManager.CHANNEL_INBOUND)
public class InvokeLogManager implements IMessageConsumer<HapinterfaceBound>, InitializingBean {

    public static final String CHANNEL_OUTBOUND = "invoke.outbound";

    public static final String CHANNEL_INBOUND = "invoke.inbound";

    @Autowired(required = false)
    private InvokeLogStrategy invokeLogStrategy;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void onMessage(HapinterfaceBound message, String pattern) {
        if(InvokeLogManager.CHANNEL_INBOUND.equalsIgnoreCase(pattern)){
            invokeLogStrategy.logInbound(message.getInbound());
        }else if(InvokeLogManager.CHANNEL_OUTBOUND.equalsIgnoreCase(pattern)){
            invokeLogStrategy.logOutbound(message.getOutbound());
        }
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        if(invokeLogStrategy == null){
            invokeLogStrategy = new DefaultInvokeLogStrategy();
            applicationContext.getAutowireCapableBeanFactory().autowireBean(invokeLogStrategy);
        }
    }

    public InvokeLogStrategy getInvokeLogStrategy(){
        return invokeLogStrategy;
    }
}
