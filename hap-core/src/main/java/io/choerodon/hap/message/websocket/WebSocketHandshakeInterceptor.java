package io.choerodon.hap.message.websocket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author hailin.xu@hand-china.com
 */
@Component
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        // 解决The extension [x-webkit-deflate-frame] is not supported问题

        if (request.getHeaders().containsKey("Sec-WebSocket-Extensions")) {
            request.getHeaders().set("Sec-WebSocket-Extensions", "permessage-deflate");
        }
        ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
        HttpSession session = servletRequest.getServletRequest().getSession(false);
        if (session != null) {
            String sessionId = session.getId();
            attributes.put("sessionId", sessionId);
        }

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                               Exception exception) {
    }
}
