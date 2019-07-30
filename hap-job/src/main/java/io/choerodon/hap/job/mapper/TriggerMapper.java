/*
 * #{copyright}#
 */
package io.choerodon.hap.job.mapper;

import java.util.List;

import io.choerodon.hap.job.dto.TriggerDto;

/**
 *
 * @author shengyang.zhou@hand-china.com
 */
public interface TriggerMapper {
    TriggerDto selectByPrimaryKey(TriggerDto key);

    List<TriggerDto> selectTriggers(TriggerDto example);

}