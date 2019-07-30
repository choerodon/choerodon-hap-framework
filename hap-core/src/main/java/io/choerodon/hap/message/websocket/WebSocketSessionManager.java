package io.choerodon.hap.message.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.message.IMessageConsumer;
import io.choerodon.message.annotation.TopicMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author njq.niu@hand-china.com
 */
@TopicMonitor(channel = {WebSocketSessionManager.CHANNEL_WEB_SOCKET})
@Component
public class WebSocketSessionManager implements IMessageConsumer<CommandMessage> {

    public static final String CHANNEL_WEB_SOCKET = "webSocket";

    private final Logger logger = LoggerFactory.getLogger(WebSocketSessionManager.class);

    private final Map<String, List<WebSocketSession>> webSocketSessionMap = new ConcurrentHashMap<>();

    @Autowired
    private ObjectMapper objectMapper;


    public void addSession(WebSocketSession session) {
        String userName = getPrincipalName(session);
        if (!StringUtils.isEmpty(userName)) {
            List<WebSocketSession> sessions = webSocketSessionMap.computeIfAbsent(userName, k -> new ArrayList<>());
            sessions.add(session);
            if (logger.isDebugEnabled()) {
                logger.debug("Add webSocketSession {}, total size {}", session.getId(), sessions.size());
            }
        }

    }


    public void removeSession(WebSocketSession session) {
        String userName = getPrincipalName(session);
        if (!StringUtils.isEmpty(userName)) {
            List<WebSocketSession> sessions = webSocketSessionMap.get(userName);
            if (sessions != null) {
                sessions.remove(session);
                logger.debug("Remove webSocketSession {}, total size {}", session.getId(), sessions.size());
            }
        }
    }

    public List<WebSocketSession> getSession(String userName) {
        List<WebSocketSession> sessions = webSocketSessionMap.get(userName);
        return sessions != null ? sessions : Collections.EMPTY_LIST;
    }

    public void sendCommandMessage(WebSocketSession session, CommandMessage commandMessage) {
        try {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(commandMessage)));
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error("send commandMessage error! commandMessage: {} ", commandMessage.toString());
            }
        }
    }


    @Override
    public void onMessage(CommandMessage commandMessage, String channel) {
        List<WebSocketSession> sessions = getSession(commandMessage.getUserName());
        if (sessions != null) {
            String sessionId = commandMessage.getSessionId();
            sessions.stream().filter(ws -> !sessionId.equalsIgnoreCase((String) ws.getAttributes().get("sessionId"))).forEach(webSocketSession -> {
                this.sendCommandMessage(webSocketSession, commandMessage);
            });
        }
    }

    public String getPrincipalName(WebSocketSession session) {
        if (session == null || session.getPrincipal() == null) {
            logger.error("WebSocket session principal is null,the handler has expired");
            return null;
        }
        return session.getPrincipal().getName();
    }
}
