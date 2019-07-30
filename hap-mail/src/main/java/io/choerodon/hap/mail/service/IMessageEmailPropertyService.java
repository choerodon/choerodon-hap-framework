package io.choerodon.hap.mail.service;

import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.mail.dto.MessageEmailProperty;
import io.choerodon.mybatis.service.IBaseService;

/**
 * 邮件服务器属性服务接口.
 *
 * @author qiang.zeng@hand-china.com
 */
public interface IMessageEmailPropertyService extends IBaseService<MessageEmailProperty>, ProxySelf<IMessageEmailPropertyService> {
    /**
     * 根据邮件配置Id删除邮件服务器属性.
     *
     * @param obj 邮件服务器属性
     * @return int
     */
    int deleteByConfigId(MessageEmailProperty obj);
}