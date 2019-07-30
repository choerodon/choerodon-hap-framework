package io.choerodon.hap;

import io.choerodon.hap.api.gateway.service.IApiInvokeService;
import io.choerodon.hap.api.logs.service.impl.ApiInvokeExecutionAdvice;
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
public class ApiInvokeExecutionAdvisor extends AbstractPointcutAdvisor {
    private static final long serialVersionUID = 1L;

    @Autowired
    private ApiInvokeExecutionAdvice apiInvokeExecutionAdvice;

    private final StaticMethodMatcherPointcut pointcut = new StaticMethodMatcherPointcut() {

        @Override
        public boolean matches(Method method, Class<?> aClass) {
            if (IApiInvokeService.class.isAssignableFrom(aClass)) {
                if ("apiInvoke".equals(method.getName())) {
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
        return this.apiInvokeExecutionAdvice;
    }
}
