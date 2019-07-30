package io.choerodon.hap.message.websocket;

import io.choerodon.message.IMessageConsumer;
import io.choerodon.message.annotation.TopicMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

/**
 * @author xuhailin
 * @since 2017/7/18.
 */
@TopicMonitor(channel = {BadgeManager.CHANNEL_BADGE})
@Component
public class BadgeManager implements IMessageConsumer<CommandMessage> {

    public static final String CHANNEL_BADGE = "channel_badge";

    @Autowired
    private WebSocketSessionManager webSocketSessionManager;


    @Override
    public void onMessage(CommandMessage commandMessage, String channel) {
        List<WebSocketSession> sessions = webSocketSessionManager.getSession(commandMessage.getUserName());
        sessions.forEach(webSocketSession -> {
            webSocketSessionManager.sendCommandMessage(webSocketSession, commandMessage);
        });
    }


}
