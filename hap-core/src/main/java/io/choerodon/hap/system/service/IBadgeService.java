package io.choerodon.hap.system.service;

import org.springframework.web.socket.WebSocketSession;

/**
 * Created by xuhailin on 2017/7/20.
 */

public interface IBadgeService {
    void sendBadgeMessageToUser(String userName, String badgeCode, Long num);
    void initBadgeMessage(WebSocketSession session);
}
