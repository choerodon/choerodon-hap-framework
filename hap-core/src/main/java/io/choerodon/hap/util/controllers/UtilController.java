package io.choerodon.hap.util.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.hap.util.dto.CodeValue;
import io.choerodon.hap.util.dto.Language;
import io.choerodon.hap.util.dto.Prompt;
import io.choerodon.hap.util.service.ICodeService;
import io.choerodon.hap.util.service.ILanguageProvider;
import io.choerodon.hap.util.service.ILovService;
import io.choerodon.hap.util.service.impl.CacheMessageSource;
import io.choerodon.hap.util.service.impl.DefaultPromptListener;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.core.impl.RequestHelper;
import io.choerodon.web.dto.ResponseData;
import org.apache.commons.lang3.LocaleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@RestController
public class UtilController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(UtilController.class);

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DefaultPromptListener promptListener;

    @Autowired
    private ICodeService codeService;
    @Autowired
    private ILanguageProvider languageProvider;
    @Autowired
    private ILovService lovService;
    @Autowired
    private CacheMessageSource cacheMessageSource;

    /**
     * 获取prompts数据.
     *
     * @return json值
     * @throws JsonProcessingException 对象转 JSON 可能出现的异常
     */
    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @GetMapping(value = "/common/prompts", produces = "application/javascript;charset=utf8")
    @ResponseBody
    public String getPromptsData() throws JsonProcessingException {
        Locale locale = LocaleUtils.toLocale(RequestHelper.getCurrentRequest().getLocale());
        String lang = locale.toString();
        return getCommonPrompts(lang);
    }

    /**
     * 获取prompts数据.
     *
     * @param params 参数
     * @return json值
     * @throws JsonProcessingException 对象转 JSON 可能出现的异常
     */
    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @GetMapping(value = "/common/code", produces = "application/javascript;charset=utf8")
    @ResponseBody
    public String getCodeData(@RequestParam Map<String, String> params) throws JsonProcessingException {
        StringBuilder sb = new StringBuilder();
        params.forEach((k, v) -> {
            List<CodeValue> enabledCodeValues = codeService.getCodeValuesByCode(v);
            try {
                if (enabledCodeValues == null) {
                    toJson(sb, k, Collections.emptyList());
                } else {
                    toJson(sb, k, enabledCodeValues);
                }
                sb.append("\n");
            } catch (JsonProcessingException e) {
                if (logger.isErrorEnabled()) {
                    logger.error(e.getMessage(), e);
                }
            }
        });
        return sb.toString();
    }

    /**
     * 获取prompts数据.
     *
     * @param params 参数
     * @return json值
     * @throws JsonProcessingException 对象转 JSON 可能出现的异常
     */
    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @GetMapping(value = "/common/language", produces = "application/javascript;charset=utf8")
    @ResponseBody
    public String getLanguageData(@RequestParam Map<String, String> params) throws JsonProcessingException {
        StringBuilder sb = new StringBuilder();
        List<?> data = languageProvider.getSupportedLanguages();
        String var = params.get("var");
        toJson(sb, var, data);
        return sb.toString();
    }

    /**
     * 获取Hap开头系统级别的描述.
     *
     * @param lang 当前语言
     * @return 描述的字符串
     */
    private String getCommonPrompts(String lang) {
        List<Prompt> list = promptListener.getDefaultPrompt(lang);
        if (list == null) {
            return "//null";
        }
        StringBuilder sb = new StringBuilder();
        for (Prompt prompt : list) {
            sb.append("$l('").append(prompt.getPromptCode().toLowerCase()).append("','").append(prompt.getDescription())
                    .append("');\n");
        }
        sb.append("//").append(list.size());
        return sb.toString();
    }

    /**
     * 基础数据转json格式字符串
     *
     * @param sb   StringBuilder
     * @param var  如果var不为空 在数据中加上var声明
     * @param data 数据
     * @throws JsonProcessingException json转换异常
     */
    private void toJson(StringBuilder sb, String var, Object data) throws JsonProcessingException {
        boolean hasVar = var != null && var.length() > 0;
        if (hasVar) {
            sb.append("var ").append(var).append('=');
        }
        sb.append(objectMapper.writeValueAsString(data));
        if (hasVar) {
            sb.append(';');
        }
    }


    /**
     * 取 快码 专用(仅限一个). <p>
     * 仅返回code 的内容(作为数组),没有额外的内容.
     *
     * @param code    快码 code
     * @param request request
     * @return json array
     * @throws JsonProcessingException 对象转 JSON 可能出现的异常
     */
    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @PostMapping(value = "/common/code/{code}/")
    @ResponseBody
    public List<CodeValue> getCommonCode(@PathVariable String code, HttpServletRequest request) throws JsonProcessingException {
        return codeService.getCodeValuesByCode(code);
    }

    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @PostMapping(value = "/common/language/")
    @ResponseBody
    public List<Language> getSupportedLanguage(HttpServletRequest request) {
        return languageProvider.getSupportedLanguages();
    }

    /**
     * 通用LOV的查询url（参数提交方式为FORM）.
     *
     * @param id       lovId
     * @param page     起始页
     * @param pagesize 分页大小
     * @param params   参数
     * @param request  HttpServletRequest
     * @return ResponseData ResponseData
     */
    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @PostMapping(value = "/common/lov/{id}")
    @ResponseBody
    public ResponseData getLovDatas(@PathVariable String id, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                    @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, @RequestParam Map<String, String> params,
                                    HttpServletRequest request) {
        return new ResponseData(lovService.selectDatas(id, params, page, pagesize));
    }

    /**
     * 通用LOV的查询url（参数提交方式为JSON）.
     *
     * @param id       lovId
     * @param page     起始页
     * @param pagesize 分页大小
     * @param params   参数
     * @param request  HttpServletRequest
     * @return ResponseData ResponseData
     */
    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @PostMapping(value = "/common/lov/dataset/{id}")
    @ResponseBody
    public ResponseData getLovDataByOption(@PathVariable String id, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                           @RequestParam(defaultValue = "0") int pagesize, @RequestBody(required = false) Map<String, Object> params,
                                           HttpServletRequest request) {
        return new ResponseData(lovService.selectDatas(id, params, page, pagesize));
    }

    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @PostMapping(value = "/common/prompt/{module}")
    @ResponseBody
    public Map<String, String> getPromptByModule(@PathVariable String module, HttpServletRequest request) {
        return Optional.ofNullable(cacheMessageSource.getModulePrompts(RequestHelper.getCurrentRequest().getLocale(), module)).orElse(Collections.emptyMap());
    }


}
