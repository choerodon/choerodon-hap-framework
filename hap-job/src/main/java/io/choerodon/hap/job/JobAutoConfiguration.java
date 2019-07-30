package io.choerodon.hap.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;

@Configuration
@ComponentScan
public class JobAutoConfiguration {
    @Autowired
    private DataSource dataSource;
    @Autowired
    private AutowiringSpringBeanJobFactory jobFactory;
    @Value("${job.autoStartup:true}")
    private boolean autoStartup;

    @Bean(name = "quartzScheduler")
    public SchedulerFactoryBean quartzScheduler() {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setDataSource(dataSource);
        schedulerFactoryBean.setAutoStartup(autoStartup);
        schedulerFactoryBean.setJobFactory(jobFactory);
        schedulerFactoryBean.setApplicationContextSchedulerContextKey("applicationContext");
        schedulerFactoryBean.setConfigLocation(new ClassPathResource("quartz.properties"));
        return schedulerFactoryBean;
    }
}