package io.choerodon.hap.util.service.impl;

import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.util.dto.Language;
import io.choerodon.hap.util.service.ILanguageService;
import io.choerodon.message.IMessagePublisher;
import io.choerodon.mybatis.service.BaseServiceImpl;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author shengyang.zhou@hand-china.com
 */
@Service
@Dataset("Language")
public class LanguageServiceImpl extends BaseServiceImpl<Language> implements ILanguageService, IDatasetService<Language> {

    @Autowired
    private IMessagePublisher messagePublisher;

    @Override
    public List<Language> submit(List<Language> list) {
        ((LanguageServiceImpl) AopContext.currentProxy()).batchUpdate(list);
        notifyCache();
        return list;
    }

    @Override
    public int remove(List<Language> list) {
        int result = ((LanguageServiceImpl) AopContext.currentProxy()).batchDelete(list);
        notifyCache();
        return result;
    }

    /**
     * 更新缓存数据
     */
    private void notifyCache() {
        messagePublisher.publish(LanguageProviderImpl.CACHE_LANGUAGE, "language");
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Language> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            Language language = new Language();
            BeanUtils.populate(language, body);
            return super.select(language, page, pageSize);
        } catch (Exception e) {
            throw new DatasetException("dataset.error", e);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Language> mutations(List<Language> languages) {
        return submit(languages);
    }

}
