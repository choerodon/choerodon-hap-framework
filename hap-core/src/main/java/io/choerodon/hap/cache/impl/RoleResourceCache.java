package io.choerodon.hap.cache.impl;

import io.choerodon.hap.function.mapper.RoleFunctionMapper;
import io.choerodon.redis.impl.HashStringRedisCache;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 角色资源缓存.
 *
 * @author wuyichu
 */
@Component(value = "roleResourceCache")
public class RoleResourceCache extends HashStringRedisCache<Long[]> {

    private String roleResourceQuerySqlId = RoleFunctionMapper.class.getName() + ".selectAllRoleResources";

    private String roleResourcesSqlId = RoleFunctionMapper.class.getName() + ".selectRoleResources";

    private final Logger logger = LoggerFactory.getLogger(RoleResourceCache.class);

    {
        setLoadOnStartUp(true);
        setType(Long[].class);
        setName("role_resource");
    }


    /**
     * key 为roleId.
     *
     * @param key roleId
     * @return values
     */
    @Override
    public Long[] getValue(String key) {
        return super.getValue(key);
    }

    /*
     * .
     * 
     * @see
     * HashStringRedisCache#setValue(java.lang.String,
     * java.lang.Object)
     */
    @Override
    public void setValue(String key, Long[] values) {
        super.setValue(key, values);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void initLoad() {
        Map<String, Set<Long>> roleResources = new HashMap<>();
        try (SqlSession sqlSession = getSqlSessionFactory().openSession()) {
            sqlSession.select(roleResourceQuerySqlId, (resultContext) -> {
                Map<String, Object> value = (Map<String, Object>) resultContext.getResultObject();
                String roleId = "" + value.get("ROLE_ID");
                Set<Long> sets = roleResources.computeIfAbsent(roleId, k -> new HashSet<>());
                Long resourceId = ((Number) value.get("RESOURCE_ID")).longValue();
                sets.add(resourceId);
            });

            roleResources.forEach((k, v) -> {
                setValue(k, v.toArray(new Long[v.size()]));
            });
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("init role resource cache exception: ", e);
            }
        }
    }

    /**
     * 按照roleId加载资源.
     *
     * @param roleId 角色id
     */
    @SuppressWarnings("unchecked")
    public void loadRoleResource(Long roleId) {
        super.remove(roleId.toString());
        Map<Long, Set<Long>> roleResources = new HashMap<>();
        try (SqlSession sqlSession = getSqlSessionFactory().openSession()) {
            sqlSession.select(roleResourcesSqlId, roleId, (resultContext) -> {
                Map<String, Object> value = (Map<String, Object>) resultContext.getResultObject();
                Set<Long> sets = roleResources.computeIfAbsent(roleId, k -> new HashSet<>());
                Long resourceId = ((Number) value.get("RESOURCE_ID")).longValue();
                sets.add(resourceId);
            });

            roleResources.forEach((k, v) -> {
                setValue(k.toString(), v.toArray(new Long[v.size()]));
            });
        } catch (RuntimeException e) {
            if (logger.isErrorEnabled()) {
                logger.error("loadRoleResource exception: ", e);
            }
        }
    }
}
