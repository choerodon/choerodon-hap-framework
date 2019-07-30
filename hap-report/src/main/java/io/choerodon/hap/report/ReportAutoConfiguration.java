package io.choerodon.hap.report;

import com.bstek.ureport.console.UReportServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ComponentScan
@ImportResource("classpath:ureport-console-context.xml")
public class ReportAutoConfiguration {
    @Bean
    public ServletRegistrationBean ureportSerlet() {
        ServletRegistrationBean registration = new ServletRegistrationBean();
        registration.setServlet(new UReportServlet());
        registration.addUrlMappings("/ureport/*");
        registration.setName("ureportSerlet");
        return registration;
    }
}
