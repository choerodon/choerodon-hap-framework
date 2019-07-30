package io.choerodon.hap.util.service.impl;

import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.util.dto.LovItem;
import io.choerodon.hap.util.mapper.LovItemMapper;
import io.choerodon.hap.util.service.ILovItemDataSet;
import io.choerodon.hap.util.service.ILovService;
import io.choerodon.mybatis.service.BaseServiceImpl;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * @author vista
 * @since 18-11-27 下午3:52
 */
@Service
@Dataset("LovItem")
@Transactional(rollbackFor = Exception.class)
public class LovItemDataSetImpl extends BaseServiceImpl<LovItem> implements ILovItemDataSet, IDatasetService<LovItem> {
    @Autowired
    private ILovService lovService;
    @Autowired
    private LovItemMapper mapper;

    @Override
    public List<LovItem> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            LovItem item = new LovItem();
            BeanUtils.populate(item, body);
            return lovService.selectLovItems(item);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new DatasetException("dataset.error", e);
        }
    }

    @Override
    public List<LovItem> mutations(List<LovItem> objs) {
        for (LovItem item : objs) {
            lovService.deleteLovItem(item);
        }
        return objs;
    }

    @Override
    public List<LovItem> selectByLovCode(String lovCode) {
        return mapper.selectByLovCode(lovCode);
    }
}
