package io.choerodon.hap;

import io.choerodon.hap.core.interceptor.MonitorInterceptor;
import io.choerodon.hap.message.websocket.DefaultWebSocketHandler;
import io.choerodon.hap.message.websocket.WebSocketHandshakeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * @author qiang.zeng
 * @since 2019/3/7.
 */
@Configuration
@ComponentScan({"**.controllers", "**.adaptor"})
@EnableWebSocket
public class CoreWebMvcConfigurer implements WebSocketConfigurer, WebMvcConfigurer {


    @Autowired
    private MonitorInterceptor monitorInterceptor;
    @Autowired
    private DefaultWebSocketHandler webSocketHandler;
    @Autowired
    private WebSocketHandshakeInterceptor handshakeInterceptor;


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/lib/**", "/resources/**")
                .addResourceLocations("classpath:/lib/", "classpath:/resources/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login.html");
        registry.addViewController("/index");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(monitorInterceptor);
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(webSocketHandler, "/websocket").addInterceptors(handshakeInterceptor).withSockJS();
    }

}
