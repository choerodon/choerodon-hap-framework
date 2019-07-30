package io.choerodon.hap.system.controllers.sys;

import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.hap.system.dto.Hotkey;
import io.choerodon.hap.system.service.IHotkeyService;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.dto.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class HotkeyController extends BaseController {

    @Autowired
    private IHotkeyService service;

    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @PostMapping(value = "/sys/hotkey/query")
    @ResponseBody
    public ResponseData query(Hotkey dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {

        dto.setHotkeyLevel("system");
        dto.setHotkeyLevelId((long) 0);
        return new ResponseData(service.selectOptions(dto, null));
    }

    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @PostMapping(value = "/sys/preference/hotkey/query")
    public ModelAndView preferenceQuery(HttpServletRequest request) {
        ModelAndView view = new ModelAndView();
        view.setViewName("/sys/um/sys_preference_hotkey");
        List<Hotkey> hotkeys = service.preferenceQuery();
        view.addObject("hotkeys", hotkeys);
        return view;
    }

    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @PostMapping(value = "/sys/personalPreference/hotkey/query")
    public List<Hotkey> preferenceQueryList(HttpServletRequest request) {
        return service.preferenceQuery();
    }

    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @PostMapping(value = "/sys/hotkey/submit")
    @ResponseBody
    public ResponseData update(@RequestBody List<Hotkey> dto, BindingResult result, HttpServletRequest request) {
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(dto));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/sys/hotkey/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<Hotkey> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }
}