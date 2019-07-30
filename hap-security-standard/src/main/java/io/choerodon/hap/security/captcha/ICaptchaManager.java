package io.choerodon.hap.security.captcha;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 验证码接口.
 * 生成验证码图片以及captchaKey等.
 *
 * @author njq.niu@hand-china.com
 * @since 2016/1/23
 */
public interface ICaptchaManager {

    /**
     * 获取CaptchaKeyName.
     *
     * @return keyName
     */
    String getCaptchaKeyName();

    /**
     * 获取验证码code.
     *
     * @return captchaCode
     */
    String generateCaptchaCode();

    /**
     * 生成CaptchaKey.
     *
     * @return captchaKey
     */
    String generateCaptchaKey();

    /**
     * 根据 code 生成图片数据,写入指定的输出流. <br>
     * 同时验证信息放入 redis
     *
     * @param captchaKey  放入 redis 需要用的 key(部分)
     * @param captchaCode 生成的 code
     * @param os          OutputStream输出流
     * @throws IOException IOException
     */
    void generateCaptcha(String captchaKey, String captchaCode, OutputStream os) throws IOException;

    /**
     * 校验验证码的值.
     *
     * @param captchaKey  cookie 中的 captchaKey
     * @param captchaCode 用户输入的验证码
     * @return 验证是否成功
     */
    boolean checkCaptcha(String captchaKey, String captchaCode);

    /**
     * 移除验证码.
     *
     * @param captchaKey 验证码key
     */
    void removeCaptcha(String captchaKey);
}
