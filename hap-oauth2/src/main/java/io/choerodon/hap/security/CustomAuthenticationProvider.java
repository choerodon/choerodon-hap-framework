package io.choerodon.hap.security;

import io.choerodon.hap.core.components.SysConfigManager;
import io.choerodon.hap.core.components.UserLoginInfoCollection;
import io.choerodon.base.util.BaseConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * 校验认证次数.
 *
 * @author JiangP
 * @since 2017/8/30.
 */
public class CustomAuthenticationProvider extends DaoAuthenticationProvider implements BaseConstants {

    private static ThreadLocal<String> clientId = new ThreadLocal<>();

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private SysConfigManager sysConfigManager;

    private static final String redisCatalog = HAP_CACHE + "authentication_limit:";

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
                                                  UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        if (authentication.getCredentials() == null) {
            logger.debug("Authentication failed: no credentials provided");

            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials",
                    "Bad credentials"));
        }
        //获取redis缓存中的key
        String redisKey = getRedisKey(userDetails);
        //判断是否被限制认证
        isAuthenticationLimit(userDetails, redisKey);

        String presentedPassword = authentication.getCredentials().toString();
        if (!getPasswordEncoder().matches(presentedPassword, userDetails.getPassword())) {
            //密码错误时，限制次数计数
            passwordErrorLimitCount(redisKey);

            logger.debug("Authentication " +
                    "failed: password does not match stored value");

            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials",
                    "Bad credentials"));
        }
        //密码校验正确，限制计数重置，缓存删除
        passwordLimitCountReset(redisKey);
    }

    /**
     * 拼接储存在redis中的Key.
     *
     * @param userDetails 用户信息
     * @return key
     */
    public String getRedisKey(UserDetails userDetails) {
        HttpServletRequest request = AuthenticationRequestContextFilter.HTTP_SERVLET_REQUEST.get();
        String ip = UserLoginInfoCollection.getIpAddress(request);
        String redisKey = redisCatalog + ip + "_";
        if (userDetails instanceof CustomUserDetails) {
            String clientIdStr = clientId.get();
            redisKey = redisKey + clientIdStr + "_" + userDetails.getUsername();
            clientId.remove();
        } else {
            clientId.set(userDetails.getUsername());
            redisKey += userDetails.getUsername();
        }
        return redisKey;
    }


    /**
     * 密码错误时，限制认证计数.
     *
     * @param redisKey redis key
     */
    public void passwordErrorLimitCount(String redisKey) {
        Object redisNum = redisTemplate.opsForValue().get(redisKey);
        if (redisNum != null) {
            redisTemplate.opsForValue().increment(redisKey, 1);
        } else {
            redisTemplate.opsForValue().set(redisKey, "1", sysConfigManager.getOauth2AuthenticationLockTime(), TimeUnit.SECONDS);
        }
    }

    /**
     * 密码验证正确时，限制认证技术重置.
     *
     * @param redisKey redis key
     */
    public void passwordLimitCountReset(String redisKey) {
        Object redisNum = redisTemplate.opsForValue().get(redisKey);
        if (redisNum != null) {
            redisTemplate.delete(redisKey);
        }
    }

    /**
     * 判断是否限制认证.
     *
     * @return true 限制认证
     */
    public void isAuthenticationLimit(UserDetails userDetails, String redisKey) {
        Object redisNum = redisTemplate.opsForValue().get(redisKey);
        if (redisNum != null && Integer.valueOf(redisNum.toString()) >= sysConfigManager.getOauth2AuthenticationNum()) {
            String msg = "";
            if (userDetails instanceof CustomUserDetails) {
                msg = "User Authentication is Limited";
            } else {//client
                msg = "Client Authentication is Limited";
            }
            throw new LockedException(msg);
        }
    }

    /**
     * 清空客户端ID
     */
    public static void clearClientInfo() {
        if (clientId.get() != null) {
            clientId.remove();
        }
    }
}

