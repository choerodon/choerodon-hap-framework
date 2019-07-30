package io.choerodon.hap.report.mapper;

import io.choerodon.mybatis.common.Mapper;
import io.choerodon.hap.report.dto.ReportFiles;

/**
 * 报表文件Mapper.
 *
 * @author qiang.zeng
 * @since 2017/9/21
 */
public interface ReportFilesMapper extends Mapper<ReportFiles> {
    /**
     * 查询报表文件.
     *
     * @param name 报表名称
     * @return 报表文件
     */
    ReportFiles selectByName(String name);

    /**
     * 删除报表文件.
     *
     * @param name 报表名称
     * @return int
     */
    int deleteByName(String name);

    /**
     * 查询报表所有参数.
     *
     * @param reportCode 报表编码
     * @return 报表文件
     */
    ReportFiles selectReportFileParams(String reportCode);
}