package io.choerodon.hap.security.permission.service.impl;

import com.github.pagehelper.PageHelper;
import io.choerodon.hap.security.permission.dto.DataPermissionRuleDetail;
import io.choerodon.hap.security.permission.mapper.DatasetMapper;
import io.choerodon.hap.security.permission.service.IDataPermissionRuleDetailService;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.mybatis.entity.BaseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Dataset("PermissionRuleDetailRole")
public class DataPermissionRuleDetailRoleServiceImpl implements IDatasetService<DataPermissionRuleDetail> {

    @Autowired
    private IDataPermissionRuleDetailService dataPermissionRuleDetailService;
    @Autowired
    private DatasetMapper datasetMapper;

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        PageHelper.startPage(page, pageSize);
        return datasetMapper.selectRuleDetailRole(body);
    }

    @Override
    public List<DataPermissionRuleDetail> mutations(List<DataPermissionRuleDetail> objs) {
        dataPermissionRuleDetailService.batchUpdate(objs);
        for (DataPermissionRuleDetail detail : objs){
            switch (detail.get__status()){
                case BaseDTO.STATUS_ADD:
                    dataPermissionRuleDetailService.updateCache(detail.getRuleId());
                    break;
                case BaseDTO.STATUS_UPDATE:
                    dataPermissionRuleDetailService.updateCache(detail.getRuleId());
                    break;
                case BaseDTO.STATUS_DELETE:
                    dataPermissionRuleDetailService.deletePostFilter(detail.getDetailId(), detail.getRuleId());
                    break;
            }
        }
        return objs;
    }


}
