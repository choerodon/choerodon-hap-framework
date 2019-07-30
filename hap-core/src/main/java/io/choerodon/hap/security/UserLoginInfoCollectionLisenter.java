package io.choerodon.hap.security;

import io.choerodon.hap.system.dto.UserLogin;
import io.choerodon.hap.system.mapper.UserLoginMapper;
import io.choerodon.message.IMessageConsumer;
import io.choerodon.message.annotation.QueueMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author jialong.zuo@hand-china.com
 * @since 2017/12/15.
 */
@Component
@QueueMonitor(queue = "hap:queue:loginInfo")
public class UserLoginInfoCollectionLisenter implements IMessageConsumer<UserLogin> {

    @Autowired
    UserLoginMapper userLoginMapper;

    @Override
    public void onMessage(UserLogin userLogin, String pattern) {
        userLoginMapper.insertSelective(userLogin);

    }
}
