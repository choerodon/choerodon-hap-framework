package io.choerodon.hap.cache.impl;

import io.choerodon.hap.function.dto.ResourceItemElement;
import io.choerodon.hap.function.mapper.ResourceItemElementMapper;
import io.choerodon.redis.impl.HashStringRedisCache;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(value = "resourceItemElementCache")
public class ResourceItemElementCache extends HashStringRedisCache<ResourceItemElement[]> {
    private String queryAllSqlId = ResourceItemElementMapper.class.getName() + ".selectAll";
    private String querySqlId = ResourceItemElementMapper.class.getName() + ".selectByResourceItemId";

    private final Logger logger = LoggerFactory.getLogger(ResourceItemElementCache.class);

    {
        setLoadOnStartUp(true);
        setType(ResourceItemElement[].class);
        setName("resource_item_element");
    }


    /**
     * @param key resourceItemId
     * @return values ResourceItemElement[]
     */
    @Override
    public ResourceItemElement[] getValue(String key) {
        return super.getValue(key);
    }

    /**
     * @param key    resourceItemId
     * @param values ResourceItemElement[]
     */
    @Override
    public void setValue(String key, ResourceItemElement[] values) {
        super.setValue(key, values);
    }

    @SuppressWarnings("unchecked")
    protected void initLoad() {
        Map<String, List<ResourceItemElement>> resourceItemElementList = new HashMap<>();
        try (SqlSession sqlSession = getSqlSessionFactory().openSession()) {
            sqlSession.select(queryAllSqlId, (resultContext) -> {
                ResourceItemElement value = (ResourceItemElement) resultContext.getResultObject();
                String ownerResourceItemId = value.getResourceItemId().toString();
                List<ResourceItemElement> elementList = resourceItemElementList.get(ownerResourceItemId);
                if (elementList == null) {
                    elementList = new ArrayList<>();
                    resourceItemElementList.put(ownerResourceItemId, elementList);
                }
                elementList.add(value);
            });

            resourceItemElementList.forEach((k, v) -> {
                setValue(k, v.toArray(new ResourceItemElement[v.size()]));
            });
            if (logger.isDebugEnabled()) {
                logger.debug("successfully loaded all resource item element cache");
            }
        } catch (Throwable e) {
            if (logger.isErrorEnabled()) {
                logger.error("init resource item element cache exception: ", e);
            }
        }
    }

    public void load(Long resourceItemId) {
        Map<String, List<ResourceItemElement>> resourceItemElementList = new HashMap<>();
        super.remove(resourceItemId.toString());
        try (SqlSession sqlSession = getSqlSessionFactory().openSession()) {
            sqlSession.select(querySqlId, resourceItemId, (resultContext) -> {
                ResourceItemElement value = (ResourceItemElement) resultContext.getResultObject();
                List<ResourceItemElement> sets = resourceItemElementList.get(resourceItemId.toString());
                if (sets == null) {
                    sets = new ArrayList<>();
                    resourceItemElementList.put(resourceItemId.toString(), sets);
                }
                sets.add(value);
            });

            resourceItemElementList.forEach((k, v) -> {
                setValue(k, v.toArray(new ResourceItemElement[v.size()]));
            });
        } catch (Throwable e) {
            if (logger.isErrorEnabled()) {
                logger.error("load resource item element cache exception: ", e);
            }
        }
    }
}
