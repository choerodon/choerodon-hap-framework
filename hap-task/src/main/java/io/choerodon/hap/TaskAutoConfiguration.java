package io.choerodon.hap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class TaskAutoConfiguration {
    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(5);
        threadPoolTaskExecutor.setMaxPoolSize(250);
        threadPoolTaskExecutor.setQueueCapacity(1000);
        threadPoolTaskExecutor.setKeepAliveSeconds(120);
        return threadPoolTaskExecutor;
    }
}
