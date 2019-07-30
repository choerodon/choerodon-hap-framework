package io.choerodon.hap.core.impl;

import io.choerodon.base.provider.CustomProvider;
import io.choerodon.hap.util.dto.Language;
import io.choerodon.hap.util.service.ILanguageProvider;
import io.choerodon.web.core.impl.RequestHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CustomProviderImpl implements CustomProvider {
    @Autowired
    private ILanguageProvider languageProvider;

    @Override
    public String currentLanguage() {
        if (RequestHelper.getCurrentRequest() == null) {
            return "zh_CN";
        }
        return RequestHelper.getCurrentRequest().getLocale();
    }

    @Override
    public Long currentPrincipal() {
        if (RequestHelper.getCurrentRequest() == null) {
            return -1L;
        }
        return RequestHelper.getCurrentRequest().getUserId();
    }

    @Override
    public Set<String> getSupportedLanguages() {
        return languageProvider.getSupportedLanguages().stream().map(Language::getLangCode).collect(Collectors.toSet());
    }
}
