/*
 * #{copyright}#
 */
package io.choerodon.hap.system.controllers;

import io.choerodon.hap.security.captcha.ICaptchaManager;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 验证码生成.
 *
 * @author wuyichu
 * @author njq.niu@hand-china.com
 */
@Controller
public class CaptchaController {

    private final Logger log = LoggerFactory.getLogger(CaptchaController.class);

    @Autowired
    private ICaptchaManager captchaManager;

    /**
     * 通过点击生成验证码.
     *
     * @param req  HttpServletRequest
     * @param resp HttpServletResponse
     */
    @Permission(type = ResourceType.SITE, permissionPublic = true)
    @RequestMapping("/verifiCode")
    public void code(HttpServletRequest req, HttpServletResponse resp) {
        try {
            // 禁止图像缓存。
            resp.setHeader("Pragma", "no-cache");
            resp.setHeader("Cache-Control", "no-cache");
            resp.setDateHeader("Expires", 0);
            resp.setContentType("image/jpeg");

            String captchaKey = captchaManager.generateCaptchaKey();
            Cookie cookie = new Cookie(captchaManager.getCaptchaKeyName(), captchaKey);
            cookie.setPath(StringUtils.defaultIfEmpty(req.getContextPath(), "/"));
            cookie.setMaxAge(-1);
            resp.addCookie(cookie);

            captchaManager.generateCaptcha(captchaKey, captchaManager.generateCaptchaCode(), resp.getOutputStream());

        } catch (IOException e) {
            if (log.isErrorEnabled()) {
                log.error("生成验证码失败", e);
            }
        }
    }
}
