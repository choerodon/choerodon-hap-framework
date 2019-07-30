package io.choerodon.hap.security.captcha.impl;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import io.choerodon.hap.security.captcha.ICaptchaManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 验证码生成工具类.
 *
 * @author njq.niu@hand-china.com
 */

@Component(value = "captchaManager")
public class DefaultCaptchaManager implements ICaptchaManager {

    private static final String CAPTCHA_WIDTH = "120";
    private static final String CAPTCHA_HEIGHT = "50";
    private static final String CAPTCHA_CODE_COUNT = "4";
    private static final int CAPTCHA_EXPIRE = 60 * 5;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private String category = "hap:cache:captcha";

    private String captchaKeyName = "captcha_key";

    /**
     * 过期时间,默认5分钟.
     */
    private Integer expire = CAPTCHA_EXPIRE;

    private DefaultKaptcha defaultKaptcha;

    public DefaultCaptchaManager() {
        defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        properties.setProperty(Constants.KAPTCHA_IMAGE_WIDTH, CAPTCHA_WIDTH);
        properties.setProperty(Constants.KAPTCHA_IMAGE_HEIGHT, CAPTCHA_HEIGHT);
        properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_CHAR_LENGTH, CAPTCHA_CODE_COUNT);
        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
    }

    @Override
    public String getCaptchaKeyName() {
        return captchaKeyName;
    }

    public void setCaptchaKeyName(String captchaKeyName) {
        this.captchaKeyName = captchaKeyName;
    }

    /**
     * @return 过期时间, 单位秒
     */
    public Integer getExpire() {
        return expire;
    }

    /**
     * captchaCode 有效期.单位 秒.
     *
     * @param expire 过期时间
     */
    public void setExpire(Integer expire) {
        this.expire = expire;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String generateCaptchaCode() {
        return defaultKaptcha.createText();
    }

    @Override
    public String generateCaptchaKey() {
        return UUID.randomUUID().toString();
    }

    @Override
    public void generateCaptcha(String captchaKey, String captchaCode, OutputStream os) throws IOException {
        redisTemplate.opsForValue().set(getCategory() + ":" + captchaKey, captchaCode, getExpire(), TimeUnit.SECONDS);
        // 将图像输出到输出流中。
        try (OutputStream ignored = os) {
            BufferedImage bi = defaultKaptcha.createImage(captchaCode);
            ImageIO.write(bi, "jpeg", os);
        }
    }

    @Override
    public boolean checkCaptcha(String captchaKey, String captchaCode) {
        if (captchaCode == null) {
            return false;
        }
        final String key = getCategory() + ":" + captchaKey;
        String captchaCodeInRedis = redisTemplate.opsForValue().get(key);
        removeCaptcha(key);
        return captchaCode.equalsIgnoreCase(captchaCodeInRedis);
    }

    @Override
    public void removeCaptcha(String key) {
        redisTemplate.execute((RedisCallback<Object>) connection -> {
            connection.del(key.getBytes());
            return null;
        });

    }

}