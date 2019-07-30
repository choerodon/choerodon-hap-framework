package io.choerodon.hap.util.service;


import io.choerodon.hap.util.dto.Prompt;
import io.choerodon.mybatis.service.IBaseService;

import java.util.List;
import java.util.Map;

/**
 * 多语言描述.
 *
 * @author shengyang.zhou@hand-china.com
 * @since 2016/6/9.
 */
public interface IPromptService extends IBaseService<Prompt> {
    /**
     * 更新描述维护.
     *
     * @param list 描述维护列表
     * @return 描述维护列表
     */
    List<Prompt> submit(List<Prompt> list);

    /**
     * 根据语言和编码获取描述.
     * 首先从redis缓存取，如果没有，再尝试从数据库去读.
     *
     * @param locale     语言
     * @param promptCode 编码
     * @return 描述
     */
    String getPromptDescription(String locale, String promptCode);

    /**
     * 根据语言获取所有系统模块的描述维护Map.
     * 首先尝试从redis缓存取，如果没有，再尝试从数据库去读.
     *
     * @param locale 语言
     * @return 所有系统模块的描述维护Map
     */
    Map<String, Map<String, String>> getModulePrompts(String locale);
}
