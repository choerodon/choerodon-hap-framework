package io.choerodon.hap.audit.mapper;

import io.choerodon.hap.audit.dto.Audit;
import io.choerodon.mybatis.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 审计Mapper.
 *
 * @author lijian.yin
 * @since 2017/9/25.
 */
public interface AuditMapper extends Mapper<Audit> {

    /**
     * 添加审计记录.
     *
     * @param map 参数
     * @return 影响行数
     */
    int auditInsert(Map<String, Object> map);

    /**
     * 修改审计记录（是否是最新记录）.
     * oracle/mysql
     *
     * @param map 审计记录
     * @return 影响行数
     */
    int auditUpdateTag(Map<String, Object> map);

    /**
     * 修改记录标记（是否是最新记录）.
     * sqlServer
     *
     * @param map 审计记录
     * @return 影响行数
     */
    void auditUpdateTagSqlserver(Map<String, Object> map);

    /**
     * 获取操作的记录
     *
     * @param parameterMap 条件参数
     * @return 操作记录集合
     */
    List<Map<String, Object>> selectOperateRecord(Map<String, Object> parameterMap);

}
