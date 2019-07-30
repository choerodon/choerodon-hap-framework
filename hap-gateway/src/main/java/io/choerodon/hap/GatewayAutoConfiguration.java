package io.choerodon.hap;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
@AutoConfigureBefore(CoreAutoConfiguration.class) //OAuth2AutoConfiguration 内有对于/**的filter，需要先执行 GatewayAutoConfiguration
public class GatewayAutoConfiguration {

}
