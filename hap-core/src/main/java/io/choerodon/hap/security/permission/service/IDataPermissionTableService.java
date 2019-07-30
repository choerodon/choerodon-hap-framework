package io.choerodon.hap.security.permission.service;

import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.security.permission.dto.DataPermissionTable;
import io.choerodon.mybatis.service.IBaseService;

import java.util.List;

/**
 * @author jialong.zuo@hand-china.com on 2017/12/8
 */
public interface IDataPermissionTableService extends IBaseService<DataPermissionTable>, ProxySelf<IDataPermissionTableService> {

    /**通过规则删除表
     * @param dataMaskTables 将要删除的表规则
     */
    void removeTableWithRule(List<DataPermissionTable> dataMaskTables);
}