package io.choerodon.hap.function.service.impl;

import io.choerodon.hap.cache.impl.ResourceItemElementCache;
import io.choerodon.hap.core.annotation.StdWho;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.function.dto.ResourceItemElement;
import io.choerodon.hap.function.mapper.ResourceItemAssignMapper;
import io.choerodon.hap.function.mapper.ResourceItemElementMapper;
import io.choerodon.hap.function.service.IResourceItemElementService;
import io.choerodon.hap.system.dto.DTOStatus;
import io.choerodon.mybatis.service.BaseServiceImpl;
import io.choerodon.web.core.IRequest;
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
 * 权限组件元素服务接口实现.
 *
 * @author qiang.zeng
 */
@Service
@Dataset("ResourceItemElement")
public class ResourceItemElementServiceImpl extends BaseServiceImpl<ResourceItemElement> implements IResourceItemElementService, IDatasetService<ResourceItemElement> {
    @Autowired
    private ResourceItemElementMapper elementMapper;
    @Autowired
    private ResourceItemElementCache elementCache;
    @Autowired
    private ResourceItemAssignMapper itemAssignMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResourceItemElement insertSelective(@StdWho ResourceItemElement element) {
        if (null == element) {
            return null;
        }
        super.insertSelective(element);
        elementCache.load(element.getResourceItemId());
        return element;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResourceItemElement updateByPrimaryKey(ResourceItemElement element) {
        if (null == element) {
            return null;
        }
        super.updateByPrimaryKey(element);
        elementCache.load(element.getResourceItemId());
        return element;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByPrimaryKey(ResourceItemElement element) {
        int updateCount = elementMapper.deleteByPrimaryKey(element);
        checkOvn(updateCount, element);
        int ret = itemAssignMapper.deleteByElementId(element.getElementId());
        elementCache.load(element.getResourceItemId());
        return ret;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ResourceItemElement> batchUpdate(List<ResourceItemElement> elementList) {
        if (CollectionUtils.isEmpty(elementList)) {
            return elementList;
        }
        for (ResourceItemElement element : elementList) {
            if (element.getElementId() == null) {
                self().insertSelective(element);
            } else {
                self().updateByPrimaryKey(element);
            }
        }
        return elementList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDelete(IRequest requestContext, List<ResourceItemElement> elementList) {
        int result = 0;
        if (CollectionUtils.isEmpty(elementList)) {
            return result;
        }
        for (ResourceItemElement element : elementList) {
            int updateCount = elementMapper.deleteByPrimaryKey(element);
            checkOvn(updateCount, element);
            itemAssignMapper.deleteByElementId(element.getElementId());
            result++;
        }
        elementCache.load(elementList.get(0).getResourceItemId());
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<ResourceItemElement> selectByResourceItemId(IRequest requestContext, ResourceItemElement element) {
        return elementMapper.selectByResourceItemId(element);
    }

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            ResourceItemElement resourceItemElement = new ResourceItemElement();
            BeanUtils.populate(resourceItemElement, body);
            return elementMapper.selectByResourceItemId(resourceItemElement);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new DatasetException("dataset.error.resourceItemElement", e);
        }
    }

    @Override
    public List<ResourceItemElement> mutations(List<ResourceItemElement> objs) {
        for (ResourceItemElement customization : objs) {
            switch (customization.get__status()) {
                case DTOStatus.ADD:
                    self().insertSelective(customization);
                    break;
                case DTOStatus.UPDATE:
                    self().updateByPrimaryKey(customization);
                    break;
                case DTOStatus.DELETE:
                    self().deleteByPrimaryKey(customization);
                    break;
                default:
                    break;
            }
        }
        return objs;
    }
}