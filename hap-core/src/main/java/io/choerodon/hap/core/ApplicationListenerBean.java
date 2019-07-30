/*
 * Copyright Hand China Co.,Ltd.
 */

package io.choerodon.hap.core;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author shengyang.zhou@hand-china.com
 */
@Component
public class ApplicationListenerBean implements ApplicationListener {

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            ApplicationContext applicationContext = ((ContextRefreshedEvent) event).getApplicationContext();
            Map<String, AppContextInitListener> beanMap = applicationContext.getBeansOfType(AppContextInitListener.class);
            beanMap.forEach((k, v) -> {
                v.contextInitialized(applicationContext);
            });
        }
    }
}
