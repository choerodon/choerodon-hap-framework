/*
 * #{copyright}#
 */

package io.choerodon.hap.audit.service.impl;


import io.choerodon.hap.audit.service.IAuditTableNameProvider;

/**
 * default impl, add '_a' suffix to baseTableName.
 *
 * @author shengyang.zhou@hand-china.com
 */
public class DefaultAuditTableNameProvider implements IAuditTableNameProvider {

    public static DefaultAuditTableNameProvider instance = new DefaultAuditTableNameProvider();

    private DefaultAuditTableNameProvider() {
    }

    @Override
    public String getAuditTableName(String baseTableName) {
        return baseTableName + "_a";
    }
}
