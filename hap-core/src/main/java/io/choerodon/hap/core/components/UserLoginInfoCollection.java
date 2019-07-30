package io.choerodon.hap.core.components;

import io.choerodon.hap.account.dto.User;
import io.choerodon.hap.security.IAuthenticationSuccessListener;
import io.choerodon.hap.system.dto.UserLogin;
import io.choerodon.message.IMessagePublisher;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.InetAddress;
import java.util.Date;

/**
 * Created by jialong.zuo@hand-china.com on 2016/10/11. on 2016/10/11.
 */
@Component
public class UserLoginInfoCollection implements IAuthenticationSuccessListener {

    @Autowired
    IMessagePublisher iMessagePublisher;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) {

        String ipAddress = getIpAddress(request);

        UserLogin userLogin = new UserLogin();
        userLogin.setUserId((Long)request.getSession(false).getAttribute(User.FIELD_USER_ID));
        userLogin.setReferer(StringUtils.abbreviate(request.getHeader("Referer"),240));
        userLogin.setUserAgent(StringUtils.abbreviate(request.getHeader("User-Agent"), 240));
        userLogin.setIp(ipAddress);
        userLogin.setLoginTime(new Date());
        iMessagePublisher.message("hap:queue:loginInfo", userLogin);
    }

    @Override
    public int getOrder() {
        return 999;
    }

    public static String getIpAddress(HttpServletRequest request) {

        String ipAddress = request.getHeader("x-forwarded-for");
        if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if(ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")){
                //根据网卡取本机配置的IP
                InetAddress inet=null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ipAddress= inet.getHostAddress();
            }
        }
        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if(ipAddress!=null && ipAddress.length()>15){ //"***.***.***.***".length() = 15
            if(ipAddress.indexOf(",")>0){
                ipAddress = ipAddress.substring(0,ipAddress.indexOf(","));
            }
        }
        return ipAddress;

    }

}
