/**
 * Copyright (c) 2016. Hand Enterprise Solution Company. All right reserved. Project
 * Name:hstaffParent Package Name:hstaff.core.aop Date:2016/11/21 0028 Create
 * By:xiangyu.qi@hand-china.com
 */
package io.choerodon.hap.intergration.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.hap.core.AppContextInitListener;
import io.choerodon.hap.intergration.annotation.HapInbound;
import io.choerodon.hap.intergration.annotation.HapOutbound;
import io.choerodon.hap.intergration.beans.HapInvokeInfo;
import io.choerodon.hap.intergration.beans.HapinterfaceBound;
import io.choerodon.hap.intergration.dto.HapInterfaceInbound;
import io.choerodon.hap.intergration.dto.HapInterfaceOutbound;
import io.choerodon.hap.intergration.util.HapInvokeLogUtils;
import io.choerodon.hap.message.components.InvokeLogManager;
import io.choerodon.message.IMessagePublisher;
import io.choerodon.web.dto.ResponseData;
import net.minidev.json.JSONObject;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;

@Aspect
@Component("invokeAspect")
public class HapInvokeAspect implements AppContextInitListener {

    private static final Logger logger = LoggerFactory.getLogger(HapInvokeAspect.class);

    @Autowired
    private IMessagePublisher messagePublisher;

//    @Autowired
//    private IHapInterfaceHeaderService headerService;

    @Autowired
    private ObjectMapper objectMapper;

    private Class interfaceHeaderServiceClazz;

    private Class interfaceHeaderClazz;

    private Object interfaceHeaderServiceImpl;

    @Override
    public void contextInitialized(ApplicationContext applicationContext) {
        try {
            interfaceHeaderServiceClazz = Class.forName("io.choerodon.hap.intergration.service.IHapInterfaceHeaderService");
            if (interfaceHeaderServiceClazz != null) {
                interfaceHeaderClazz = Class.forName("io.choerodon.hap.intergration.dto.HapInterfaceHeader");
                interfaceHeaderServiceImpl = applicationContext.getParentBeanFactory().getBean("hapInterfaceHeaderServiceImpl", interfaceHeaderServiceClazz);
            }
        } catch (Exception e) {
            logger.debug("interface module class not found");
        }
    }


    @Pointcut("@annotation(HapOutbound)")
    public void outboundAspect() {
    }

    /*
     * 出站请求AOP处理
     */
    @Around("@annotation(bound)")
    public Object aroundMethod(ProceedingJoinPoint pjd, HapOutbound bound) throws Throwable {

        Long startTime = System.currentTimeMillis();
        //HapInvokeInfo.REQUEST_START_TIME.set(startTime);
        Object result = null;
        Throwable throwable = null;
       /* HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();*/
        HapInterfaceOutbound outbound = new HapInterfaceOutbound();
        HapInvokeInfo.OUTBOUND.set(outbound);
        try {
            outbound.setRequestTime(new Date());
            Object[] args = pjd.getArgs();
            // 处理请求参数
            String sysName = null;
            String apiName = null;
            for (Object obj : args) {
                if (obj instanceof HttpServletRequest) {
                    sysName = ((HttpServletRequest) obj).getParameter("sysName");
                    apiName = ((HttpServletRequest) obj).getParameter("apiName");
                }
            }
            if (sysName == null && apiName == null) {
                sysName = bound.sysName();
                apiName = bound.apiName();
            }
            outbound.setInterfaceName(sysName + "-" + apiName);

            //interface 模块调用记录
            if (interfaceHeaderServiceClazz != null && interfaceHeaderClazz != null && interfaceHeaderServiceImpl != null) {
                Method getHeaderAndLineMethod = interfaceHeaderServiceClazz.getMethod("getHeaderAndLine", String.class, String.class);
                Object hapInterfaceHeaderObject = getHeaderAndLineMethod.invoke(interfaceHeaderServiceImpl, sysName, apiName);
                if (hapInterfaceHeaderObject != null) {
                    Method getDomainUrl = interfaceHeaderClazz.getMethod("getDomainUrl");
                    Method getIftUrl = interfaceHeaderClazz.getMethod("getIftUrl");
                    outbound.setInterfaceUrl(getDomainUrl.invoke(hapInterfaceHeaderObject).toString() + getIftUrl.invoke(hapInterfaceHeaderObject).toString());
                } else {
                    outbound.setInterfaceUrl(" ");
                }
            }


            result = pjd.proceed();
            if (HapInvokeInfo.OUTBOUND_REQUEST_PARAMETER.get() != null) {
                outbound.setRequestParameter(HapInvokeInfo.OUTBOUND_REQUEST_PARAMETER.get());
            }
            if (HapInvokeInfo.HTTP_RESPONSE_CODE.get() != null)
                outbound.setResponseCode(HapInvokeInfo.HTTP_RESPONSE_CODE.get().toString());
            // 请求成功
            if (HapInvokeInfo.OUTBOUND_RESPONSE_DATA.get() != null) {
                outbound.setResponseContent(HapInvokeInfo.OUTBOUND_RESPONSE_DATA.get());
            } else if (result != null) {
                outbound.setResponseContent(result.toString());
            }
            outbound.setRequestStatus(HapInvokeInfo.REQUEST_SUCESS);

            // 如果同时监听inbound outbound 异常只会被捕捉一次，设置异常
            HapInterfaceInbound inbound = HapInvokeInfo.INBOUND.get();
            if (inbound != null) {
                if (inbound.getStackTrace() != null) {
                    outbound.setStackTrace(inbound.getStackTrace());
                    outbound.setRequestStatus(HapInvokeInfo.REQUEST_FAILURE);
                }
                HapInvokeInfo.INBOUND.remove();
            }

        } catch (Throwable e) {
            throwable = e;
            result = new JSONObject();
            ((JSONObject) result).put("error", e.getMessage());
        } finally {
            outbound.setResponseTime(System.currentTimeMillis() - startTime);
            HapInvokeLogUtils.processExceptionInfo(outbound, throwable);
            HapinterfaceBound hapinterfaceBound = new HapinterfaceBound(outbound);
            messagePublisher.message(InvokeLogManager.CHANNEL_OUTBOUND, hapinterfaceBound);
            //outboundService.outboundInvoke(outbound, throwable);
            HapInvokeInfo.clearOutboundInfo();
        }
        return result;

    }

