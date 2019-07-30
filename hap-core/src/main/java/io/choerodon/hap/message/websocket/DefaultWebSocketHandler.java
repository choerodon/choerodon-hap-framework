package io.choerodon.hap.message.websocket;


import io.choerodon.hap.system.service.IBadgeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * @author hailin.xu@hand-china.com
 * @author njq.niu@hand-china.com
 */
@Component(value = "websocket")
public class DefaultWebSocketHandler implements WebSocketHandler {

    private final Logger logger = LoggerFactory.getLogger(DefaultWebSocketHandler.class);


    @Autowired
    private WebSocketSessionManager webSocketSessionManager;

    @Autowired
    private IBadgeService badgeService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        webSocketSessionManager.addSession(session);
        badgeService.initBadgeMessage(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if (session.isOpen()) {
            session.close();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        webSocketSessionManager.removeSession(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }


}