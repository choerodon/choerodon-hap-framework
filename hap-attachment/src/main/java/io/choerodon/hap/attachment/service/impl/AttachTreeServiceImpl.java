package io.choerodon.hap.attachment.service.impl;

import io.choerodon.hap.attachment.dto.AttachCategory;
import io.choerodon.hap.attachment.service.IAttachCategoryService;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author jiameng.cao
 * @since 2019/1/10
 */

@Service
@Transactional(rollbackFor = Exception.class)
@Dataset("AttachTree")
public class AttachTreeServiceImpl implements IDatasetService<AttachCategory> {

    @Autowired
    private IAttachCategoryService iAttachCategoryService;

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        AttachCategory attachCategory = new AttachCategory();
        try {
            BeanUtils.populate(attachCategory, body);
            return iAttachCategoryService.queryTree(null, attachCategory);
        } catch (Exception e) {
            throw new DatasetException("dataset.error", e);
        }
    }

    @Override
    public List<AttachCategory> mutations(List<AttachCategory> objs) {
        return null;
    }
}
