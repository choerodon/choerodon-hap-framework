/**
 * Copyright (c) 2016. Hand Enterprise Solution Company. All right reserved. Project
 * Name:hstaffParent Package Name:hstaff.core.aop Date:2016/7/28 0028 Create
 * By:zongyun.zhou@hand-china.com
 *
 */
package io.choerodon.hap.metrics;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.codahale.metrics.Timer;



@Aspect
@Component("metricAspect")
public class MetricAspect {
  @Autowired
  MetricsConfigurationBean metricsConfigurationBean;
  private Timer.Context context = null;
  private static final Logger logger = LoggerFactory.getLogger(MetricAspect.class);

  @Pointcut("@annotation(com.codahale.metrics.annotation.Timed)")
  public void timedAspect() {}

  @Before("timedAspect()")
  public void doBefore(JoinPoint joinPoint) {
    HttpServletRequest request =
        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    context = metricsConfigurationBean.getMetricRegistry().timer(request.getRequestURI()).time();
  }

  @After("timedAspect()")
  public void doAfter(JoinPoint joinPoint) {
    context.stop();
  }
}
