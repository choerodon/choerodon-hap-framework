package io.choerodon.hap.audit.service;


import io.choerodon.hap.core.ProxySelf;

import java.util.List;
import java.util.Map;

/**
 * 审计service.
 *
 * @author xiawang.liu
 */
public interface IAuditService extends ProxySelf<IAuditService> {

    /**
     * 添加审计记录.
     *
     * @param updateMap 修改审计记录条件参数
     * @param insertMap 添加审计记录参数
     */
    void insertAudit(Map<String, Object> updateMap, Map<String, Object> insertMap);

    /**
     * 修改记录标记（是否是最新记录）.
     *
     * @param map 条件参数
     * @return 影响行数
     */
    void updateAuditFlag(Map<String, Object> map);

    /**
     * 获取一次审计操作记录.
     *
     * @param map 条件参数
     * @return 操作记录
     */
    List<Map<String, Object>> selectOperateRecord(Map<String, Object> map);
}
