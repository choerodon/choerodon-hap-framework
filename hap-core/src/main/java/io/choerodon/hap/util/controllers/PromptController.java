package io.choerodon.hap.util.controllers;

import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.hap.util.dto.Prompt;
import io.choerodon.hap.util.service.IPromptService;
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
 * 描述维护.
 *
 * @author shengyang.zhou@hand-china.com
 * @since 2016/6/9.
 */
@RestController
@RequestMapping(value = {"/sys/prompt", "/api/sys/prompt"})
public class PromptController extends BaseController {

    @Autowired
    private IPromptService promptService;

    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @PostMapping(value = "/query")
    public ResponseData query(HttpServletRequest request, Prompt prompt, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        return new ResponseData(promptService.select(prompt, page, pagesize));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/submit")
    public ResponseData submit(@RequestBody List<Prompt> prompts, BindingResult result, HttpServletRequest request) {
        getValidator().validate(prompts, result);
        if (result.hasErrors()) {
            ResponseData rd = new ResponseData(false);
            rd.setMessage(getErrorMessage(result, request));
            return rd;
        }
        return new ResponseData(promptService.submit(prompts));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/remove")
    public ResponseData remove(HttpServletRequest request, @RequestBody List<Prompt> prompts) {
        promptService.submit(prompts);
        return new ResponseData();
    }
}
