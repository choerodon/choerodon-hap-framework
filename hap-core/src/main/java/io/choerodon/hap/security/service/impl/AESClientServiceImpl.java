package io.choerodon.hap.security.service.impl;

import io.choerodon.hap.security.service.IAESClientService;
import org.springframework.stereotype.Service;

/**
 * @author shengyang.zhou@hand-china.com
 */
@Service
public class AESClientServiceImpl implements IAESClientService {

    @Override
    public String encrypt(String password) {
        return password;
    }

    @Override
    public String decrypt(String password) {
        return password;
    }
}
