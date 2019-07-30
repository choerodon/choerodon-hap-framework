package io.choerodon.hap.security.permission.service.impl;

import io.choerodon.hap.security.permission.dto.DataPermissionTable;
import io.choerodon.message.IMessageConsumer;
import io.choerodon.message.annotation.TopicMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author jialong.zuo@hand-china.com
 * @since 2017/9/13.
 */
@Service
@TopicMonitor(channel = {"dataPermission.tableRemove"})
public class DataPermissionTableListener implements IMessageConsumer<DataPermissionTable> {

    @Autowired
    DataPermissionCacheContainer container;

    @Override
    public void onMessage(DataPermissionTable message, String pattern) {

        container.removeMaskTableRuleMap(message.getTableName());
    }
}
