package io.choerodon.hap.util.service.impl;

import io.choerodon.hap.util.dto.Prompt;
import io.choerodon.hap.util.service.IPromptService;
import io.choerodon.message.IMessageConsumer;
import io.choerodon.message.annotation.TopicMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CacheMessageSource.
 *
 * @author shengyang.zhou@hand-china.com
 */
@Component("messageSource")
@TopicMonitor(channel = CacheMessageSource.CACHE_PROMPT_ALL)
public class CacheMessageSource extends AbstractMessageSource implements IMessageConsumer<Prompt> {
    private static final String SINGLE_QUOTES_REPLACEMENT = "&#39;";
    private static final String DOUBLE_QUOTES_REPLACEMENT = "&#34;";

    public static final String CACHE_PROMPT_ALL = "cache.prompt.all";

    private Map<String, Map<String, String>> promptsMap = new ConcurrentHashMap<>();
    /**
     * Map<语言,Map<模块名,Map<描述维护code，描述维护description>>>.
     */
    private Map<String, Map<String, Map<String, String>>> localeModulePromptsMap = new ConcurrentHashMap<>();

    @Autowired
    private IPromptService promptService;

    public CacheMessageSource() {
        reload();
    }

    public void reload() {
    }

    @Override
    public void onMessage(Prompt message, String pattern) {
        Map<String, String> prompts = promptsMap.get(message.getLang());
        Map<String, Map<String, String>> modulePromptMap = localeModulePromptsMap.get(message.getLang());
        if (!CollectionUtils.isEmpty(prompts)) {
            prompts.put(message.getPromptCode(), message.getDescription());
        }
        if (!CollectionUtils.isEmpty(modulePromptMap)) {
            Map<String, String> modulePrompts = modulePromptMap.get(message.getModuleCode());
            if (CollectionUtils.isEmpty(modulePrompts)) {
                modulePrompts = new ConcurrentHashMap<>(100);
                modulePrompts.put(message.getPromptCode(), message.getDescription());
                modulePromptMap.put(message.getModuleCode(), modulePrompts);
            } else {
                modulePrompts.put(message.getPromptCode(), message.getDescription());
            }
        }
    }

    /**
     * 根据语言和模块名,查询出模块下的所有描述维护.
     *
     * @param locale 语言
     * @param module 模块名
     * @return 描述维护列表
     */
    public Map<String, String> getModulePrompts(String locale, String module) {
        // 获取当前语言所有模块的描述维护
        Map<String, Map<String, String>> modulePromptMap = localeModulePromptsMap.get(locale);
        if (CollectionUtils.isEmpty(modulePromptMap)) {
            modulePromptMap = promptService.getModulePrompts(locale);
            localeModulePromptsMap.put(locale, modulePromptMap);
        }
        //获取当前模块的所有描述维护
        return modulePromptMap.get(module);
    }

    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {
        return createMessageFormat(resolveCodeWithoutArguments(code, locale), locale);
    }


    @Override
    protected String resolveCodeWithoutArguments(String code, Locale locale) {
        String code2 = code.toLowerCase();
        String description = resolveCodeFromMap(code2, locale.toString());
        if (description == null) {
            return code;
        }
        return replaceQuote(description);
    }

    private String resolveCodeFromMap(String code, String locale) {
        Map<String, String> prompts = promptsMap.get(locale);
        if (CollectionUtils.isEmpty(prompts)) {
            //应该设置一个相对合适的size
            prompts = new ConcurrentHashMap<>(5000);
        }
        String description = prompts.get(code);
        if (description == null) {
            description = promptService.getPromptDescription(locale, code);
            if (description != null) {
                prompts.put(code, description);
                promptsMap.put(locale, prompts);
            }
        }
        return description;
    }

    private String replaceQuote(String str) {
        int idx = -1;
        char c = 0;
        for (int i = 0; i < str.length(); i++) {
            c = str.charAt(i);
            if (c == '\'' || c == '"') {
                idx = i;
                break;
            }
        }
        if (idx == -1) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str.length() + 32);
        sb.append(str.substring(0, idx));
        if (c == '"') {
            sb.append(DOUBLE_QUOTES_REPLACEMENT);
        } else {
            sb.append(SINGLE_QUOTES_REPLACEMENT);
        }
        for (int i = idx + 1; i < str.length(); i++) {
            c = str.charAt(i);
            if (c == '"') {
                sb.append(DOUBLE_QUOTES_REPLACEMENT);
            } else if (c == '\'') {
                sb.append(SINGLE_QUOTES_REPLACEMENT);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}