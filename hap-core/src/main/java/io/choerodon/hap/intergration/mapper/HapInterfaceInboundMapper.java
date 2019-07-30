package io.choerodon.hap.intergration.mapper;

import io.choerodon.hap.intergration.dto.HapInterfaceInbound;
import io.choerodon.mybatis.common.Mapper;

import java.util.List;

public interface HapInterfaceInboundMapper extends Mapper<HapInterfaceInbound> {

    List<HapInterfaceInbound> select (HapInterfaceInbound inbound);
}