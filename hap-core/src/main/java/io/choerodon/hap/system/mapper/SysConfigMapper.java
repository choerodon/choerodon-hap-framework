package io.choerodon.hap.system.mapper;

import io.choerodon.hap.system.dto.SysConfig;
import io.choerodon.mybatis.common.Mapper;

/**
 * @author hailin.xu@hand-china.com
 */
public interface SysConfigMapper extends Mapper<SysConfig> {

    SysConfig selectByCode(String configCode);
}
