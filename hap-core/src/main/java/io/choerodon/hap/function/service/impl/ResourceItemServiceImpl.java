package io.choerodon.hap.function.service.impl;

import io.choerodon.hap.cache.impl.ResourceItemCache;
import io.choerodon.hap.cache.impl.ResourceItemElementCache;
import io.choerodon.hap.core.annotation.StdWho;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.function.dto.Resource;
import io.choerodon.hap.function.dto.ResourceItem;
import io.choerodon.hap.function.mapper.ResourceItemAssignMapper;
import io.choerodon.hap.function.mapper.ResourceItemElementMapper;
import io.choerodon.hap.function.mapper.ResourceItemMapper;
import io.choerodon.hap.function.mapper.RoleResourceItemMapper;
import io.choerodon.hap.function.service.IResourceItemService;
import io.choerodon.hap.system.dto.DTOStatus;
import io.choerodon.mybatis.service.BaseServiceImpl;
import io.choerodon.web.core.IRequest;
import io.choerodon.web.core.impl.RequestHelper;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * 权限组件服务接口实现.
 *
 * @author njq.niu@hand-china.com
 * @author qiang.zeng@hand-china.com
 * @since 2016年4月7日
 */
@Service
@Dataset("ResourceItem")
public class ResourceItemServiceImpl extends BaseServiceImpl<ResourceItem> implements IResourceItemService, IDatasetService<ResourceItem> {

    @Autowired
    private ResourceItemMapper resourceItemMapper;
    @Autowired
    private RoleResourceItemMapper roleResourceItemMapper;
    @Autowired
    private ResourceItemCache resourceItemCache;
    @Autowired
    private ResourceItemElementMapper resourceItemElementMapper;
    @Autowired
    private ResourceItemAssignMapper resourceItemAssignMapper;
    @Autowired
    private ResourceItemElementCache resourceItemElementCache;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResourceItem insertSelective(@StdWho ResourceItem resourceItem) {
        if (null == resourceItem) {
            return null;
        }
        super.insertSelective(resourceItem);
        resourceItemCache.load(resourceItem.getOwnerResourceId().toString());
        return resourceItem;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResourceItem updateByPrimaryKey(ResourceItem resourceItem) {
        if (null == resourceItem) {
            return null;
        }
        super.updateByPrimaryKey(resourceItem);
        resourceItemCache.load(resourceItem.getOwnerResourceId().toString());
        return resourceItem;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<ResourceItem> selectResourceItems(IRequest request, Resource resource) {
        return resourceItemMapper.selectResourceItemsByResourceId(resource);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ResourceItem> batchUpdate(List<ResourceItem> resourceItems) {
        if (CollectionUtils.isEmpty(resourceItems)) {
            return resourceItems;
        }
        for (ResourceItem resourceItem : resourceItems) {
            if (resourceItem.getResourceItemId() == null) {
                self().insertSelective(resourceItem);
            } else {
                self().updateByPrimaryKey(resourceItem);
            }
        }
        return resourceItems;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDelete(IRequest requestContext, List<ResourceItem> resourceItems) {
        int result = 0;
        if (CollectionUtils.isEmpty(resourceItems)) {
            return result;
        }
        for (ResourceItem resourceItem : resourceItems) {
            int updateCount = resourceItemMapper.deleteByPrimaryKey(resourceItem);
            checkOvn(updateCount, resourceItem);
            roleResourceItemMapper.deleteByResourceItemId(resourceItem.getResourceItemId());
            resourceItemAssignMapper.deleteByResourceItemId(resourceItem.getResourceItemId());
            resourceItemElementMapper.deleteByResourceItemId(resourceItem.getResourceItemId());
            resourceItemCache.load(resourceItem.getOwnerResourceId().toString());
            resourceItemElementCache.remove(resourceItem.getResourceItemId().toString());
            result++;
        }
        return result;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByPrimaryKey(ResourceItem resourceItem) {
        int updateCount = resourceItemMapper.deleteByPrimaryKey(resourceItem);
        checkOvn(updateCount, resourceItem);
        roleResourceItemMapper.deleteByResourceItemId(resourceItem.getResourceItemId());
        resourceItemAssignMapper.deleteByResourceItemId(resourceItem.getResourceItemId());
        resourceItemElementMapper.deleteByResourceItemId(resourceItem.getResourceItemId());
        resourceItemCache.load(resourceItem.getOwnerResourceId().toString());
        resourceItemElementCache.remove(resourceItem.getResourceItemId().toString());
        return updateCount;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public ResourceItem selectResourceItemByResourceIdAndItemId(ResourceItem resourceItem) {
        return resourceItemMapper.selectResourceItemByResourceIdAndItemId(resourceItem);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByResourceId(Resource resource) {
        int result = 0;
        List<ResourceItem> resourceItems = resourceItemMapper.selectResourceItemsByResourceId(resource);
        if (CollectionUtils.isNotEmpty(resourceItems)) {
            IRequest request = RequestHelper.newEmptyRequest();
            result = self().batchDelete(request, resourceItems);
        }
        return result;
    }

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            Resource resource = new Resource();
            BeanUtils.populate(resource, body);
            return resourceItemMapper.selectResourceItemsByResourceId(resource);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new DatasetException("dataset.error.resourceItem", e);
        }
    }

    @Override
    public List<ResourceItem> mutations(List<ResourceItem> objs) {
        for (ResourceItem resourceItem : objs) {
            switch (resourceItem.get__status()) {
                case DTOStatus.ADD:
                    self().insertSelective(resourceItem);
                    break;
                case DTOStatus.UPDATE:
                    self().updateByPrimaryKey(resourceItem);
                    break;
                case DTOStatus.DELETE:
                    self().deleteByPrimaryKey(resourceItem);
                    break;
                default:
                    break;
            }
        }
        return objs;
    }

}