    /*
     * 入站请求AOP处理
     */
    @Around("@annotation(bound)")
    public Object inaroundMethod(ProceedingJoinPoint pjd, HapInbound bound) throws Throwable {
        Long startTime = System.currentTimeMillis();

        Object result = null;
        Throwable throwable = null;
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        HapInterfaceInbound inbound = new HapInterfaceInbound();
        HapInvokeInfo.INBOUND.set(inbound);
        HapInvokeInfo.TOKEN_TASK_COUNT.set(5);
        try {
            inbound.setRequestTime(new Date());
            inbound.setInterfaceName(bound.apiName());
            result = pjd.proceed();
            if (result != null) {
                String content = "";
                if (result instanceof ResponseData) {
                    content = objectMapper.writeValueAsString(result);

                } else if (result instanceof ModelAndView) {
                    content = result.toString();
                } else if (result instanceof String) {
                    content = (String) result;
                } else {
                    content = result.toString();
                }
                inbound.setResponseContent(content);
            }
            inbound.setRequestStatus(HapInvokeInfo.REQUEST_SUCESS);
            HapInterfaceOutbound outbound = HapInvokeInfo.OUTBOUND.get();
            // 如果同时监听inbound outbound 异常只会被捕捉一次，设置异常
            if (outbound != null) {
                if (outbound.getStackTrace() != null) {
                    inbound.setStackTrace(outbound.getStackTrace());
                    inbound.setRequestStatus(HapInvokeInfo.REQUEST_FAILURE);
                }
                HapInvokeInfo.OUTBOUND.remove();
            }

        } catch (Throwable e) {
            throwable = e;
            //是否是拦截的HapApiController
            if (interfaceHeaderServiceClazz != null && pjd.getTarget().getClass()
                    .getName().equalsIgnoreCase("io.choerodon.hap.intergration.controllers.HapApiController")) {
                result = new JSONObject();
                ((JSONObject) result).put("error", e.getMessage());
            }
        } finally {
            Long endTime = System.currentTimeMillis();
            inbound.setResponseTime(endTime - startTime);
            // 处理一些请求信息
            HapInvokeLogUtils.processRequestInfo(inbound, request);
            HapInvokeLogUtils.processExceptionInfo(inbound, throwable);
            HapinterfaceBound hapinterfaceBound = new HapinterfaceBound(inbound);
            messagePublisher.message(InvokeLogManager.CHANNEL_INBOUND, hapinterfaceBound);
            HapInvokeInfo.TOKEN_TASK_COUNT.remove();
        }
        return result;
    }

}
