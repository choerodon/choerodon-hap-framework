/*
 * Copyright Hand China Co.,Ltd.
 */

package io.choerodon.hap.demo.ws;

import io.choerodon.hap.account.dto.User;
import org.springframework.stereotype.Component;

import javax.jws.WebService;

@WebService(endpointInterface = "io.choerodon.hap.demo.ws.HelloWorld", serviceName = "HelloGT")
@Component(value = "service1")
public class HelloWorldImpl implements HelloWorld {

    @Override
    public User sayHello(String name, User user) {
        System.out.println(user.getUserName());

        user.setEmail("test@hand.com");
        return user;
    }

}  