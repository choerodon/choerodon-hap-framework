package io.choerodon.hap.system.controllers;

import io.choerodon.hap.system.dto.Shortcut;
import io.choerodon.hap.system.service.IShortcutService;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.core.IRequest;
import io.choerodon.web.core.impl.RequestHelper;
import io.choerodon.web.dto.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class ShortcutController extends BaseController {

    @Autowired
    private IShortcutService service;

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/sys/shortcut/query")
    public ResponseData query(HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectMyShortcutFunction(requestContext.getUserId()));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/sys/shortcut/submit")
    public ResponseData update(HttpServletRequest request, String functionCode) {
        IRequest requestContext = RequestHelper.getCurrentRequest();
        Shortcut shortcuts = new Shortcut(requestContext.getUserId(), functionCode);
        List<Shortcut> shortcutList = service.select(shortcuts, 1, 10);
        if (shortcutList.isEmpty()) {
            service.insert(shortcuts);
        } else {
            return null;
        }
        return new ResponseData();
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/sys/shortcut/remove")
    public ResponseData delete(HttpServletRequest request, String shortcutId) {
        Shortcut shortcut = new Shortcut();
        shortcut.setShortcutId(Long.parseLong(shortcutId));
        service.deleteByPrimaryKey(shortcut);
        return new ResponseData();
    }
}