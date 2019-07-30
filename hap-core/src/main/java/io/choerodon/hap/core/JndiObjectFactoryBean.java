/*
 * Copyright Hand China Co.,Ltd.
 */

package io.choerodon.hap.core;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.config.PlaceholderConfigurerSupport;

import java.io.IOException;
import java.util.Properties;

/**
 * @author shengyang.zhou@hand-china.com
 * @deprecated
 */
public class JndiObjectFactoryBean extends org.springframework.jndi.JndiObjectFactoryBean {

    @Override
    public void setJndiName(String jndiName) {
        if (jndiName.startsWith(PlaceholderConfigurerSupport.DEFAULT_PLACEHOLDER_PREFIX)) {
            String rawKey = jndiName.substring(2, jndiName.length() - 1);
            String defaultValue = null;
            int idx = rawKey.indexOf(PlaceholderConfigurerSupport.DEFAULT_VALUE_SEPARATOR);
            if (idx != -1) {
                rawKey = StringUtils.trim(rawKey.substring(0, idx));
                defaultValue = StringUtils.trim(rawKey.substring(idx + 1));
            }
            Properties properties = new Properties();
            try {
                properties.load(JndiObjectFactoryBean.class.getResourceAsStream("/hap-default-config.properties"));
                jndiName = properties.getProperty(rawKey);
                if (jndiName == null && defaultValue != null) {
                    jndiName = defaultValue;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.setJndiName(jndiName);
    }

}
