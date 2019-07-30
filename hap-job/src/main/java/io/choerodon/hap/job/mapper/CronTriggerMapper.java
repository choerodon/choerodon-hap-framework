/*
 * #{copyright}#
 */
package io.choerodon.hap.job.mapper;

import io.choerodon.hap.job.dto.CronTriggerDto;

/**
 * @author shengyang.zhou@hand-china.com
 */
public interface CronTriggerMapper {
    CronTriggerDto selectByPrimaryKey(CronTriggerDto key);
}