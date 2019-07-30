/*
 * #{copyright}#
 */
package io.choerodon.hap.job.mapper;

import java.util.List;

import io.choerodon.hap.job.dto.SchedulerDto;

/**
 *
 * @author shengyang.zhou@hand-china.com
 */
public interface SchedulerMapper {

    SchedulerDto selectByPrimaryKey(SchedulerDto key);

    List<SchedulerDto> selectSchedulers(SchedulerDto example);

}