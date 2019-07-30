package io.choerodon.hap.demo.ws;


import org.apache.wss4j.common.ext.WSPasswordCallback;
import org.springframework.stereotype.Component;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;

/**
 * @author Qixiangyu
 * @since 2016/12/16.
 */
@Component(value = "serverPasswordCallback")
public class ServerPasswordCallback implements CallbackHandler {

    private String username = "admin";

    private String password = "admin";


    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (Callback callback : callbacks) {
            //验证逻辑
            WSPasswordCallback pc = (WSPasswordCallback) callback;
            if (pc.getIdentifier().equals(username)) {// 判断来自客户端的用户名是否是指定的用户名
                //设置密码
                //这个密码和客户端发送的密码进行比较（cxf2.4以后会自动进行比较，只需要设值）
                //如果和客户端不同将抛出org.apache.ws.security.WSSecurityException
                pc.setPassword(password);
            }
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
