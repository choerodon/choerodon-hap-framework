package io.choerodon.hap.util.cache.impl;

import io.choerodon.hap.util.dto.Code;
import io.choerodon.hap.util.dto.CodeValue;
import io.choerodon.hap.util.dto.Language;
import io.choerodon.hap.util.mapper.CodeMapper;
import io.choerodon.hap.util.mapper.CodeValueMapper;
import io.choerodon.hap.util.service.ILanguageProvider;
import io.choerodon.redis.impl.HashStringRedisCacheGroup;
import io.choerodon.web.core.IRequest;
import io.choerodon.web.core.impl.RequestHelper;
import io.choerodon.web.core.impl.ServiceRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author shengyang.zhou@hand-china.com
 */
@Component(value = "codeCache")
public class SysCodeCache extends HashStringRedisCacheGroup<Code> {

    private Logger logger = LoggerFactory.getLogger(SysCodeCache.class);

    private String codeQuerySqlId = CodeMapper.class.getName() + ".select";
    private String codeValueQuerySqlId = CodeValueMapper.class.getName() + ".select";

    @Autowired
    private ILanguageProvider languageProvider;

    {
        setLoadOnStartUp(true);
        setType(Code.class);
        setGroupField(new String[]{"lang"});
        setKeyField(new String[]{"code"});
        setName("code");
    }

    /**
     * key 包含 code 和语言两部分,用'.'拼接.
     *
     * @param key code.lang
     * @return 一个仅包含 code value 的空的 code
     */
    @Override
    public Code getValue(String key) {
        String[] keys = retrieveGroupAndKey(key);
        return super.getValue(keys[0], keys[1]);
    }

    private String[] retrieveGroupAndKey(String key) {
        String group = "zh_CN";
        String str = ".";
        if (key != null && key.contains(str)) {
            group = StringUtils.substringAfterLast(key, str);
            key = StringUtils.substringBeforeLast(key, str);
        }
        return new String[]{group, key};
    }

    @Override
    public void setValue(String key, Code code) {
        String[] keys = retrieveGroupAndKey(key);
        super.setValue(keys[0], keys[1], code);
    }

    /**
     * @param key code
     */
    @Override
    public void remove(String key) {
        for (Language language : languageProvider.getSupportedLanguages()) {
            super.remove(language.getLangCode(), key);
        }
    }

    public void reload(Long codeId) {
        IRequest oldRequest = RequestHelper.getCurrentRequest();
        try (SqlSession sqlSession = getSqlSessionFactory().openSession()) {
            for (Language language : languageProvider.getSupportedLanguages()) {
                IRequest request = new ServiceRequest();
                request.setLocale(language.getLangCode());
                RequestHelper.setCurrentRequest(request);
                Code para = new Code();
                para.setCodeId(codeId);
                Code code = sqlSession.selectOne(codeQuerySqlId, para);
                CodeValue p2 = new CodeValue();
                p2.setCodeId(codeId);
                p2.setSortname("orderSeq");
                List<CodeValue> codeValues = sqlSession.selectList(codeValueQuerySqlId, p2);
                code.setCodeValues(codeValues);
                setValue(language.getLangCode(), code.getCode(), code);
            }
        } finally {
            RequestHelper.setCurrentRequest(oldRequest);
        }
    }

    @Override
    protected void initLoad() {
        Map<Long, Code> tempMap = new HashMap<>(64);
        try (SqlSession sqlSession = getSqlSessionFactory().openSession()) {
            for (Language language : languageProvider.getSupportedLanguages()) {
                IRequest request = new ServiceRequest();
                request.setLocale(language.getLangCode());
                RequestHelper.setCurrentRequest(request);

                sqlSession.select(codeQuerySqlId, resultContext -> {
                    Code code = (Code) resultContext.getResultObject();
                    tempMap.put(code.getCodeId(), code);
                });

                CodeValue cV = new CodeValue();
                cV.setSortname("orderSeq");
                sqlSession.select(codeValueQuerySqlId, cV, resultContext -> {
                    CodeValue value = (CodeValue) resultContext.getResultObject();
                    Code code = tempMap.get(value.getCodeId());
                    if (code != null) {
                        List<CodeValue> codeValues = code.getCodeValues();
                        if (codeValues == null) {
                            codeValues = new ArrayList<>();
                            code.setCodeValues(codeValues);
                        }
                        codeValues.add(value);
                    }
                });
                tempMap.forEach((k, v) -> setValue(language.getLangCode(), v.getCode(), v));
                tempMap.clear();
            }
            RequestHelper.clearCurrentRequest();
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("init syscode exception ", e);
            }
        }
    }

}
