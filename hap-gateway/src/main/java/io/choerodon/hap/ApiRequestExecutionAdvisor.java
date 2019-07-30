package io.choerodon.hap;

import io.choerodon.hap.api.gateway.controllers.ApiInvokeContoller;
import io.choerodon.hap.api.logs.service.impl.ApiRequestExecutionAdvice;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author jiameng.cao
 * @since 2019/2/28
 */
@Component
public class ApiRequestExecutionAdvisor extends AbstractPointcutAdvisor {
    private static final long serialVersionUID = 1L;

    @Autowired
    private ApiRequestExecutionAdvice apiRequestExecutionAdvice;

    private final StaticMethodMatcherPointcut pointcut = new StaticMethodMatcherPointcut() {

        @Override
        public boolean matches(Method method, Class<?> aClass) {
            if (ApiInvokeContoller.class.isAssignableFrom(aClass)) {
                if ("sentRequest".equals(method.getName())) {
                    return true;
                }
            }
            return false;
        }
    };


    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    public Advice getAdvice() {
        return this.apiRequestExecutionAdvice;
    }
}
