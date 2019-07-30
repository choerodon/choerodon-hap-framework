package io.choerodon.hap.mail.service;

import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.mail.dto.MessageReceiver;
import io.choerodon.mybatis.service.IBaseService;

/**
 * @author jiameng.cao
 * @since 2018/12/14
 */
public interface IMessageReceiverService extends IBaseService<MessageReceiver>, ProxySelf<IMessageReceiverService> {
}
