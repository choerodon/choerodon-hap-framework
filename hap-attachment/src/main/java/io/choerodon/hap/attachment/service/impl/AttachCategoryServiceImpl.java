package io.choerodon.hap.attachment.service.impl;

import io.choerodon.hap.attachment.dto.AttachCategory;
import io.choerodon.hap.attachment.exception.CategorySourceTypeRepeatException;
import io.choerodon.hap.attachment.exception.StoragePathNotExsitException;
import io.choerodon.hap.attachment.mapper.AttachCategoryMapper;
import io.choerodon.hap.attachment.service.IAttachCategoryService;
import io.choerodon.hap.core.annotation.StdWho;
import io.choerodon.hap.core.util.FormatUtil;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.system.dto.DTOStatus;
import io.choerodon.web.core.IRequest;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 附件类型service.
 *
 * @author hua.xiao@hand-china.com
 */
@Service
@Transactional(rollbackFor = Exception.class)
@Dataset("AttachCategory")
public class AttachCategoryServiceImpl implements IAttachCategoryService, IDatasetService<AttachCategory> {

    /**
     * 找不到对应的路径.
     **/
    public static final String CATEGORY_PATH_NOT_CORRECT = "category_path_not_correct";

    @Autowired
    private AttachCategoryMapper categoryMapper;

    @Override
    public AttachCategory insert(IRequest requestContext, @StdWho AttachCategory category) {
        if (StringUtils.isBlank(category.getStatus())) {
            category.setStatus(AttachCategory.STATUS_NORMAL);
        }
        // 更新其父节点不为空
        AttachCategory parent = categoryMapper.selectByPrimaryKey(category.getParentCategoryId());
        if (parent != null && AttachCategory.LEAF_TRUE.equals(parent.getLeafFlag())) {
            parent.setLeafFlag(AttachCategory.LEAF_FALSE);
            categoryMapper.updateByPrimaryKeySelective(parent);
        }
        categoryMapper.insertSelective(category);
        category.setPath(String.valueOf(category.getCategoryId()));
        if (category.getParentCategoryId() != null) {
            AttachCategory p1 = new AttachCategory();
            p1.setCategoryId(category.getParentCategoryId());
            p1 = categoryMapper.selectByPrimaryKey(p1);
            if (p1 != null) {
                category.setPath(p1.getPath() + "." + category.getCategoryId());
            }
        }
        // update path
        categoryMapper.updateByPrimaryKeySelective(category);
        return category;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public AttachCategory selectAttachByCode(IRequest requestContext, String sourceCode) {
        if (StringUtils.isBlank(sourceCode)) {
            return null;
        }
        AttachCategory params = new AttachCategory();
        params.setSourceType(sourceCode);
        params.setStatus(AttachCategory.STATUS_NORMAL);
        return categoryMapper.selectCategory(params);
    }

    /*
     * 根据sourceType 查找绝对路径 比如：/home/attachment/image/2015/1
     *
     * @see IAttachCategoryService#selectPathByCode( IRequest, java.lang.String)
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public String selectPathByCode(IRequest requestContext, String sourceType) throws StoragePathNotExsitException {
        AttachCategory params = new AttachCategory();
        params.setSourceType(sourceType);
        AttachCategory category = categoryMapper.selectCategory(params);

        if (category == null || category.getCategoryPath().length() <= 0) {
            throw new StoragePathNotExsitException();
        }
        return category.getCategoryPath();
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<AttachCategory> selectCategories(AttachCategory category) {
        return categoryMapper.select(category);
    }

    @Override
    public List<AttachCategory> batchUpdate(IRequest requestCtx, @StdWho List<AttachCategory> categories) {
        for (AttachCategory category : categories) {

            if (category.getCategoryId() == null) {
                self().insert(requestCtx, category);
            } else {
                categoryMapper.updateByPrimaryKeySelective(category);
            }
        }
        return categories;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public AttachCategory selectByPrimaryKey(IRequest requestContext, Long categoryId) {
        return categoryMapper.selectByPrimaryKey(categoryId);
    }

    @Override
    public boolean remove(AttachCategory category) {
        category = categoryMapper.selectByPrimaryKey(category.getCategoryId());
        if (category == null || AttachCategory.LEAF_FALSE.equals(category.getLeafFlag())) {
            return false;
        } else {
            category.setStatus(AttachCategory.STATUS_DELETED);
            categoryMapper.updateByPrimaryKeySelective(category);
            return true;
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<AttachCategory> selectCategories(IRequest requestContext, AttachCategory category) {
        List<AttachCategory> categories = categoryMapper.select(category);
        categories.forEach(c -> {
            String fileSize = FormatUtil.formatFileSize(c.getAllowedFileSize());
            if (!"0".equalsIgnoreCase(fileSize))
                c.setAllowedFileSizeDesc(fileSize);
        });
        return categories;
    }

    @Override
    public void validateType(List<AttachCategory> categories) throws CategorySourceTypeRepeatException {
        for (AttachCategory category : categories) {
            if (category.getCategoryId() == null && categoryMapper.countSourceType(category.getSourceType()) > 0) {
                throw new CategorySourceTypeRepeatException(category.getSourceType());
            }
        }
    }

    public List<AttachCategory> selectCategoryBreadcrumbList(IRequest requestContext, Long categoryId) {

        AttachCategory a1 = new AttachCategory();
        a1.setCategoryId(categoryId);

        AttachCategory current = categoryMapper.selectByPrimaryKey(a1);

        String path = "";
        if (current != null) {
            path = StringUtils.trimToEmpty(current.getPath());
        }
        List<Long> idList = new ArrayList<>();
        idList.add(-1L);
        String[] paths = StringUtils.split(path, ".");
        for (int i = 0; i < paths.length; i++) {
            idList.add(Long.parseLong(paths[i]));
        }

        List<AttachCategory> cates = categoryMapper.selectAllParentCategory(idList);
        if (cates != null && !cates.isEmpty()) {
            cates.sort((a, b) -> {
                if (a.getCategoryId().equals(b.getParentCategoryId())) {
                    return -1;
                }
                if (a.getParentCategoryId().equals(b.getCategoryId())) {
                    return 1;
                }
                return 0;
            });
            cates.forEach(a -> {
                a.setCategoryName(StringUtils.abbreviate(a.getCategoryName(), 15));
            });
        }
        return cates;
    }

    @Override
    public List<AttachCategory> queryTree(IRequest requestContext, AttachCategory category) {
        List<AttachCategory> list = categoryMapper.queryTree(category);
        for (AttachCategory attachCategory : list) {
            if (Objects.equals(attachCategory.getParentCategoryId(), -1L)) {
                attachCategory.setParentCategoryId(null);
            }
        }
        return list;
    }

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            AttachCategory category = new AttachCategory();
            BeanUtils.populate(category, body);
            if (category.getParentCategoryId() == null) {
                category.setParentCategoryId(AttachCategory.NO_PARENT);
            }
            category.setSortname(AttachCategory.FIELD_CATEGORY_NAME);
            category.setSortorder("ASC");
            return self().selectCategories(null, category);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new DatasetException("dataset.error", e);
        }
    }

    @Override
    public List<AttachCategory> mutations(List<AttachCategory> objs) {
        for (AttachCategory category : objs) {
            switch (category.get__status()) {
                case DTOStatus.ADD:
                    self().insert(null, category);
                    break;
                case DTOStatus.UPDATE:
                    categoryMapper.updateByPrimaryKeySelective(category);
                    break;
                default:
                    break;
            }
        }
        return objs;
    }
}
