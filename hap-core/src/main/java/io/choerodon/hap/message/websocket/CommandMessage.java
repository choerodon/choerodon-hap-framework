package io.choerodon.hap.message.websocket;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.util.Map;

public class CommandMessage implements Serializable {

    private String action;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String userName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String sessionId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, Object> parameter;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Map<String, Object> getParameter() {
        return parameter;
    }

    public void setParameter(Map<String, Object> parameter) {
        this.parameter = parameter;
    }
}
