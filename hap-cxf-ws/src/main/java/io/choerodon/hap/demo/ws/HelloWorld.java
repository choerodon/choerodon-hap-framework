/*
 * Copyright Hand China Co.,Ltd.
 */

package io.choerodon.hap.demo.ws;

import io.choerodon.hap.account.dto.User;

import javax.jws.WebService;


@WebService  
public interface HelloWorld {  
      
     User sayHello(String name, User user);
      
} 