package io.choerodon.hap.security.service.impl;

import io.choerodon.hap.core.AppContextInitListener;
import io.choerodon.hap.security.IUserSecurityStrategy;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Qixiangyu
 * @since 2016/12/22.
 */
@Component
public class UserSecurityStrategyManager implements AppContextInitListener {

    List<IUserSecurityStrategy> userSecurityStrategyList = Collections.emptyList();
    @Override
    public void contextInitialized(ApplicationContext applicationContext) {
        userSecurityStrategyList= new ArrayList<>(applicationContext.getBeansOfType(IUserSecurityStrategy.class).values());
        Collections.sort(userSecurityStrategyList);
    }

    public  List<IUserSecurityStrategy> getUserSecurityStrategyList(){
        return userSecurityStrategyList;
    }
}
