package io.choerodon.hap.core.interceptor;

import io.choerodon.hap.system.dto.DTOClassInfo;
import io.choerodon.base.util.BaseConstants;
import io.choerodon.mybatis.common.query.JoinCache;
import io.choerodon.mybatis.common.query.JoinCode;
import io.choerodon.mybatis.common.query.JoinLov;
import io.choerodon.mybatis.common.query.SQLField;
import io.choerodon.mybatis.common.query.Selection;
import io.choerodon.mybatis.entity.BaseDTO;
import io.choerodon.mybatis.entity.Criteria;
import io.choerodon.redis.CacheResolve;
import io.choerodon.web.core.IRequest;
import io.choerodon.web.core.impl.RequestHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import tk.mybatis.mapper.entity.EntityField;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author jialong.zuo@hand-china.com
 * @since 2017/5/31.
 */
@Order(7)
@Intercepts({@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),})
public class CacheJoinInterceptor implements Interceptor {

    private Logger logger = LoggerFactory.getLogger(CacheJoinInterceptor.class);

    private Map<String, CacheResolve> cacheJoinType;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object target = invocation.getTarget();
        if (target instanceof Executor) {
            MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
            if (!mappedStatement.getId().endsWith("selectOptions")) {
                return invocation.proceed();
            }
            Object domain = invocation.getArgs()[1];
            Criteria criteria = null;
            if (domain instanceof Map) {
                Map m = (Map) domain;
                if (m.containsKey(BaseConstants.OPTIONS_CRITERIA)) {
                    criteria = (Criteria) ((Map) domain).get(BaseConstants.OPTIONS_CRITERIA);
                }
                if (m.containsKey(BaseConstants.OPTIONS_DTO)) {
                    domain = ((Map) domain).get(BaseConstants.OPTIONS_DTO);
                }
            }
            if (domain instanceof BaseDTO) {
                BaseDTO dtoObj = (BaseDTO) domain;
                if (mappedStatement.getSqlCommandType() == SqlCommandType.SELECT) {
                    Object obj = invocation.proceed();
                    if (!mappedStatement.getId().contains("!")) {
                        setFieldValueFromCache(dtoObj, criteria, obj);
                    }
                    return obj;
                }
            }
        }
        return invocation.proceed();
    }

    //从缓存中查询缓存字段
    @SuppressWarnings("unchecked")
    private void setFieldValueFromCache(BaseDTO parameterObject, Criteria criteria, Object resultObject) throws Exception {
        criteria = criteria == null ? new Criteria() : criteria;
        Set<String> selectFields = null;
        Set<String> unSelectFields = criteria.getExcludeSelectFields();
        Set<Selection> selectionList = CollectionUtils.isEmpty(unSelectFields) ? criteria.getSelectFields() : null;
        if (selectionList != null) {
            selectFields = selectionList.stream().map(SQLField::getField).collect(Collectors.toSet());
        }

        Class<?> clazz = parameterObject.getClass();
        Map<String, Annotation> cacheEntityMap = new HashMap<>();
        cacheEntityMap.putAll(getEntityField(clazz, JoinCache.class, selectFields, unSelectFields));
        cacheEntityMap.putAll(getEntityField(clazz, JoinCode.class, selectFields, unSelectFields));
        cacheEntityMap.putAll(getEntityField(clazz, JoinLov.class, selectFields, unSelectFields));

        if (!cacheEntityMap.isEmpty() && resultObject instanceof ArrayList) {
            ArrayList resultList = (ArrayList) resultObject;
            for (Object aResultList : resultList) {
                //查询缓存操作
                addCacheColumn(aResultList, cacheEntityMap);
            }
        }
    }

    //获取缓存注解字段
    @SuppressWarnings("unchecked")
    public Map getEntityField(Class clazz, Class annotation, Set<String> selectFields, Set<String> unSelectFields) {
        Map cacheColumns = new HashMap<String, Annotation>();
        EntityField[] fieldsOfAnnotation = DTOClassInfo.getFieldsOfAnnotation(clazz, annotation);
        for (EntityField entityField : fieldsOfAnnotation) {
            String fieldName = entityField.getName();
            if (entityField.getAnnotation(annotation) instanceof JoinCode) {
                JoinCode code = (JoinCode) entityField.getAnnotation(annotation);
                fieldName = code.joinKey();
            } else if (entityField.getAnnotation(annotation) instanceof JoinCache) {
                JoinCache cache = (JoinCache) entityField.getAnnotation(annotation);
                fieldName = cache.joinKey();
            } else if (entityField.getAnnotation(annotation) instanceof JoinLov) {
                JoinLov cache = (JoinLov) entityField.getAnnotation(annotation);
                fieldName = cache.joinKey();
            }

            if (null != unSelectFields && !unSelectFields.isEmpty()) {
                if (unSelectFields.contains(fieldName)) {
                    continue;
                }
            } else {
                if (null != selectFields && !selectFields.isEmpty() && !selectFields.contains(fieldName)) {
                    continue;
                }
            }
            cacheColumns.put(entityField.getName(), entityField.getAnnotation(annotation));

        }
        return cacheColumns;
    }


    //将缓存中查出的值插入dto
    @SuppressWarnings("unchecked")
    private void addCacheColumn(Object resultMap, Map cacheEntityMap) {
        for (Map.Entry<String, Annotation> cacheEntity : (Iterable<Map.Entry<String, Annotation>>) cacheEntityMap.entrySet()) {
            String cacheKey = cacheEntity.getKey();
            try {
                Object columnValue = getColumnValueByCache(resultMap, cacheEntity);
                if (null == columnValue) {
                    continue;
                }
                Field column = resultMap.getClass().getDeclaredField(cacheKey);
                column.setAccessible(true);
                column.set(resultMap, columnValue);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                logger.error(e.getMessage(), e);
            }

        }
    }

    //从缓存中获取值
    private Object getColumnValueByCache(Object resultMap, Map.Entry<String, Annotation> cacheEntity) throws NoSuchFieldException, IllegalAccessException {
        IRequest iRequest = RequestHelper.getCurrentRequest(true);
        String lang = iRequest.getLocale();
        Object result;
        CacheResolve cacheResolve = null;
        if (cacheEntity.getValue() instanceof JoinCache) {
            JoinCache joinCache = (JoinCache) cacheEntity.getValue();
            cacheResolve = cacheJoinType.get(joinCache.cacheName());
        } else if (cacheEntity.getValue() instanceof JoinCode) {
            cacheResolve = cacheJoinType.get("_code");
        } else if (cacheEntity.getValue() instanceof JoinLov) {
            cacheResolve = cacheJoinType.get("_lov");
        }
        if (cacheResolve == null) {
            logger.warn("CacheResolve cannot get @JoinCache or @JoinCode or @JoinLov");
            return null;
        }
        result = cacheResolve.resolve(cacheEntity.getValue(), resultMap, lang);

        return result;
    }


    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {
    }

    public Map<String, CacheResolve> getCacheJoinType() {
        return cacheJoinType;
    }

    public void setCacheJoinType(Map<String, CacheResolve> cacheJoinType) {
        this.cacheJoinType = cacheJoinType;
    }
}
