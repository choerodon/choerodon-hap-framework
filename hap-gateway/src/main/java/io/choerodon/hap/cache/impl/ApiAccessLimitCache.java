package io.choerodon.hap.cache.impl;

import io.choerodon.hap.api.application.dto.ApiAccessLimit;
import io.choerodon.hap.api.application.mapper.ApiAccessLimitMapper;
import io.choerodon.redis.impl.HashStringRedisCache;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Created by lijian.yin@hand-china.com on 2017/10/14.
 */
@Component(value = "apiAccessLimitCache")
public class ApiAccessLimitCache extends HashStringRedisCache<Map<String, ApiAccessLimit>> {

    private String selectAccessLimit = ApiAccessLimitMapper.class.getName() + ".selectAll";

    private final Logger logger = LoggerFactory.getLogger(ApiAccessLimitCache.class);

    {
        setLoadOnStartUp(true);
        setType(ApiAccessLimit.class);
        setName("api_access_limit");
    }


    /**
     * get ApiAccessLimit
     *
     * @return
     */
    public Map<String, ApiAccessLimit> getValue(String clientId, String serverCode) {
        String key = clientId + "_" + serverCode;
        return super.getValue(key);
    }

    @Override
    public void setValue(String key, Map<String, ApiAccessLimit> value) {
        super.setValue(key, value);
    }

    public void setValue(ApiAccessLimit value) {
        Map<String, ApiAccessLimit> apiAccessLimitMap = new HashMap<>();
        apiAccessLimitMap = getValue(value.getClientId(), value.getServerCode());
        if (null == apiAccessLimitMap) {
            apiAccessLimitMap = new HashMap<>();
        }
        String code = value.getInterfaceCode();
        apiAccessLimitMap.put(code, value);
        String key = value.getClientId() + "_" + value.getServerCode();
        super.setValue(key, apiAccessLimitMap);

    }

    @SuppressWarnings("unchecked")
    protected void initLoad() {
        try (SqlSession sqlSession = getSqlSessionFactory().openSession()) {
            List<ApiAccessLimit> apiAccessLimits = sqlSession.selectList(selectAccessLimit);
            Map<String, List<ApiAccessLimit>> groupMap = new HashMap<>();
            // 分组 key clientId
            //     value: List<ApiAccessLimit>
            Map<String, List<ApiAccessLimit>> apiAccessLimitMap = apiAccessLimits
                    .stream()
                    .collect(Collectors.groupingBy(ApiAccessLimit::getClientId));
            // 进一步分组 key: clientId_serverCode
            //          value: List<ApiAccessLimit>
            apiAccessLimitMap.forEach((key, value) -> {
                Map<String, List<ApiAccessLimit>> serverCodeMap = value
                        .stream()
                        .collect(Collectors.groupingBy(ApiAccessLimit::getServerCode));
                serverCodeMap.forEach((k, v) -> {
                    String keys = key + "_" + k;
                    groupMap.put(keys, v);
                });
            });
            // 最终分组，并存入redis
            groupMap.forEach((key, value) -> {
                Map<String, ApiAccessLimit> resultMap = new HashMap<>();
                value.forEach(apiAccessLimit -> {
                    resultMap.put(apiAccessLimit.getInterfaceCode(), apiAccessLimit);
                });
                setValue(key, resultMap);
            });
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("init apiAccessLimit cache exception: ", e);
            }
        }
    }

    @Override
    protected Map<String, ApiAccessLimit> stringToObject(String value) {
        try {
            return getObjectMapper().readValue(value, getObjectMapper().getTypeFactory().constructParametricType(
                    HashMap.class, String.class, ApiAccessLimit.class));
        } catch (Exception e) {
            if (logger.isWarnEnabled()) {
                logger.warn("invalid value: " + value);
            }
            throw new RuntimeException(e);
        }
    }
}
