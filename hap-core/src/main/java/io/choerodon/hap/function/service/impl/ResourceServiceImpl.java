package io.choerodon.hap.function.service.impl;

import com.github.pagehelper.PageHelper;
import io.choerodon.hap.core.annotation.StdWho;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.function.dto.Function;
import io.choerodon.hap.function.dto.Resource;
import io.choerodon.hap.function.mapper.FunctionResourceMapper;
import io.choerodon.hap.function.mapper.ResourceMapper;
import io.choerodon.hap.function.service.IResourceCustomizationService;
import io.choerodon.hap.function.service.IResourceItemService;
import io.choerodon.hap.function.service.IResourceService;
import io.choerodon.hap.system.dto.DTOStatus;
import io.choerodon.mybatis.service.BaseServiceImpl;
import io.choerodon.base.util.BaseConstants;
import io.choerodon.redis.Cache;
import io.choerodon.redis.CacheManager;
import io.choerodon.web.core.IRequest;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * 资源服务接口实现.
 *
 * @author wuyichu
 * @author njq.niu@hand-china.com
 */
@Service
@Dataset("Resource")
public class ResourceServiceImpl extends BaseServiceImpl<Resource> implements IResourceService, IDatasetService<Resource> {
    private static final String CACHE_RESOURCE_URL = "resource_url";
    private static final String CACHE_RESOURCE_ID = "resource_id";

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private FunctionResourceMapper functionResourceMapper;

    @Autowired
    private IResourceItemService resourceItemService;

    @Autowired
    private IResourceCustomizationService resourceCustomizationService;


    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Resource selectResourceByUrl(String url) {
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        Cache<Resource> cache = getResourceByURL();
        Resource resource = cache.getValue(url);
        if (resource == null) {
            resource = resourceMapper.selectResourceByUrl(url);
            flushCache(resource);
        }
        return resource;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Resource selectResourceById(Long id) {
        if (id == null) {
            return null;
        }
        Cache<Resource> cache = getResourceById();
        Resource resource = cache.getValue(id.toString());
        if (resource == null) {
            resource = resourceMapper.selectByPrimaryKey(id);
            flushCache(resource);
        }
        return resource;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resource insertSelective(Resource resource) {
        String url = resource.getUrl();
        if (url.startsWith(BaseConstants.FORWARD_SLASH)) {
            resource.setUrl(url.substring(1));
        }
        resourceMapper.insertSelective(resource);
        flushCache(resource);
        return resource;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Resource> batchUpdate(List<Resource> resources) {
        for (Resource resource : resources) {
            if (resource.getResourceId() != null) {
                self().updateByPrimaryKeySelective(resource);
            } else {
                self().insertSelective(resource);
            }
        }
        return resources;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resource updateByPrimaryKeySelective(@StdWho Resource record) {
        String url = record.getUrl();
        if (url.startsWith(BaseConstants.FORWARD_SLASH)) {
            record.setUrl(url.substring(1));
        }
        record = super.updateByPrimaryKeySelective(record);
        flushCache(record);
        reloadFunction();
        return record;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByPrimaryKey(Resource resource) {
        if (resource == null || resource.getResourceId() == null || StringUtils.isEmpty(resource.getUrl())) {
            return 0;
        }
        int updateCount = resourceMapper.deleteByPrimaryKey(resource);
        checkOvn(updateCount, resource);
        functionResourceMapper.deleteByResource(resource);
        resourceItemService.deleteByResourceId(resource);
        resourceCustomizationService.deleteByResourceId(resource.getResourceId());
        removeCache(resource);
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDelete(IRequest requestContext, List<Resource> resources) {
        int result = 0;
        if (CollectionUtils.isEmpty(resources)) {
            return result;
        }
        for (Resource resource : resources) {
            self().deleteByPrimaryKey(resource);
            result++;
        }
        return result;
    }

    @Override
    public List<Resource> selectExcludePageByOptions(Resource resource) {
        return resourceMapper.selectExcludePageByOptions(resource);
    }

    @Override
    public List<Resource> selectPageByOptions(Resource resource) {
        return resourceMapper.selectPageByOptions(resource);
    }

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            Resource resource = new Resource();
            BeanUtils.populate(resource, body);
            resource.setSortname(sortname);
            String sortorder = isDesc ? "desc" : "asc";
            resource.setSortorder(sortorder);
            PageHelper.startPage(page, pageSize);
            return self().selectExcludePageByOptions(resource);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new DatasetException("dataset.error", e);
        }
    }

    @Override
    public List<Resource> mutations(List<Resource> objs) {
        for (Resource resource : objs) {
            switch (resource.get__status()) {
                case DTOStatus.ADD:
                    self().insertSelective(resource);
                    break;
                case DTOStatus.UPDATE:
                    self().updateByPrimaryKeySelective(resource);
                    break;
                case DTOStatus.DELETE:
                    self().deleteByPrimaryKey(resource);
                    break;
                default:
                    break;
            }
        }
        return objs;
    }


    /**
     * 获取资源URL缓存
     *
     * @return 资源URL缓存
     */
    private Cache<Resource> getResourceByURL() {
        return cacheManager.getCache(CACHE_RESOURCE_URL);
    }

    /**
     * 获取资源ID缓存
     *
     * @return 资源ID缓存
     */
    private Cache<Resource> getResourceById() {
        return cacheManager.getCache(CACHE_RESOURCE_ID);
    }

    /**
     * 获取功能的缓存
     *
     * @return
     */
    private Cache<Function> getFunction() {
        return cacheManager.getCache(Function.CACHE_FUNCTION);
    }

    /**
     * 更新资源URL缓存和资源ID缓存
     *
     * @param resource 资源
     */
    private void flushCache(Resource resource) {
        if (resource == null) {
            return;
        }
        Cache<Resource> resourceCache = getResourceByURL();
        resourceCache.setValue(resource.getUrl(), resource);
        Cache<Resource> resourceCache2 = getResourceById();
        resourceCache2.setValue(resource.getResourceId().toString(), resource);
    }

    /**
     * 删除资源URL缓存和资源ID缓存
     *
     * @param resource 资源
     */
    private void removeCache(Resource resource) {
        if (resource == null) {
            return;
        }
        Cache<Resource> resourceCache = getResourceByURL();
        resourceCache.remove(resource.getUrl());
        Cache<Resource> resourceCache2 = getResourceById();
        resourceCache2.remove(resource.getResourceId().toString());

        reloadFunction();
    }

    /**
     * 重新加载 Function 的缓存
     */
    private void reloadFunction() {
        Cache<Function> functionCache = getFunction();
        functionCache.reload();
    }
}
