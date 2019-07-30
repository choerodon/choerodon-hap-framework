package io.choerodon.hap.demo.ws;

import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.xml.ws.Endpoint;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * CXF 发布 WebService示例.
 *
 * @author qiang.zeng
 * @since 2019/3/18.
 */
@Component
public class CXFBeansDemoConfiguration {
    @Autowired
    private HelloWorld helloWorld;
    @Autowired
    private ServerPasswordCallback serverPasswordCallback;

    @Bean(name = "serverInInterceptor")
    public WSS4JInInterceptor serverInInterceptor() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("action", "UsernameToken");
        properties.put("passwordType", "PasswordText");
        properties.put("passwordCallbackRef", serverPasswordCallback);
        return new WSS4JInInterceptor(properties);
    }

    @Bean(name = "hello")
    public Endpoint endpoint() {
        EndpointImpl endpoint = new EndpointImpl(helloWorld);
        endpoint.setAddress("/HelloWorldService");
        endpoint.setInInterceptors(Collections.singletonList(serverInInterceptor()));
        endpoint.publish();
        return endpoint;
    }

}
