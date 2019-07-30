package io.choerodon.hap.intergration.mapper;

import io.choerodon.hap.intergration.dto.HapInterfaceOutbound;
import io.choerodon.mybatis.common.Mapper;

import java.util.List;

public interface HapInterfaceOutboundMapper extends Mapper<HapInterfaceOutbound> {

    List<HapInterfaceOutbound> select (HapInterfaceOutbound outbound);
}