package io.choerodon.hap.system.service;

import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.system.dto.ParameterConfig;
import io.choerodon.mybatis.service.IBaseService;

import java.util.List;

/**
 * @author qiang.zeng
 * @since 2017/11/6
 */
public interface IParameterConfigService extends IBaseService<ParameterConfig>, ProxySelf<IParameterConfigService> {
    /**
     * 查询报表的参数配置
     *
     * @param reportCode 报表编码
     * @return List<ParameterConfig>
     */
    List<ParameterConfig> selectByReportCode(String reportCode);

    /**
     * 根据参数编码和所属编码ID查询参数配置.
     *
     * @param code     参数编码
     * @param targetId 所属编码ID
     * @return 参数配置集合
     */
    List<ParameterConfig> selectByCodeAndTargetId(String code, Long targetId);
}
