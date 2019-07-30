package io.choerodon.hap.system.service;

import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.system.dto.Hotkey;
import io.choerodon.mybatis.service.IBaseService;

import java.util.List;

public interface IHotkeyService extends IBaseService<Hotkey>, ProxySelf<IHotkeyService> {

    /**
     * 获取首选项展示的热键数据.
     *
     * @return 热键列表
     */
    List<Hotkey> preferenceQuery();

}