package io.choerodon.hap.util.service.impl;

import io.choerodon.hap.util.dto.Prompt;
import io.choerodon.hap.util.service.IPromptService;
import io.choerodon.message.IMessageConsumer;
import io.choerodon.message.annotation.TopicMonitor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author shengyang.zhou@hand-china.com
 */
@Component
@TopicMonitor(channel = DefaultPromptListener.CACHE_PROMPT)
public class DefaultPromptListener implements IMessageConsumer<String>, InitializingBean {

    public static final String CACHE_PROMPT = "cache.prompt";

    public static final String SYS_PROMPT = "hap.";

    @Autowired
    private IPromptService promptService;

    private Map<String, List<Prompt>> promptsMap = new HashMap<>();

    @Override
    public void onMessage(String message, String pattern) {
        if (message.toLowerCase().startsWith(SYS_PROMPT)) {
            reload();
        }
    }

    private void reload() {
        Prompt p = new Prompt();
        p.setPromptCode(SYS_PROMPT);
        List<Prompt> prompts = promptService.selectOptions(p, null);
        Map<String, List<Prompt>> promptsMapNew = new HashMap<>(16);
        for (Prompt prompt : prompts) {
            List<Prompt> list = promptsMapNew.computeIfAbsent(prompt.getLang(), k -> new ArrayList<>());
            list.add(prompt);
        }

        promptsMap = promptsMapNew;
    }

    public List<Prompt> getDefaultPrompt(String lang) {
        List<Prompt> prompts = promptsMap.get(lang);
        if (prompts == null || prompts.isEmpty()) {
            reload();
            prompts = promptsMap.get(lang);
        }
        return prompts;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        reload();
    }
}
