package io.choerodon.hap.util.service;



import io.choerodon.hap.util.dto.Language;

import java.util.List;

/**
 * 用于获取系统所支持的语言环境.
 *
 * @author shengyang.zhou@hand-china.com
 */
public interface ILanguageProvider {

    /**
     * 取得系统支持的语言.
     *
     * @return 没有数据的话, 返回空的 list. 不返回null
     */
    List<Language> getSupportedLanguages();
}
