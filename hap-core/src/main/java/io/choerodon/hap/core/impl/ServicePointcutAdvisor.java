package io.choerodon.hap.core.impl;

import io.choerodon.mybatis.service.BaseServiceImpl;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class ServicePointcutAdvisor extends AbstractPointcutAdvisor {

    @Autowired
    private ServiceExecutionAdvice serviceExecutionAdvice;

    private final StaticMethodMatcherPointcut pointcut = new StaticMethodMatcherPointcut() {

        @Override
        public boolean matches(Method method, Class<?> aClass) {
            if (BaseServiceImpl.class.isAssignableFrom(aClass)) {
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
        return serviceExecutionAdvice;
    }

}
