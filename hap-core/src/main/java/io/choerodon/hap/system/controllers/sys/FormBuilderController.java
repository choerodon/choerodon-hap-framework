package io.choerodon.hap.system.controllers.sys;

import io.choerodon.hap.system.dto.Form;
import io.choerodon.hap.system.service.IFormBuilderService;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.dto.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class FormBuilderController extends BaseController {

    @Autowired
    private IFormBuilderService service;

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "sys/form/builder/query")
    @ResponseBody
    public ResponseData queryAll(Form builder, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                 @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        return new ResponseData(service.select(builder, page, pageSize));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "sys/form/builder/submit")
    @ResponseBody
    public ResponseData update(@RequestBody List<Form> dto, BindingResult result, HttpServletRequest request) {
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(dto));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "sys/form/builder/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<Form> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }

    @Permission(type = ResourceType.SITE)
    @GetMapping(value = {"/form/{name}"})
    public ModelAndView renderView(@PathVariable String name, Model model) {
        return new ModelAndView(name + ".form");
    }

    @Permission(type = ResourceType.SITE)
    @GetMapping(value = {"/form_preview"})
    public ModelAndView previewView(@RequestParam String code, Model model) {
        return new ModelAndView("preview/" + code + ".form");
    }


}