package io.choerodon.hap.security.permission.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.security.permission.dto.DataPermissionTable;
import io.choerodon.hap.security.permission.dto.DataPermissionTableRule;
import io.choerodon.hap.security.permission.mapper.DataPermissionTableRuleMapper;
import io.choerodon.hap.security.permission.service.IDataPermissionTableService;
import io.choerodon.message.IMessagePublisher;
import io.choerodon.mybatis.common.query.Comparison;
import io.choerodon.mybatis.common.query.WhereField;
import io.choerodon.mybatis.entity.BaseDTO;
import io.choerodon.mybatis.entity.Criteria;
import io.choerodon.mybatis.service.BaseServiceImpl;

/**
 * @author jialong.zuo@hand-china.com
 */
@Service
@Dataset("PermissionTable")
public class DataPermissionTableServiceImpl extends BaseServiceImpl<DataPermissionTable> implements IDataPermissionTableService, IDatasetService<DataPermissionTable> {

    @Autowired
    DataPermissionTableRuleMapper dataPermissionTableRuleMapper;

    @Autowired
    IMessagePublisher iMessagePublisher;

    @Override
    public void removeTableWithRule(List<DataPermissionTable> dataMaskTables) {
        batchDelete(dataMaskTables);
        dataMaskTables.forEach(v -> {
            DataPermissionTableRule rule = new DataPermissionTableRule();
            rule.setTableId(v.getTableId());
            dataPermissionTableRuleMapper.delete(rule);
            iMessagePublisher.publish("dataPermission.tableRemove", v);
        });
    }

    public void deletePostFilter(DataPermissionTable table) {
        DataPermissionTableRule rule = new DataPermissionTableRule();
        rule.setTableId(table.getTableId());
        dataPermissionTableRuleMapper.delete(rule);
        iMessagePublisher.publish("dataPermission.tableRemove", table);
    }

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            DataPermissionTable example = new DataPermissionTable();
            BeanUtils.populate(example, body);
            Criteria criteria = new Criteria(example);
            criteria.where(new WhereField(DataPermissionTable.FIELD_TABLE_NAME, Comparison.LIKE), new WhereField(DataPermissionTable.FIELD_DESCRIPTION, Comparison.LIKE));
            return selectOptions(example, criteria, page, pageSize);
        } catch (Exception e) {
            throw new DatasetException("dataset.error", e);
        }
    }

    @Override
    public List<DataPermissionTable> mutations(List<DataPermissionTable> objs) {
        batchUpdate(objs);
        for (DataPermissionTable table : objs) {
            if (BaseDTO.STATUS_DELETE.equals(table.get__status())) {
                deletePostFilter(table);
            }
        }
        return objs;
    }
}