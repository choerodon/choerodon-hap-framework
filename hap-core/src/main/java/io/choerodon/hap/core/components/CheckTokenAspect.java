package io.choerodon.hap.core.components;

import io.choerodon.hap.core.annotation.CheckToken;
import io.choerodon.hap.core.exception.TokenException;
import io.choerodon.hap.core.interceptor.SecurityTokenInterceptor;
import io.choerodon.hap.security.TokenUtils;
import io.choerodon.hap.system.dto.DTOStatus;
import io.choerodon.mybatis.entity.BaseDTO;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Collection;

/**
 * 执行更新时，在controller层校验BaseDTO的防篡改token.
 *
 * @author qiang.zeng@hand-china.com
 * @since 2018/6/7
 */
@Component
public class CheckTokenAspect implements MethodInterceptor {

    /**
     * DTO update时，是否进行token校验.
     * 默认校验
     */
    @Value("${sys.security.token.validate:true}")
    private boolean sysSecurityTokenValidate;

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        before(methodInvocation.getMethod(), methodInvocation.getArguments());
        return methodInvocation.proceed();
    }

    private void before(Method method, Object[] args) throws TokenException {
        CheckToken checkToken = AnnotationUtils.findAnnotation(method, CheckToken.class);
        if (checkToken != null && !checkToken.check()) {
            return;
        }
        if (sysSecurityTokenValidate) {
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof BaseDTO) {
                    checkToken(args[i]);
                } else if (args[i] instanceof Collection) {
                    for (Object o : (Collection) args[i]) {
                        if (o instanceof BaseDTO) {
                            checkToken(o);
                        }
                    }
                }
            }
        }
    }

    /**
     * BaseDTO 防篡改Token校验.
     *
     * @param obj Object
     * @throws TokenException token校验异常
     */
    private void checkToken(Object obj) throws TokenException {
        BaseDTO baseDto = (BaseDTO) obj;
        boolean check = DTOStatus.UPDATE.equals(baseDto.get__status());
        if (check) {
            TokenUtils.checkToken(SecurityTokenInterceptor.LOCAL_SECURITY_KEY.get(), baseDto);
        }
    }
}
