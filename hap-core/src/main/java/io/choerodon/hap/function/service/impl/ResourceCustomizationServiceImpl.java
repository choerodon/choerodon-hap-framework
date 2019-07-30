package io.choerodon.hap.function.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.hap.cache.impl.ResourceCustomizationCache;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.function.dto.ResourceCustomization;
import io.choerodon.hap.function.mapper.ResourceCustomizationMapper;
import io.choerodon.hap.function.service.IResourceCustomizationService;
import io.choerodon.hap.system.dto.DTOStatus;
import io.choerodon.mybatis.service.BaseServiceImpl;
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
 * 资源合并配置服务接口实现.
 *
 * @author zhizheng.yang@hand-china.com
 */
@Service
@Dataset("ResourceCustomization")
public class ResourceCustomizationServiceImpl extends BaseServiceImpl<ResourceCustomization> implements IResourceCustomizationService, IDatasetService<ResourceCustomization> {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ResourceCustomizationMapper resourceCustomizationMapper;

    @Autowired
    private ResourceCustomizationCache resourceCustomizationCache;


    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<ResourceCustomization> selectResourceCustomizationsByResourceId(Long resourceId) {
        return resourceCustomizationMapper.selectResourceCustomizationsByResourceId(resourceId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByResourceId(Long resourceId) {
        int result = 0;
        List<ResourceCustomization> resourceCustomizations = resourceCustomizationMapper.selectResourceCustomizationsByResourceId(resourceId);
        if (CollectionUtils.isNotEmpty(resourceCustomizations)) {
            result = super.batchDelete(resourceCustomizations);
            resourceCustomizationCache.remove(resourceCustomizations.get(0).getResourceId().toString());
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ResourceCustomization> batchUpdate(List<ResourceCustomization> list) {
        List<ResourceCustomization> result = super.batchUpdate(list);
        if (list.size() > 0) {
            resourceCustomizationCache.load(list.get(0).getResourceId().toString());
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDelete(List<ResourceCustomization> list) {
        int size = super.batchDelete(list);
        if (list.size() > 0) {
            resourceCustomizationCache.load(list.get(0).getResourceId().toString());
        }
        return size;
    }

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            ResourceCustomization resourceCustomization = new ResourceCustomization();
            BeanUtils.populate(resourceCustomization, body);
            return resourceCustomizationMapper.selectResourceCustomizationsByResourceId(resourceCustomization.getResourceId());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new DatasetException("dataset.error.resourceCustomization", e);
        }
    }

    @Override
    public List<ResourceCustomization> mutations(List<ResourceCustomization> objs) {
        for (ResourceCustomization customization : objs) {
            switch (customization.get__status()) {
                case DTOStatus.ADD:
                    super.insertSelective(customization);
                    break;
                case DTOStatus.UPDATE:
                    super.updateByPrimaryKeySelective(customization);
                    break;
                case DTOStatus.DELETE:
                    super.deleteByPrimaryKey(customization);
                    break;
                default:
                    break;
            }
        }
        if (objs.size() > 0) {
            resourceCustomizationCache.load(objs.get(0).getResourceId().toString());
        }
        return objs;
    }
}
