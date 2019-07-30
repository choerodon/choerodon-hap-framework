package io.choerodon.hap.system.mapper;

import io.choerodon.hap.system.dto.Hotkey;
import io.choerodon.mybatis.common.Mapper;

import java.util.List;

public interface HotkeyMapper extends Mapper<Hotkey> {

    List<Hotkey> queryAll();
}
