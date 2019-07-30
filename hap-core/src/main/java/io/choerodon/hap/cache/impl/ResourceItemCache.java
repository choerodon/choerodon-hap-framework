package io.choerodon.hap.cache.impl;

import io.choerodon.hap.function.dto.ResourceItem;
import io.choerodon.hap.function.mapper.ResourceItemMapper;
import io.choerodon.redis.impl.HashStringRedisCache;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author:qiang.zeng on 2017/3/9.
 */
@Component(value = "resourceItemCache")
public class ResourceItemCache extends HashStringRedisCache<ResourceItem[]> {
    private String resourceItemQueryAllSqlId = ResourceItemMapper.class.getName() + ".selectForCache";
    private String resourceItemQuerySqlId = ResourceItemMapper.class.getName() + ".selectResourceItemsByResourceId";

    private final Logger logger = LoggerFactory.getLogger(ResourceItemCache.class);

    {
        setLoadOnStartUp(true);
        setType(ResourceItem[].class);
        setName("resource_item");
    }


    /**
     * @param key resourceId
     * @return values ResourceItem[]
     */
    @Override
    public ResourceItem[] getValue(String key) {
        return super.getValue(key);
    }

    /**
     * @param key    resourceId
     * @param values ResourceItem[]
     */
    @Override
    public void setValue(String key, ResourceItem[] values) {
        super.setValue(key, values);
    }

    @SuppressWarnings("unchecked")
    protected void initLoad() {
        Map<String, List<ResourceItem>> resourceResourceItems = new HashMap<>();
        try (SqlSession sqlSession = getSqlSessionFactory().openSession()) {
            sqlSession.select(resourceItemQueryAllSqlId, (resultContext) -> {
                ResourceItem value = (ResourceItem) resultContext.getResultObject();
                String ownerResourceId = value.getOwnerResourceId().toString();
                List<ResourceItem> resourceItems = resourceResourceItems.get(ownerResourceId);
                if (resourceItems == null) {
                    resourceItems = new ArrayList<>();
                    resourceResourceItems.put(ownerResourceId, resourceItems);
                }
                resourceItems.add(value);
            });

            resourceResourceItems.forEach((k, v) -> {
                setValue(k, v.toArray(new ResourceItem[v.size()]));
            });
            if (logger.isDebugEnabled()) {
                logger.debug("successfully loaded all resource item cache");
            }
        } catch (Throwable e) {
            if (logger.isErrorEnabled()) {
                logger.error("init resource item cache exception: ", e);
            }
        }
    }

    public void load(String resourceId) {
        Map<String, List<ResourceItem>> resourceResourceItems = new HashMap<>();
        super.remove(resourceId);
        try (SqlSession sqlSession = getSqlSessionFactory().openSession()) {
            sqlSession.select(resourceItemQuerySqlId, Long.parseLong(resourceId), (resultContext) -> {
                ResourceItem value = (ResourceItem) resultContext.getResultObject();
                List<ResourceItem> sets = resourceResourceItems.get(resourceId);
                if (sets == null) {
                    sets = new ArrayList<>();
                    resourceResourceItems.put(resourceId, sets);
                }
                sets.add(value);
            });

            resourceResourceItems.forEach((k, v) -> {
                setValue(k, v.toArray(new ResourceItem[v.size()]));
            });
        } catch (Throwable e) {
            if (logger.isErrorEnabled()) {
                logger.error("load resource item cache exception: ", e);
            }
        }
    }
}
