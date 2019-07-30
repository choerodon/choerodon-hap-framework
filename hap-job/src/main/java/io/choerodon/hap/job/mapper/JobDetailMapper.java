/*
 * #{copyright}#
 */

package io.choerodon.hap.job.mapper;

import java.util.List;

import io.choerodon.hap.job.dto.JobDetailDto;
import io.choerodon.hap.job.dto.JobInfoDetailDto;

/**
 *
 * @author shengyang.zhou@hand-china.com
 */
public interface JobDetailMapper {
    JobDetailDto selectByPrimaryKey(JobDetailDto key);

    List<JobDetailDto> selectJobDetails(JobDetailDto example);

    List<JobInfoDetailDto> selectJobInfoDetails(JobDetailDto example);
}