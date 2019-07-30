package io.choerodon.hap.audit.service;

/**
 * translate baseTableName to auditTableName.
 *
 * @author shengyang.zhou@hand-china.com
 */
public interface IAuditTableNameProvider {

    /**
     * @param baseTableName base table name
     * @return audit table name
     */
    String getAuditTableName(String baseTableName);
}
