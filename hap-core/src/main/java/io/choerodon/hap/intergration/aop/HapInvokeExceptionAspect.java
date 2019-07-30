/**
 * Copyright (c) 2016. Hand Enterprise Solution Company. All right reserved. Project
 * Name:hstaffParent Package Name:hstaff.core.aop Date:2016/11/21 0028 Create
 * By:xiangyu.qi@hand-china.com
 *
 */
package io.choerodon.hap.intergration.aop;

import io.choerodon.hap.core.interceptor.MonitorInterceptor;
import io.choerodon.hap.intergration.annotation.HapInbound;
import io.choerodon.hap.intergration.beans.HapInvokeInfo;
import io.choerodon.hap.intergration.beans.HapinterfaceBound;
import io.choerodon.hap.intergration.dto.HapInterfaceInbound;
import io.choerodon.hap.intergration.util.HapInvokeLogUtils;
import io.choerodon.hap.message.components.InvokeLogManager;
import io.choerodon.message.IMessagePublisher;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;

@Aspect
@Component(value = "invokeExceptionAspect")
public class HapInvokeExceptionAspect {


    @Autowired
    private IMessagePublisher messagePublisher;

    private static final Logger logger = LoggerFactory.getLogger(HapInvokeExceptionAspect.class);

    /*
     * 入站请求异常AOP处理
     */
    @Before("@annotation(exceptionHander)")
    public void beforeMethod(JoinPoint joinpoint, ExceptionHandler exceptionHander) {
        // 这次请求的方法
        HandlerMethod handler = (HandlerMethod) MonitorInterceptor.REST_INVOKE_HANDLER.get();
        if(handler == null) {
            return;
        }
        Method method = handler.getMethod();
        HapInbound hapInbound = method.getAnnotation(HapInbound.class);
        if (hapInbound != null) {
            // 记录错误信息
            Exception e = null;
            Object[] parms = joinpoint.getArgs();
            for (Object obj : parms) {
                if (obj instanceof Exception)
                    e = (Exception) obj;
            }
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                    .getRequest();
            HapInterfaceInbound inbound = new HapInterfaceInbound();
            inbound.setInterfaceName(hapInbound.apiName());
            inbound.setRequestTime(new Date());
            inbound.setRequestStatus(HapInvokeInfo.REQUEST_FAILURE);
            inbound.setResponseTime(0L);
            HapInvokeLogUtils.processRequestInfo(inbound,request);
            HapInvokeLogUtils.processExceptionInfo(inbound,e);
            HapinterfaceBound bound = new HapinterfaceBound(inbound);
            messagePublisher.message(InvokeLogManager.CHANNEL_INBOUND,bound);
        }
    }
}
