package io.choerodon.hap.audit.service.impl;

import io.choerodon.hap.audit.mapper.AuditMapper;
import io.choerodon.hap.audit.service.IAuditLogService;
import io.choerodon.hap.audit.service.IAuditService;
import io.choerodon.hap.core.util.IDGenerator;
import io.choerodon.base.helper.ApplicationContextHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 审计serviceImpl.
 *
 * @author xiawang.liu
 */
@Service
public class AuditServiceImpl implements IAuditService {

    private Logger logger = LoggerFactory.getLogger(AuditServiceImpl.class);

    @Value("${db.type}")
    private String dataSourceType;

    @Autowired
    private AuditMapper auditMapper;

    @Autowired(required = false)
    private IAuditLogService auditLogService;
    /**
     * 审计异常
     */
    private final String MSG_AUDIT_ERROR = "Audit Failures";

    @Override
    @Transactional(propagation = Propagation.NESTED, rollbackFor = Exception.class)
    public void insertAudit(Map<String, Object> updateMap, Map<String, Object> insertMap) {
        try {
            updateAuditFlag(updateMap);
            // 获取操作的记录
            List<Map<String, Object>> records = selectOperateRecord(insertMap);
            List<String> cols = (List<String>) insertMap.get("_COLS");
            if (!records.isEmpty()) {
                for (Map<String, Object> dto : records) {
                    for (String field : cols) {
                        Object obj = dto.get(field) == null ? dto.get(field.toUpperCase()) : dto.get(field);
                        if(dataSourceType.equalsIgnoreCase("mssql")) {
                            insertMap.put(field, obj == null ? "" : obj);
                        } else {
                            insertMap.put(field, obj);
                        }
                    }
                    insertMap.put("_AUDIT_ID", IDGenerator.getInstance().generate());

                    auditMapper.auditInsert(insertMap);
                }
            }
        } catch (Exception e) {
            if (auditLogService == null) {
                auditLogService = ApplicationContextHelper.getApplicationContext().getBean(IAuditLogService.class);
            }
            if (auditLogService != null) {
                Map<String, Object> auditLog = new HashMap<>(5);
                auditLog.put("operator", insertMap.get("_LAST_UPDATED_BY"));
                auditLog.put("message", e.getMessage());
                auditLog.put("sql", insertMap.get("sql"));
                auditLogService.auditlog(auditLog);
            }
            logger.error(MSG_AUDIT_ERROR);
            logger.error(e.getMessage());
            throw e;
        }
    }

    @Override
    public void updateAuditFlag(Map<String, Object> map) {
        // 将其他审计记录标记 AUDIT_TAG设为0
        if(dataSourceType.equalsIgnoreCase("mysql")
                || dataSourceType.equalsIgnoreCase("oracle")) {
            auditMapper.auditUpdateTag(map);
        } else if(dataSourceType.equalsIgnoreCase("mssql")) {
            auditMapper.auditUpdateTagSqlserver(map);
        }
    }

    @Override
    public List<Map<String, Object>> selectOperateRecord(Map<String, Object> map) {
        return auditMapper.selectOperateRecord(map);
    }

}
