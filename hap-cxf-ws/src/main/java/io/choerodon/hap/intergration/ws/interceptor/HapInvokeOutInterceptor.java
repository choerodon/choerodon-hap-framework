package io.choerodon.hap.intergration.ws.interceptor;

import io.choerodon.hap.intergration.beans.HapInvokeInfo;
import io.choerodon.hap.intergration.beans.HapinterfaceBound;
import io.choerodon.hap.intergration.dto.HapInterfaceInbound;
import io.choerodon.hap.intergration.dto.HapInterfaceOutbound;
import io.choerodon.hap.intergration.util.CXFInvokeLogUtils;
import io.choerodon.hap.message.components.InvokeLogManager;
import io.choerodon.message.IMessagePublisher;
import org.apache.cxf.common.injection.NoJSR250Annotations;
import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.interceptor.AbstractLoggingInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.LoggingMessage;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.io.CacheAndWriteOutputStream;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.io.CachedOutputStreamCallback;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.transport.http.Address;
import org.apache.cxf.transport.http.HTTPConduit;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.OutputStream;
import java.util.Date;
import java.util.logging.Logger;

/**
 * @author xiangyu.qi@hand-china.com on 2016/12/2.
 */
@NoJSR250Annotations
public class HapInvokeOutInterceptor extends AbstractLoggingInterceptor {

    private static final Logger LOG = LogUtils.getLogger(LoggingOutInterceptor.class);

    @Autowired
    private IMessagePublisher messagePublisher;

    public HapInvokeOutInterceptor() {
        super(Phase.PRE_STREAM);
    }

    public static final long DEFAULT_THRESHOLD = 1024 * 1024 * 10;

    public static final int DEFAULT_LIMIT = 1024 * 1024 * 10;

    @Override
    public void handleMessage(Message message) throws Fault {
        HapInterfaceInbound inbound = (HapInterfaceInbound) message.getExchange().get(HapInvokeInfo.INVOKE_INFO_INBOUND);
        if (inbound != null) {
            inboundInvoke(inbound, message);
        } else {
            outboundInvoke(message);
        }
    }

    @Override
    public void handleFault(Message message) {
        CXFInvokeLogUtils.processCxfHandleFault(message, messagePublisher);
    }

    /**
     * 出站请求
     *
     * @param message
     */
    protected void outboundInvoke(Message message) {
        HapInterfaceOutbound outbound = new HapInterfaceOutbound();
        message.getExchange().put(HapInvokeInfo.INVOKE_INFO_OUTBOUND, outbound);
        outbound.setRequestTime(new Date());
        String url;
        Object address = message.get(HTTPConduit.KEY_HTTP_CONNECTION_ADDRESS);
        if (address instanceof Address) {
            url = ((Address) address).getString();
        } else {
            url = "";
        }
        outbound.setInterfaceUrl(url);
        // 获得serviceName
        Endpoint ep = message.getExchange().getEndpoint();
        EndpointInfo endpoint = ep.getEndpointInfo();
        String serviceName = endpoint.getService().getName().getLocalPart();
        outbound.setInterfaceName(serviceName);
        getXmlContent(message);
    }

    /**
     * 入站请求
     *
     * @param inbound 入站请求记录对象
     * @param message
     */
    protected void inboundInvoke(HapInterfaceInbound inbound, Message message) {
        getXmlContent(message);
    }

    protected String getXmlContent(Message message) {
        String content = "";
        try {
            OutputStream os = message.getContent(OutputStream.class);
            final CacheAndWriteOutputStream newOut = new CacheAndWriteOutputStream(os);
            newOut.setThreshold(DEFAULT_THRESHOLD);
            message.setContent(OutputStream.class, newOut);
            newOut.registerCallback(new LoggingCallback(message, os));
        } catch (Exception e) {
            throw new Fault(e);
        }
        return content;
    }

    private LoggingMessage setupBuffer(Message message) {
        String id = (String) message.getExchange().get(LoggingMessage.ID_KEY);
        if (id == null) {
            id = LoggingMessage.nextId();
            message.getExchange().put(LoggingMessage.ID_KEY, id);
        }
        final LoggingMessage buffer = new LoggingMessage("Outbound Message\n---------------------------", id);

        Integer responseCode = (Integer) message.get(Message.RESPONSE_CODE);
        if (responseCode != null) {
            buffer.getResponseCode().append(responseCode);
        }

        String encoding = (String) message.get(Message.ENCODING);
        if (encoding != null) {
            buffer.getEncoding().append(encoding);
        }
        String httpMethod = (String) message.get(Message.HTTP_REQUEST_METHOD);
        if (httpMethod != null) {
            buffer.getHttpMethod().append(httpMethod);
        }
        String address = (String) message.get(Message.ENDPOINT_ADDRESS);
        if (address != null) {
            buffer.getAddress().append(address);
            String uri = (String) message.get(Message.REQUEST_URI);
            if (uri != null && !address.startsWith(uri)) {
                if (!address.endsWith("/") && !uri.startsWith("/")) {
                    buffer.getAddress().append("/");
                }
                buffer.getAddress().append(uri);
            }
        }
        String ct = (String) message.get(Message.CONTENT_TYPE);
        if (ct != null) {
            buffer.getContentType().append(ct);
        }
        Object headers = message.get(Message.PROTOCOL_HEADERS);
        if (headers != null) {
            buffer.getHeader().append(headers);
        }
        return buffer;
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }


    class LoggingCallback implements CachedOutputStreamCallback {

        private final Message message;
        private final OutputStream origStream;

        LoggingCallback(final Message msg, final OutputStream os) {
            this.message = msg;
            this.origStream = os;
        }

        public void onFlush(CachedOutputStream cos) {

        }

        public void onClose(CachedOutputStream cos) {
            LoggingMessage buffer = setupBuffer(message);
            HapInterfaceOutbound invokeOut = (HapInterfaceOutbound) message.getExchange().get(HapInvokeInfo.INVOKE_INFO_OUTBOUND);
            HapInterfaceInbound inbound = (HapInterfaceInbound) message.getExchange().get(HapInvokeInfo.INVOKE_INFO_INBOUND);
            String ct = (String) message.get(Message.CONTENT_TYPE);
            if (!isShowBinaryContent() && isBinaryContent(ct)) {
                buffer.getMessage().append(BINARY_CONTENT_MESSAGE).append('\n');
                return;
            }
            if (!isShowMultipartContent() && isMultipartContent(ct)) {
                buffer.getMessage().append(MULTIPART_CONTENT_MESSAGE).append('\n');
                return;
            }

            try {
                String encoding = (String) message.get(Message.ENCODING);
                setLimit(DEFAULT_LIMIT);
                writePayload(buffer.getPayload(), cos, encoding, ct);
                if (invokeOut != null) {
                    invokeOut.setRequestParameter(buffer.getPayload().toString());
                } else {
                    if (inbound != null) {
                        inbound.setResponseContent(buffer.getPayload().toString());
                        inbound.setResponseTime(System.currentTimeMillis() - inbound.getRequestTime().getTime());
                        inbound.setRequestStatus(HapInvokeInfo.REQUEST_SUCESS);
                        messagePublisher.message(InvokeLogManager.CHANNEL_INBOUND, new HapinterfaceBound(inbound));
                    }
                }
            } catch (Exception ex) {
                //ignore
            }

            try {
                //empty out the cache
                cos.lockOutputStream();
                cos.resetOut(null, false);
            } catch (Exception ex) {
                //ignore
            }
            message.setContent(OutputStream.class, origStream);

        }
    }

}
