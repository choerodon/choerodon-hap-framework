package io.choerodon.hap.system.mapper;

import io.choerodon.hap.system.dto.ParameterConfig;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author qiang.zeng
 * @since 2017/11/6
 */
public interface ParameterConfigMapper extends Mapper<ParameterConfig> {
    /**
     * 查询报表的参数配置.
     *
     * @param reportCode 报表编码
     * @return List<ParameterConfig>
     */
    List<ParameterConfig> selectByReportCode(String reportCode);

    /**
     * 根据参数编码和所属编码Id删除参数配置.
     *
     * @param code     参数编码
     * @param targetId 所属编码Id
     * @return int
     */
    int deleteByCodeAndTargetId(@Param("code") String code, @Param("targetId") Long targetId);

    /**
     * 根据任务Id删除参数.
     *
     * @param taskId 任务Id
     * @return int
     */
    int deleteByTaskId(Long taskId);

    /**
     * 根据参数编码和所属编码Id查询参数配置.
     *
     * @param code     参数编码
     * @param targetId 所属编码Id
     * @return 参数配置集合
     */
    List<ParameterConfig> selectByCodeAndTargetId(@Param("code") String code, @Param("targetId") Long targetId);
}