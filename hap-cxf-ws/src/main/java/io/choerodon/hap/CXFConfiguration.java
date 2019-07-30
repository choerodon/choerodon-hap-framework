package io.choerodon.hap;

import io.choerodon.hap.intergration.ws.feature.HapInvokeFeature;
import org.apache.cxf.Bus;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import java.util.Collections;

/**
 * CXF 基本配置.
 *
 * @author qiang.zeng
 * @since 2019/3/18.
 */

@ImportResource({
        "classpath:META-INF/cxf/cxf.xml",
        "classpath:META-INF/cxf/cxf-servlet.xml"
})
@Configuration
public class CXFConfiguration implements InitializingBean {
    @Autowired
    private HapInvokeFeature hapInvokeFeature;
    @Autowired
    private Bus bus;

    @Bean
    @SuppressWarnings("unchecked")
    public ServletRegistrationBean CXFServlet() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new CXFServlet());
        registration.setLoadOnStartup(1);
        registration.setAsyncSupported(true);
        registration.addUrlMappings("/ws/*");
        return registration;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        bus.setFeatures(Collections.singletonList(hapInvokeFeature));
    }
}
