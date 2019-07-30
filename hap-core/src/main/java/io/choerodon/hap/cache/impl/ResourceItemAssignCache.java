package io.choerodon.hap.cache.impl;

import io.choerodon.hap.function.dto.ResourceItemAssign;
import io.choerodon.hap.function.mapper.ResourceItemAssignMapper;
import io.choerodon.redis.impl.HashStringRedisCache;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(value = "resourceItemAssignCache")
public class ResourceItemAssignCache extends HashStringRedisCache<ResourceItemAssign[]> {
    private String queryAllSqlId = ResourceItemAssignMapper.class.getName() + ".selectAll";
    private String querySqlId = ResourceItemAssignMapper.class.getName() + ".selectByTypeId";

    private final Logger logger = LoggerFactory.getLogger(ResourceItemAssignCache.class);

    {
        setLoadOnStartUp(true);
        setType(ResourceItemAssign[].class);
        setName("resource_item_assign");
    }


    /**
     * @param key typeId
     * @return values resourceItemAssigns
     */
    @Override
    public ResourceItemAssign[] getValue(String key) {
        return super.getValue(key);
    }

    /**
     * @param key    typeId
     * @param values values resourceItemAssigns
     */
    @Override
    public void setValue(String key, ResourceItemAssign[] values) {
        super.setValue(key, values);
    }

    @SuppressWarnings("unchecked")
    protected void initLoad() {
        Map<String, List<ResourceItemAssign>> assignResourceItems = new HashMap<>();
        try (SqlSession sqlSession = getSqlSessionFactory().openSession()) {
            sqlSession.select(queryAllSqlId, (resultContext) -> {
                ResourceItemAssign value = (ResourceItemAssign) resultContext.getResultObject();
                String key = value.getAssignType() + "_" + value.getTypeId().toString();
                List<ResourceItemAssign> resourceItems = assignResourceItems.get(key);
                if (resourceItems == null) {
                    resourceItems = new ArrayList<>();
                    assignResourceItems.put(key, resourceItems);
                }
                resourceItems.add(value);
            });

            assignResourceItems.forEach((k, v) -> {
                setValue(k, v.toArray(new ResourceItemAssign[v.size()]));
            });
            if (logger.isDebugEnabled()) {
                logger.debug("successfully loaded all assign resource item cache");
            }
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("init assign resource item cache exception: ", e);
            }
        }
    }

    public void load(String key) {
        Map<String, List<ResourceItemAssign>> assignResourceItems = new HashMap<>();
        super.remove(key);
        try (SqlSession sqlSession = getSqlSessionFactory().openSession()) {
            sqlSession.select(querySqlId, setResourceItemAssign(key), (resultContext) -> {
                ResourceItemAssign value = (ResourceItemAssign) resultContext.getResultObject();
                List<ResourceItemAssign> sets = assignResourceItems.get(key);
                if (sets == null) {
                    sets = new ArrayList<>();
                    assignResourceItems.put(key, sets);
                }
                sets.add(value);
            });

            assignResourceItems.forEach((k, v) -> {
                setValue(k, v.toArray(new ResourceItemAssign[v.size()]));
            });
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("load assign resource item cache exception: ", e);
            }
        }
    }

    private ResourceItemAssign setResourceItemAssign(String key) {
        String[] assignArr = key.split("_");
        ResourceItemAssign assign = new ResourceItemAssign();
        assign.setAssignType(assignArr[0]);
        assign.setTypeId(Long.parseLong(assignArr[1]));
        return assign;
    }
}
