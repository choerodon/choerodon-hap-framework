package io.choerodon.hap.core.components;

import io.choerodon.hap.message.profile.SystemConfigListener;
import io.choerodon.base.util.BaseConstants;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class CaptchaConfig implements SystemConfigListener, BaseConstants {

    public static final String LOGIN_KEY = "loginKey";

    private static final int CAPTCHA_EXPIRE = 60 * 60;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private String redisCatalog = HAP_CACHE + "login:";


    /**
     * 是否开启验证码
     */
    private boolean enableCaptcha = true;

    /**
     * 失败几次后开启验证码 0表示不开启
     */
    private Integer wrongTimes = 0;

    /**
     * 登录失败过期时间,一天.
     */
    private Integer expire = CAPTCHA_EXPIRE;

    /**
     * 登录失败次数 有效期.单位 秒.
     *
     * @param expire 过期时间
     */
    public void setExpire(Integer expire) {
        this.expire = expire;
    }

    public Integer getExpire() {
        return this.expire;
    }


    @Override
    public List<String> getAcceptedProfiles() {
        return Collections.singletonList("CAPTCHA");
    }

    @Override
    public void updateProfile(String configCode, String configValue) {
        if ("DISABLE".equalsIgnoreCase(configValue)) {
            this.enableCaptcha = false;
            this.wrongTimes = 0;
        } else if ("ENABLE".equalsIgnoreCase(configValue)) {
            this.enableCaptcha = true;
        } else if ("AFTER3".equalsIgnoreCase(configValue)) {
            this.enableCaptcha = false;
            this.wrongTimes = 3;
        }
    }


    public Integer getWrongTimes() {
        return wrongTimes;
    }

    /**
     * 更新登录错误次数
     * 初始化登录出错次数 为3
     */
    public void updateLoginFailureInfo(Cookie cookie) {

        if (cookie != null) {
            String failureTimes = redisTemplate.opsForValue().get(redisCatalog + cookie.getValue());
            if (failureTimes == null) {
                redisTemplate.opsForValue().set(redisCatalog + cookie.getValue(), this.wrongTimes.toString(), expire, TimeUnit.SECONDS);
            } else {
                Integer times = Integer.parseInt(failureTimes);
                if (times < this.wrongTimes) {
                    redisTemplate.opsForValue().increment(redisCatalog + cookie.getValue(), 1);
                }
            }
        }
    }

    /**
     * 从redis中 重置这次 登录失败记录及过期时间
     */
    public void resetLoginFailureInfo(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = WebUtils.getCookie(request, LOGIN_KEY);
        if (cookie != null) {
            cookie.setMaxAge(this.expire);
            cookie.setPath(StringUtils.defaultIfEmpty(request.getContextPath(), "/"));
            response.addCookie(cookie);
            redisTemplate.opsForValue().set(redisCatalog + cookie.getValue(), "0", expire, TimeUnit.SECONDS);
        }
    }

    /**
     * 是否开启验证码
     */
    public boolean isEnableCaptcha(Cookie cookie) {
        boolean enable = this.enableCaptcha;
        if (!this.enableCaptcha && this.wrongTimes > 0) {
            if (cookie != null) {
                String failureTimes = redisTemplate.opsForValue().get(redisCatalog + cookie.getValue());
                if (failureTimes != null) {
                    Integer times = Integer.parseInt(failureTimes);
                    if (times.equals(wrongTimes)) {
                        enable = true;
                    }
                } else {
                    //有cookie 但是redis中没有记录 说明是伪造的cookie 直接开启验证码
                    enable = true;
                }
            }
        }
        return enable;
    }
}
