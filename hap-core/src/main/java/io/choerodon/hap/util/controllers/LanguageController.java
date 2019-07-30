package io.choerodon.hap.util.controllers;

import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.hap.util.dto.Language;
import io.choerodon.hap.util.service.ILanguageService;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.dto.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author shengyang.zhou@hand-china.com
 */
@RestController
@RequestMapping(value = {"/sys/language", "/api/sys/language"})
public class LanguageController extends BaseController {

    @Autowired
    private ILanguageService languageService;

    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @PostMapping(value = "/query")
    public ResponseData query(HttpServletRequest request, Language example,
                              @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        return new ResponseData(languageService.select(example, page, pagesize));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/submit")
    public ResponseData submit(HttpServletRequest request, @RequestBody List<Language> languageList,
                               BindingResult result) {
        getValidator().validate(languageList, result);
        if (result.hasErrors()) {
            ResponseData rs = new ResponseData(false);
            rs.setMessage(getErrorMessage(result, request));
            return rs;
        }
        return new ResponseData(languageService.submit(languageList));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = {"/remove", "/delete"})
    public ResponseData delete(HttpServletRequest request, @RequestBody List<Language> languageList) {
        languageService.remove(languageList);
        return new ResponseData();
    }
}
