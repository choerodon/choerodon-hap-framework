package io.choerodon.hap.security;

import io.choerodon.hap.message.profile.SystemConfigListener;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.util.StringUtil;

import java.util.Arrays;
import java.util.List;

/**
 * @author njq.niu@hand-china.com
 * @author xiangyu.qi@hand-china.com
 * @since 2016/1/31
 * @since 2016/10/10
 */
@Component
public class PasswordManager implements PasswordEncoder, InitializingBean, SystemConfigListener {

    public static final String PASSWORD_COMPLEXITY_NO_LIMIT = "NO_LIMIT";
    public static final String PASSWORD_COMPLEXITY_DIGITS_AND_LETTERS = "DIGITS_AND_LETTERS";
    public static final String PASSWORD_COMPLEXITY_DIGITS_AND_CASE_LETTERS = "DIGITS_AND_CASE_LETTERS";

    private PasswordEncoder delegate;

    private String siteWideSecret = "Zxa1pO6S6uvBMlY";

    private String defaultPassword = "123456";

    /**
     * 密码失效时间 默认0 不失效
     */
    private Integer passwordInvalidTime = 0;

    /**
     * 密码长度
     */
    private Integer passwordMinLength = 8;

    /**
     * 密码复杂度
     */
    private String passwordComplexity = "no_limit";

    public Integer getPasswordInvalidTime() {
        return passwordInvalidTime;
    }

    public Integer getPasswordMinLength() {
        return passwordMinLength;
    }


    public String getPasswordComplexity() {
        return passwordComplexity;
    }


    public String getDefaultPassword() {
        return defaultPassword;
    }


    public String getSiteWideSecret() {
        return siteWideSecret;
    }

    public void setSiteWideSecret(String siteWideSecret) {
        this.siteWideSecret = siteWideSecret;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        delegate = new StandardPasswordEncoder(siteWideSecret);
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return delegate.encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (StringUtil.isEmpty(encodedPassword)) {
            return false;
        }
        return delegate.matches(rawPassword, encodedPassword);
    }

    @Override
    public List<String> getAcceptedProfiles() {
        return Arrays.asList("DEFAULT_PASSWORD", "PASSWORD_INVALID_TIME", "PASSWORD_MIN_LENGTH", "PASSWORD_COMPLEXITY");
    }

    @Override
    public void updateProfile(String profileName, String profileValue) {
        if ("PASSWORD_INVALID_TIME".equalsIgnoreCase(profileName)) {
            this.passwordInvalidTime = Integer.parseInt(profileValue);
        } else if ("PASSWORD_MIN_LENGTH".equalsIgnoreCase(profileName)) {
            this.passwordMinLength = Integer.parseInt(profileValue);
        } else if ("PASSWORD_COMPLEXITY".equalsIgnoreCase(profileName)) {
            this.passwordComplexity = profileValue;
        } else if ("DEFAULT_PASSWORD".equalsIgnoreCase(profileName)) {
            this.defaultPassword = profileValue;
        }
    }
}
