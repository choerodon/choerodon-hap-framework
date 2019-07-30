package io.choerodon.hap.audit.service;

import io.choerodon.mybatis.entity.BaseDTO;
import io.choerodon.web.core.IRequest;

import java.util.List;
import java.util.Map;

/**
 * 审计记录接口.
 *
 * @author lijian.yin@hand-china.com
 */
public interface IAuditRecordService {

    /**
     * 审计的DTO记录列表
     *
     * @param iRequest IRequest
     * @param dto      审计的DTO
     * @param page     页码
     * @param pageSize 每页显示数量
     * @return DTO列表
     */
    List<Map<String, Object>> selectAuditRecord(IRequest iRequest, BaseDTO dto, int page, int pageSize);

    /**
     * 审计DTO某一数据的审计记录.
     *
     * @param iRequest IRequest
     * @param dto      审计的DTO
     * @param page     页码
     * @param pageSize 每页显示数量
     * @return 审计记录
     */
    List selectAuditRecordDetail(IRequest iRequest, BaseDTO dto, int page, int pageSize);
}
