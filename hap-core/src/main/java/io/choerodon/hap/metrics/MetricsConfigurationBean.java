/**
 * Copyright (c) 2016. Hand Enterprise Solution Company. All right reserved. Project
 * Name:hstaffParent Package Name:hstaff.core.beans Date:2016/7/28 0028 Create
 * By:zongyun.zhou@hand-china.com
 *
 */
package io.choerodon.hap.metrics;

import java.lang.management.ManagementFactory;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import com.codahale.metrics.servlet.InstrumentedFilter;
import com.codahale.metrics.servlets.HealthCheckServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.jvm.*;
import com.codahale.metrics.servlets.MetricsServlet;
import com.ryantenney.metrics.spring.config.annotation.MetricsConfigurerAdapter;
import org.springframework.stereotype.Component;

@Component
public class MetricsConfigurationBean extends MetricsConfigurerAdapter {
  private static final String PROP_METRIC_REG_JVM_MEMORY = "jvm.memory";
  private static final String PROP_METRIC_REG_JVM_GARBAGE = "jvm.garbage";
  private static final String PROP_METRIC_REG_JVM_THREADS = "jvm.threads";
  private static final String PROP_METRIC_REG_JVM_FILES = "jvm.files";
  private static final String PROP_METRIC_REG_JVM_BUFFERS = "jvm.buffers";
  private final Logger log = LoggerFactory.getLogger(MetricsConfigurationBean.class);

  private MetricRegistry metricRegistry = new MetricRegistry();
  private HealthCheckRegistry healthCheckRegistry = new HealthCheckRegistry();
//  @Value("${metric.reportPeriod}")
//  private String reportPeriod;
  @Bean
  public MetricRegistry getMetricRegistry() {
    return metricRegistry;
  }
  @Bean
  public HealthCheckRegistry getHealthCheckRegistry() {
    return healthCheckRegistry;
  }
  @Autowired
  private ServletContext context;

  @PostConstruct
  public void init() {
    log.debug("Registering JVM gauges");
    metricRegistry.register(PROP_METRIC_REG_JVM_MEMORY, new MemoryUsageGaugeSet());
    metricRegistry.register(PROP_METRIC_REG_JVM_GARBAGE, new GarbageCollectorMetricSet());
    metricRegistry.register(PROP_METRIC_REG_JVM_THREADS, new ThreadStatesGaugeSet());
    metricRegistry.register(PROP_METRIC_REG_JVM_FILES, new FileDescriptorRatioGauge());
    metricRegistry.register(PROP_METRIC_REG_JVM_BUFFERS,new BufferPoolMetricSet(ManagementFactory.getPlatformMBeanServer()));

   /* log.debug("Initializing Metrics JMX reporting");
    JmxReporter jmxReporter = JmxReporter.forRegistry(metricRegistry).build();
    jmxReporter.start();*/

    context.setAttribute(MetricsServlet.METRICS_REGISTRY,metricRegistry);
    context.setAttribute(InstrumentedFilter.REGISTRY_ATTRIBUTE, metricRegistry);
    context.setAttribute(HealthCheckServlet.HEALTH_CHECK_REGISTRY,healthCheckRegistry);
    
    // 这里注释掉将来使用采集reporter替代
    // log.info("Initializing Metrics Log reporting");
//    final ConsoleReporter console = ConsoleReporter.forRegistry(metricRegistry)
//    .convertRatesTo(TimeUnit.SECONDS).convertDurationsTo(TimeUnit.MILLISECONDS).build();
//    console.start(10, TimeUnit.SECONDS);

    //    log.info("Initializing Metrics Log reporting");
    //    final Slf4jReporter reporter =
    //        Slf4jReporter.forRegistry(metricRegistry).outputTo(LoggerFactory.getLogger(this.getClass()))
    //            .convertRatesTo(TimeUnit.SECONDS).convertDurationsTo(TimeUnit.MILLISECONDS).build();
    //    reporter.start(60, TimeUnit.SECONDS);

  }
}
