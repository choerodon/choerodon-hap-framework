package io.choerodon.hap.util.service.impl;

import io.choerodon.base.annotation.CacheDelete;
import io.choerodon.base.annotation.CacheSet;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.util.dto.Prompt;
import io.choerodon.hap.util.service.IPromptService;
import io.choerodon.message.IMessagePublisher;
import io.choerodon.mybatis.entity.Criteria;
import io.choerodon.mybatis.service.BaseServiceImpl;
import io.choerodon.redis.impl.HashStringRedisCacheGroup;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 描述维护.
 *
 * @author njq.niu@hand-china.com
 * @since 2016/6/9.
 */
@Service
@Dataset("Prompt")
public class PromptServiceImpl extends BaseServiceImpl<Prompt> implements IPromptService, IDatasetService<Prompt> {

    private static final String CACHE_PROMPT = "prompt";

    @Autowired
    private IMessagePublisher messagePublisher;

    @Autowired
    @Qualifier("promptCache")
    private HashStringRedisCacheGroup<Prompt> promptCache;

    @Override
    @SuppressWarnings("unchecked")
    public List<Prompt> submit(List<Prompt> list) {
        ((PromptServiceImpl) AopContext.currentProxy()).batchUpdate(list);
        for (Prompt prompt : list) {
            notifyCache(prompt);
            notifyPromptCache(prompt);
        }
        return list;
    }

    @Override
    public String getPromptDescription(String locale, String promptCode) {
        Prompt prompt = promptCache.getGroup(locale).getValue(promptCode);
        if (prompt == null) {
            prompt = new Prompt();
            prompt.setLang(locale);
            prompt.setPromptCode(promptCode);
            Criteria criteria = new Criteria(prompt);
            criteria.where(Prompt.FIELD_LANG, Prompt.FIELD_PROMPT_CODE);
            criteria.select(Prompt.FIELD_DESCRIPTION);
            List<Prompt> prompts = super.selectOptions(prompt, criteria);
            if (CollectionUtils.isNotEmpty(prompts)) {
                prompt = prompts.get(0);
                promptCache.getGroup(locale).setValue(promptCode, prompt);
            }
        }
        return prompt.getDescription();
    }

    @Override
    public Map<String, Map<String, String>> getModulePrompts(String locale) {
        Map<String, Map<String, String>> modulePromptMap = new ConcurrentHashMap<>(20);
        List<Prompt> promptList = promptCache.getGroup(locale).getAll();
        boolean reloadCache = false;
        if (CollectionUtils.isEmpty(promptList)) {
            Prompt prompt = new Prompt();
            prompt.setLang(locale);
            Criteria criteria = new Criteria(prompt);
            criteria.where(Prompt.FIELD_LANG);
            criteria.select(Prompt.FIELD_PROMPT_CODE, Prompt.FIELD_DESCRIPTION, Prompt.FIELD_MODULE_CODE);
            promptList = super.selectOptions(prompt, criteria);
            if (CollectionUtils.isNotEmpty(promptList)) {
                reloadCache = true;
            }
        }
        if (CollectionUtils.isNotEmpty(promptList)) {
            for (Prompt prompt : promptList) {
                if (prompt.getModuleCode() != null) {
                    Map<String, String> modulePrompts = modulePromptMap.computeIfAbsent(prompt.getModuleCode(), k -> new ConcurrentHashMap<>(100));
                    modulePrompts.put(prompt.getPromptCode(), prompt.getDescription());
                }
                if (reloadCache) {
                    promptCache.getGroup(locale).setValue(prompt.getPromptCode(), prompt);
                }
            }
        }
        return modulePromptMap;
    }

    @Override
    @CacheSet(cache = CACHE_PROMPT)
    public Prompt insertSelective(Prompt prompt) {
        super.insertSelective(prompt);
        return prompt;
    }

    @Override
    @CacheDelete(cache = CACHE_PROMPT)
    public int deleteByPrimaryKey(Prompt prompt) {
        return super.deleteByPrimaryKey(prompt);
    }

    @Override
    @CacheSet(cache = CACHE_PROMPT)
    public Prompt updateByPrimaryKeySelective(Prompt prompt) {
        return super.updateByPrimaryKeySelective(prompt);
    }

    @Override
    @CacheSet(cache = CACHE_PROMPT)
    public Prompt updateByPrimaryKey(Prompt prompt) {
        return super.updateByPrimaryKey(prompt);
    }

    private void notifyCache(Prompt prompt) {
        messagePublisher.publish(DefaultPromptListener.CACHE_PROMPT, prompt.getPromptCode());
    }

    private void notifyPromptCache(Prompt prompt) {
        messagePublisher.publish(CacheMessageSource.CACHE_PROMPT_ALL, prompt);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Prompt> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            Prompt prompt = new Prompt();
            BeanUtils.populate(prompt, body);
            prompt.setSortname(sortname);
            prompt.setSortorder(isDesc ? "desc" : "asc");
            return super.select(prompt, page, pageSize);
        } catch (Exception e) {
            throw new DatasetException("dataset.error", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Prompt> mutations(List<Prompt> prompts) {
        return submit(prompts);
    }


}
