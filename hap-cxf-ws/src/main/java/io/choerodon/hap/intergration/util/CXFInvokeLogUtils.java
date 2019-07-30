package io.choerodon.hap.intergration.util;

import io.choerodon.hap.intergration.beans.HapInvokeInfo;
import io.choerodon.hap.intergration.beans.HapinterfaceBound;
import io.choerodon.hap.intergration.dto.HapInterfaceInbound;
import io.choerodon.hap.intergration.dto.HapInterfaceOutbound;
import io.choerodon.hap.message.components.InvokeLogManager;
import io.choerodon.message.IMessagePublisher;
import org.apache.cxf.message.Message;

/**
 * CXF 调用记录工具类.
 *
 * @author qiang.zeng
 * @since 2019/3/19.
 */
public class CXFInvokeLogUtils {
    /**
     * 用于CXF Interceptor.
     *
     * @param message          消息
     * @param messagePublisher 消息推送
     */
    public static void processCxfHandleFault(Message message, IMessagePublisher messagePublisher) {
        HapInterfaceOutbound outbound = (HapInterfaceOutbound) message.getExchange().get(HapInvokeInfo.INVOKE_INFO_OUTBOUND);
        HapInterfaceInbound inbound = (HapInterfaceInbound) message.getExchange().get(HapInvokeInfo.INVOKE_INFO_INBOUND);
        Exception fault = message.getContent(Exception.class);
        if (inbound != null) {
            inbound.setRequestStatus(HapInvokeInfo.REQUEST_FAILURE);
            inbound.setResponseTime(System.currentTimeMillis() - inbound.getRequestTime().getTime());
            HapInvokeLogUtils.processExceptionInfo(inbound, fault);
            messagePublisher.message(InvokeLogManager.CHANNEL_INBOUND, new HapinterfaceBound(inbound));
        } else if (outbound != null) {
            HapInvokeLogUtils.processExceptionInfo(outbound, fault);
            outbound.setResponseTime(System.currentTimeMillis() - outbound.getRequestTime().getTime());
            messagePublisher.message(InvokeLogManager.CHANNEL_OUTBOUND, new HapinterfaceBound(outbound));
        }
    }
}
