package io.choerodon.hap.system.service.impl;

import io.choerodon.hap.system.mapper.ShortcutMapper;
import io.choerodon.hap.system.dto.Shortcut;
import io.choerodon.hap.system.service.IShortcutService;
import io.choerodon.mybatis.service.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ShortcutServiceImpl extends BaseServiceImpl<Shortcut> implements IShortcutService {

    @Autowired
    ShortcutMapper shortcutMapper;

    @Override
    public List<Shortcut> selectMyShortcutFunction(Long userId) {
        return shortcutMapper.selectMyShortcutFunction(userId);
    }
}