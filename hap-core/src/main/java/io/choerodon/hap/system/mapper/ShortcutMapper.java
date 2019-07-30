package io.choerodon.hap.system.mapper;

import io.choerodon.hap.system.dto.Shortcut;
import io.choerodon.mybatis.common.Mapper;

import java.util.List;

public interface ShortcutMapper extends Mapper<Shortcut> {

    List<Shortcut> selectMyShortcutFunction(Long userId);

}