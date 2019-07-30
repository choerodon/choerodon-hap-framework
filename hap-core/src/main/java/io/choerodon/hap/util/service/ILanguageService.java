package io.choerodon.hap.util.service;



import io.choerodon.hap.util.dto.Language;
import io.choerodon.mybatis.service.IBaseService;

import java.util.List;

/**
 * @author shengyang.zhou@hand-china.com
 */
public interface ILanguageService extends IBaseService<Language> {

    /**
     * 更新Language.
     *
     * @param list 语言列表
     * @return 结果列表
     */
    List<Language> submit(List<Language> list);

    /**
     * 删除Language.
     *
     * @param list 语言列表
     * @return 影响行数
     */
    int remove(List<Language> list);
}
