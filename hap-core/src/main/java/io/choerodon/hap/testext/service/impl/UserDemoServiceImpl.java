package io.choerodon.hap.testext.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.mybatis.service.BaseServiceImpl;
import io.choerodon.hap.testext.dto.UserDemo;
import io.choerodon.hap.testext.service.IUserDemoService;

@Service
@Transactional
public class UserDemoServiceImpl extends BaseServiceImpl<UserDemo> implements IUserDemoService {

}