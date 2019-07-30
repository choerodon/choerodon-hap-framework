/*
 * Copyright Hand China Co.,Ltd.
 */

package io.choerodon.hap.core;

import org.springframework.context.ApplicationContext;

/**
 * @author shengyang.zhou@hand-china.com
 */
public interface AppContextInitListener{
    void contextInitialized(ApplicationContext applicationContext);
}
