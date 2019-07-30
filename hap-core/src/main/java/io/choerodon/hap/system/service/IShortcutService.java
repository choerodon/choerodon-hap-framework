package io.choerodon.hap.system.service;

import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.system.dto.Shortcut;
import io.choerodon.mybatis.service.IBaseService;

import java.util.List;

public interface IShortcutService extends IBaseService<Shortcut>, ProxySelf<IShortcutService> {

    List<Shortcut> selectMyShortcutFunction(Long userId);

}