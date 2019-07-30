/*
 * #{copyright}#
 */
package io.choerodon.hap.job.mapper;

import io.choerodon.hap.job.dto.SimpleTriggerDto;

/**
 *
 * @author shengyang.zhou@hand-china.com
 */
public interface SimpleTriggerMapper {

    SimpleTriggerDto selectByPrimaryKey(SimpleTriggerDto key);
}