package io.choerodon.hap.system.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import io.choerodon.hap.account.exception.UserException;
import io.choerodon.hap.cache.impl.HotkeyCache;
import io.choerodon.hap.core.impl.DefaultTlTableNameProvider;
import io.choerodon.hap.system.dto.DTOClassInfo;
import io.choerodon.hap.system.dto.Hotkey;
import io.choerodon.hap.system.mapper.MultiLanguageMapper;
import io.choerodon.hap.system.service.IProfileService;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.hap.util.dto.Language;
import io.choerodon.hap.util.service.ILanguageProvider;
import io.choerodon.redis.Cache;
import io.choerodon.redis.CacheManager;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.core.IRequest;
import io.choerodon.web.dto.ResponseData;
import io.choerodon.web.util.RequestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;
import tk.mybatis.mapper.entity.EntityField;

import javax.persistence.Table;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * 通用的 Controller,用来获取公共数据.
 *
 * @author shengyang.zhou@hand-china.com
 * @author njq.niu@hand-china.com
 */
@RestController
public class CommonController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(CommonController.class);

    @Autowired
    private MultiLanguageMapper multiLanguageMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private ILanguageProvider languageProvider;

    @Autowired
    private IProfileService profileService;

    @Autowired
    private MessageSource messageSource;

    private static final String HOTKEY = "hotkey";
    private static final String ERROR_CODE_SESSION_TIMEOUT = "sys_session_timeout";
    private static final String ERROR_CODE_ACCESS_DENIED = "sys_access_denied";
    public static final String CODE = "code";
    public static final String CTRL = "Ctrl";
    public static final String ALT = "Alt";
    public static final String SHIFT = "Shift";

    /**
     * 获取Hotkey数据.
     *
     * @param params  参数
     * @param request HttpServletRequest
     * @return json值
     * @throws JsonProcessingException 对象转 JSON 可能出现的异常
     */
    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @RequestMapping(value = "/common/hotkey", produces = "application/javascript;charset=utf8")
    @ResponseBody
    public String getCommonData(@RequestParam Map<String, String> params,
                                HttpServletRequest request) throws JsonProcessingException {
        StringBuilder sb = new StringBuilder();
        IRequest iRequest = createRequestContext(request);
        Cache<?> cache = cacheManager.getCache(HOTKEY);
        List<HotkeyData> data = getCommonHotkeys(iRequest, (HotkeyCache) cache);
        toJson(sb, "hotKeys", data);
        return sb.toString();
    }


    /**
     * 处理多语言字段.
     *
     * @param request HttpServletRequest
     * @param id      主键值
     * @param dto     dto全名
     * @param field   多语言字段名称(dto中的属性名)
     * @return 视图
     */
    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @RequestMapping(value = "sys/sys_multilanguage_editor.html")
    public ModelAndView loadMultiLanguageFields(HttpServletRequest request, @RequestParam String id,
                                                @RequestParam String dto, @RequestParam String field) {
        ModelAndView view = new ModelAndView(getViewPath() + "/sys/sys_multilanguage_editor");
        if (StringUtils.isNotEmpty(id) && StringUtils.isNotEmpty(dto) && StringUtils.isNotEmpty(field)) {
            Class<?> clazz;
            try {
                clazz = Class.forName(dto);
                Table table = clazz.getAnnotation(Table.class);
                EntityField idField = DTOClassInfo.getIdFields(clazz)[0];
                EntityField tlField = DTOClassInfo.getEntityField(clazz, field);
                if (table != null && idField != null && tlField != null) {
                    Map<String, String> map = new HashMap<>(4);
                    map.put("table", DefaultTlTableNameProvider.getInstance().getTlTableName(table.name()));
                    map.put("idName", DTOClassInfo.getColumnName(idField));
                    map.put("tlName", DTOClassInfo.getColumnName(tlField));
                    map.put("id", id);
                    List list = multiLanguageMapper.select(map);
                    view.addObject("list", list);
                }
            } catch (Exception e) {
                if (logger.isErrorEnabled()) {
                    logger.error(e.getMessage(), e);
                }
            }
        } else {
            List<Language> list = languageProvider.getSupportedLanguages();
            list.sort((a, b) -> a.getLangCode().compareTo(b.getLangCode()));
            view.addObject("list", list);
        }
        return view;
    }

    /**
     * 处理多语言字段.
     *
     * @param request HttpServletRequest
     * @param id      主键值
     * @param dto     dto全名
     * @param field   多语言字段名称(dto中的属性名)
     * @return Map
     */
    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @GetMapping(value = "/sys/multiLanguage")
    public Map<String, Object> loadMultiLanguageFields2(HttpServletRequest request, @RequestParam String id,
                                                        @RequestParam String dto, @RequestParam String field) {
        Map<String, Object> result = new HashMap<>(1);
        if (StringUtils.isNotEmpty(id) && StringUtils.isNotEmpty(dto) && StringUtils.isNotEmpty(field)) {
            Class<?> clazz;
            try {
                clazz = Class.forName(dto);
                Table table = clazz.getAnnotation(Table.class);
                EntityField idField = DTOClassInfo.getIdFields(clazz)[0];
                EntityField tlField = DTOClassInfo.getEntityField(clazz, field);
                if (table != null && idField != null && tlField != null) {
                    Map<String, String> map = new HashMap<>(4);
                    map.put("table", DefaultTlTableNameProvider.getInstance().getTlTableName(table.name()));
                    map.put("idName", DTOClassInfo.getColumnName(idField));
                    map.put("tlName", DTOClassInfo.getColumnName(tlField));
                    map.put("id", id);
                    result.put("multiLanguages", multiLanguageMapper.select(map));
                }
            } catch (Exception e) {
                if (logger.isErrorEnabled()) {
                    logger.error(e.getMessage(), e);
                }
            }
        } else {
            List<Language> list = languageProvider.getSupportedLanguages();
            list.sort((a, b) -> a.getLangCode().compareTo(b.getLangCode()));
            result.put("multiLanguages", list);
        }
        return result;
    }

    /**
     * 获取配置文件.
     *
     * @param params  参数
     * @param request HttpServletRequest
     * @return json值
     */
    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @PostMapping(value = "/common/profile", produces = "application/javascript;charset=utf8")
    @ResponseBody
    public String getProfile(@RequestParam Map<String, String> params, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        StringBuilder sb = new StringBuilder();
        params.forEach((k, v) -> {
            String value = profileService.getProfileValue(requestContext, v);
            try {
                toJson(sb, k, value);
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
     * 请求超时
     *
     * @param request  request
     * @param response response
     * @return 超时信息
     */
    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @PostMapping(value = "/timeout")
    public Object sessionTimeout(HttpServletRequest request, HttpServletResponse response) {
        if (RequestUtils.isAjaxRequest(request)) {
            ResponseData res = new ResponseData(false);
            res.setCode(ERROR_CODE_SESSION_TIMEOUT);
            return res;
        } else {
            return new ModelAndView("timeout");
        }
    }


    /**
     * 拒绝访问.
     *
     * @param request  request
     * @param response response
     * @return Object
     */
    @Permission(type = ResourceType.SITE, permissionPublic = true)
    @PostMapping(value = "/access-denied")
    public Object accessDenied(HttpServletRequest request, HttpServletResponse response) {
        if (request.getSession(false) == null) {
            return sessionTimeout(request, response);
        } else {
            if (RequestUtils.isAjaxRequest(request)) {
                ResponseData res = new ResponseData(false);
                res.setCode(ERROR_CODE_ACCESS_DENIED);
                return res;
            } else {
                return new ModelAndView("403");
            }
        }
    }

    @Permission(type = ResourceType.SITE, permissionPublic = true)
    @PostMapping(value = "/kendo/export")
    @ResponseBody
    public void save(String fileName, String base64, String contentType, HttpServletResponse response) throws IOException {
        response.setHeader("Content-Disposition", "attachment;filename*=UTF-8''" + URLEncoder.encode(fileName, "UTF-8"));
        response.setContentType(contentType);
        byte[] data = DatatypeConverter.parseBase64Binary(base64);
        response.setContentLength(data.length);
        response.getOutputStream().write(data);
        response.flushBuffer();
    }

    /**
     * 单点登录失败逻辑.
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @return 单点登录失败界面
     */
    @Permission(type = ResourceType.SITE, permissionPublic = true)
    @GetMapping(value = "/casLoginFailure")
    public ModelAndView casLoginFailure(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView view = new ModelAndView("cas_login_failure");
        Throwable exception = (Exception) request.getAttribute("exception");
        String code = UserException.ERROR_USER_PASSWORD;
        if (exception != null) {
            exception = Throwables.getRootCause(exception);
            code = exception.getMessage();
        }
        Locale locale = RequestContextUtils.getLocale(request);
        String errorMessage = messageSource.getMessage(code, null, locale);
        view.addObject("errorMessage", errorMessage);
        return view;
    }

    @RequestMapping(value = {"/{name}.html", "/{name}.view"})
    public ModelAndView renderView(@PathVariable String name, Model model) {
        return new ModelAndView(name);
    }

    @RequestMapping(value = {"/{folder1}/{name}.html", "/{folder1}/{name}.view"})
    public ModelAndView renderFolder1View(@PathVariable String folder1, @PathVariable String name, Model model) {
        return new ModelAndView(
                new StringBuilder(getViewPath()).append("/").append(folder1).append("/").append(name).toString());
    }

    @RequestMapping(value = {"/{folder1}/{folder2}/{name}.html", "/{folder1}/{folder2}/{name}.view"})
    public ModelAndView renderFolder2View(@PathVariable String folder1, @PathVariable String folder2,
                                          @PathVariable String name, Model model) {
        return new ModelAndView(new StringBuilder(getViewPath()).append("/").append(folder1).append("/").append(folder2)
                .append("/").append(name).toString());
    }

    @RequestMapping(value = {"/{folder1}/{folder2}/{folder3}/{name}.html",
            "/{folder1}/{folder2}/{folder3}/{name}.view"})
    public ModelAndView renderFolder3View(@PathVariable String folder1, @PathVariable String folder2,
                                          @PathVariable String folder3, @PathVariable String name, Model model) {
        return new ModelAndView(new StringBuilder(getViewPath()).append("/").append(folder1).append("/").append(folder2)
                .append("/").append(folder3).append("/").append(name).toString());
    }

    @RequestMapping(value = {"/{folder1}/{folder2}/{folder3}/{folder4}/{name}.html",
            "/{folder1}/{folder2}/{folder3}/{folder4}/{name}.view"})
    public ModelAndView renderFolder4View(@PathVariable String folder1, @PathVariable String folder2,
                                          @PathVariable String folder3, @PathVariable String folder4, @PathVariable String name, Model model) {
        return new ModelAndView(new StringBuilder(getViewPath()).append("/").append(folder1).append("/").append(folder2)
                .append("/").append(folder3).append("/").append(folder4).append("/").append(name).toString());
    }

    /**
     * 封装热键数据.
     *
     * @param hotkey 热键
     * @return 热键数据
     */
    private HotkeyData getHotkeyData(Hotkey hotkey) {
        HotkeyData hotkeyData = new HotkeyData();
        HotkeyValue hotkeyValue = new HotkeyValue();
        String key = hotkey.getHotkey();
        hotkeyData.setCode(hotkey.getCode());
        hotkeyData.setHotkey(hotkeyValue.initValue(key, hotkeyValue));
        return hotkeyData;

    }

    /**
     * 获取热键数据
     *
     * @param iRequest IRequest
     * @param cache    HotkeyCache
     * @return 热键List
     */
    private List<HotkeyData> getCommonHotkeys(IRequest iRequest, HotkeyCache cache) {
        Map<String, HotkeyData> hotkeyDatas = new HashMap<>(2);
        List<HotkeyData> data = new ArrayList<>();
        Hotkey[] hotkeysSys = cache.getValue("system_0");
        Hotkey[] hotkeyUser = cache.getValue("user_" + iRequest.getUserId());
        if (hotkeysSys != null) {
            for (Hotkey hotkey : hotkeysSys) {
                hotkeyDatas.put(hotkey.getCode(), getHotkeyData(hotkey));
            }
        }
        if (hotkeyUser != null) {
            for (Hotkey hotkey : hotkeyUser) {
                HotkeyData value = hotkeyDatas.get(hotkey.getCode());
                if (value != null) {
                    hotkeyDatas.put(hotkey.getCode(), getHotkeyData(hotkey));
                }
            }
        }
        data.addAll(hotkeyDatas.values());
        return data;
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

    private class HotkeyValue {
        private boolean altKey = false;

        private boolean shiftKey = false;

        private boolean ctrlKey = false;

        private String keyValue = "";

        public String getKeyValue() {
            return keyValue;
        }

        public void setKeyValue(String keyValue) {
            this.keyValue = keyValue;
        }

        public boolean isAltKey() {
            return altKey;
        }

        public void setAltKey(boolean altKey) {
            this.altKey = altKey;
        }

        public boolean isShiftKey() {
            return shiftKey;
        }

        public void setShiftKey(boolean shiftKey) {
            this.shiftKey = shiftKey;
        }

        public boolean isCtrlKey() {
            return ctrlKey;
        }

        public void setCtrlKey(boolean ctrlKey) {
            this.ctrlKey = ctrlKey;
        }

        public HotkeyValue initValue(String key, HotkeyValue value) {
            if (key.contains(CTRL)) {
                value.setCtrlKey(true);
                key = key.replace(CTRL, "");
            }
            if (key.contains(ALT)) {
                value.setAltKey(true);
                key = key.replace(ALT, "");
            }
            if (key.contains(SHIFT)) {
                value.setShiftKey(true);
                key = key.replace(SHIFT, "");
            }
            key = key.replace("+", "");
            value.setKeyValue(key);
            return value;
        }
    }

    private class HotkeyData {
        private HotkeyValue hotkey;

        private String code;

        public HotkeyValue getHotkey() {
            return hotkey;
        }

        public void setHotkey(HotkeyValue hotkey) {
            this.hotkey = hotkey;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }

    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @PostMapping(value = "/common/generator/uuid")
    public String generatorUUID() {
        return UUID.randomUUID().toString();
    }
}
