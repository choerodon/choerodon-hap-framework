package io.choerodon.hap.message.components;

import io.choerodon.hap.cache.impl.RoleResourceCache;
import io.choerodon.message.IMessageConsumer;
import io.choerodon.message.annotation.TopicMonitor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色-资源二级缓存.
 *
 * @author Yinlijian
 */
@Component
@TopicMonitor(channel = DefaultRoleResourceListener.CACHE_ROLE_RESOURCE)
public class DefaultRoleResourceListener implements IMessageConsumer<Long> {

    public static final String CACHE_ROLE_RESOURCE = "cache.role.resource";

    @Autowired
    private RoleResourceCache roleResourceCache;

    private Map<Long, List<Long>> roleResourceMap = new HashMap<>();

    @Override
    public void onMessage(Long roleId, String pattern) {
        if (null == roleId) {
            roleResourceCache.reload();
            roleResourceMap.clear();
        } else {
            roleResourceCache.loadRoleResource(roleId);
            roleResourceMap.remove(roleId);
            Long[] resourceIs = roleResourceCache.getValue(roleId + "");
            if(ArrayUtils.isNotEmpty(resourceIs)) {
                roleResourceMap.put(roleId, Arrays.asList(resourceIs));
            }
        }
    }

    /**
     * 获取角色资源信息.
     *
     * @param roleId 角色ID
     * @return 角色资源ID集合
     */
    public List<Long> getRoleResource(Long roleId) {
        List<Long> resourceIds = roleResourceMap.get(roleId);
        if (CollectionUtils.isEmpty(resourceIds)) {
            Long[] ids = roleResourceCache.getValue(roleId + "");
            if(ArrayUtils.isNotEmpty(ids)) {
                resourceIds = Arrays.asList(ids);
                roleResourceMap.put(roleId, resourceIds);
            }
        }
        return resourceIds;
    }
}
