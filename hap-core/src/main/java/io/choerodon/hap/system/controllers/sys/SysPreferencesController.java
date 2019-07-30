package io.choerodon.hap.system.controllers.sys;

import io.choerodon.hap.system.dto.SysPreferences;
import io.choerodon.hap.system.service.ISysPreferencesService;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.base.util.BaseConstants;
import io.choerodon.hap.util.dto.Language;
import io.choerodon.hap.util.service.ILanguageProvider;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.core.IRequest;
import io.choerodon.web.dto.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ThemeResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 系统首选项Controller.
 *
 * @author zhangYang
 */
@Controller
public class SysPreferencesController extends BaseController {

    @Autowired
    private ISysPreferencesService sysPreferencesService;

    @Autowired
    private ILanguageProvider languageProvider;

    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @GetMapping(value = "/sys/um/sys_preferences.html")
    public ModelAndView sysPreferences(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView(getViewPath() + "/sys/um/sys_preferences");
        List<Language> languages = languageProvider.getSupportedLanguages();
        mv.addObject("languages", languages);
        return mv;
    }

    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @PostMapping(value = "/sys/preferences/savePreferences")
    @ResponseBody
    public ResponseData savePreferences(HttpServletRequest request, HttpServletResponse response, @RequestBody List<SysPreferences> sysPreferences, BindingResult result) {
        getValidator().validate(sysPreferences, result);
        if (result.hasErrors()) {
            ResponseData rd = new ResponseData(false);
            rd.setMessage(getErrorMessage(result, request));
            return rd;
        }
        List<SysPreferences> lists = sysPreferencesService.saveSysPreferences(createRequestContext(request), sysPreferences);
        //  在数据库操作成功后 更新用户session中的首选项信息
        for (SysPreferences preference : lists) {
            if (BaseConstants.PREFERENCE_LOCALE.equalsIgnoreCase(preference.getPreferences())) {
                LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
                if (localeResolver != null) {
                    localeResolver.setLocale(request, response, StringUtils.parseLocaleString(preference.getPreferencesValue()));
                }
            } else if (BaseConstants.PREFERENCE_THEME.equalsIgnoreCase(preference.getPreferences())) {
                ThemeResolver themeResolver = RequestContextUtils.getThemeResolver(request);
                if (themeResolver != null) {
                    themeResolver.setThemeName(request, response, preference.getPreferencesValue());
                }
            } else if (BaseConstants.PREFERENCE_TIME_ZONE.equalsIgnoreCase(preference.getPreferences())) {
                WebUtils.setSessionAttribute(request, SessionLocaleResolver.TIME_ZONE_SESSION_ATTRIBUTE_NAME,
                        org.springframework.util.StringUtils.parseTimeZoneString(preference.getPreferencesValue()));
                WebUtils.setSessionAttribute(request, BaseConstants.PREFERENCE_TIME_ZONE, preference.getPreferencesValue());
            } else if (BaseConstants.PREFERENCE_NAV.equalsIgnoreCase(preference.getPreferences())) {
                WebUtils.setSessionAttribute(request, BaseConstants.PREFERENCE_NAV, preference.getPreferencesValue());
            }
        }

        return new ResponseData(lists);
    }

    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @PostMapping(value = "/sys/preferences/queryPreferences")
    @ResponseBody
    public ResponseData queryPreferences(HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        SysPreferences preference = new SysPreferences();
        preference.setUserId(requestContext.getUserId());
        List<SysPreferences> lists = sysPreferencesService.querySysPreferencesByDb(requestContext, preference);
        return new ResponseData(lists);
    }
}
