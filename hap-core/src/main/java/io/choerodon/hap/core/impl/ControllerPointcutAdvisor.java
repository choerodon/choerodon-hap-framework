package io.choerodon.hap.core.impl;

import io.choerodon.hap.core.components.CheckTokenAspect;
import io.choerodon.web.controller.BaseController;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class ControllerPointcutAdvisor extends AbstractPointcutAdvisor {
    @Autowired
    private CheckTokenAspect checkTokenAspect;

    private final StaticMethodMatcherPointcut pointcut = new StaticMethodMatcherPointcut() {

        @Override
        public boolean matches(Method method, Class<?> aClass) {
            if (BaseController.class.isAssignableFrom(aClass)) {
                return true;
            }
            return false;
        }
    };

    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }

    @Override
    public Advice getAdvice() {
        return checkTokenAspect;
    }
}
