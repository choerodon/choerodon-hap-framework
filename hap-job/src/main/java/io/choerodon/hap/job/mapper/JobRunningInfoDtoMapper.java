/*
 * #{copyright}#
 */
package io.choerodon.hap.job.mapper;

import io.choerodon.hap.job.dto.JobRunningInfoDto;
import io.choerodon.mybatis.common.Mapper;

/**
 *
 * @author liyan.shi@hand-china.com
 */
public interface JobRunningInfoDtoMapper extends Mapper<JobRunningInfoDto> {

    void deleteByNameGroup(JobRunningInfoDto example);

}