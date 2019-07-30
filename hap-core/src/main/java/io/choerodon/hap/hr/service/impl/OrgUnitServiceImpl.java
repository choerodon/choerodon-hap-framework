package io.choerodon.hap.hr.service.impl;

import io.choerodon.hap.hr.dto.HrOrgUnit;
import io.choerodon.hap.hr.service.IOrgUnitService;
import io.choerodon.hap.system.dto.DTOStatus;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.mybatis.common.query.Comparison;
import io.choerodon.mybatis.common.query.WhereField;
import io.choerodon.mybatis.entity.Criteria;
import io.choerodon.mybatis.service.BaseServiceImpl;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 部门组织服务接口实现.
 *
 * @author jialong.zuo@hand-china.com
 * @since 2016/9/16.
 */
@Service
@Dataset("OrgUnit")
public class OrgUnitServiceImpl extends BaseServiceImpl<HrOrgUnit> implements IOrgUnitService, IDatasetService<HrOrgUnit> {

    @Override
    protected boolean useSelectiveUpdate() {
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<HrOrgUnit> batchUpdate(List<HrOrgUnit> list) {
        Criteria criteria = new Criteria();
        criteria.update(HrOrgUnit.FIELD_NAME, HrOrgUnit.FIELD_UNIT_CATEGORY,
                HrOrgUnit.FIELD_UNIT_TYPE, HrOrgUnit.FIELD_PARENT_ID, HrOrgUnit.FIELD_DESCRIPTION,
                HrOrgUnit.FIELD_MANAGER_POSITION, HrOrgUnit.FIELD_COMPANY_ID, HrOrgUnit.FIELD_ENABLED_FLAG);
        criteria.updateExtensionAttribute();
        for (HrOrgUnit unit : list) {
            if (unit.get__status().equalsIgnoreCase(DTOStatus.ADD)) {
                self().insertSelective(unit);
            } else if (unit.get__status().equalsIgnoreCase(DTOStatus.UPDATE)) {
                self().updateByPrimaryKeyOptions(unit, criteria);
            } else if (unit.get__status().equalsIgnoreCase(DTOStatus.DELETE)) {
                self().deleteByPrimaryKey(unit);
            }
        }
        return list;
    }

    @Override
    public List<HrOrgUnit> queries(Map<String, Object> body, int page, int pageSize, String sortnName, boolean isDesc) {
        try {
            HrOrgUnit orgUnit = new HrOrgUnit();
            BeanUtils.populate(orgUnit, body);
            orgUnit.setSortname(sortnName);
            orgUnit.setSortorder(isDesc ? "desc" : "asc");
            Criteria criteria = new Criteria(orgUnit);
            criteria.where(new WhereField(HrOrgUnit.FIELD_UNIT_CODE, Comparison.LIKE), HrOrgUnit.FIELD_UNIT_ID, HrOrgUnit.FIELD_PARENT_ID, HrOrgUnit.FIELD_NAME, HrOrgUnit.FIELD_UNIT_TYPE);
            return self().selectOptions(orgUnit, criteria, page, pageSize);
        } catch (Exception e) {
            throw new DatasetException("dataset.error", e);
        }
    }

    @Override
    public List<HrOrgUnit> mutations(List<HrOrgUnit> list) {
        return batchUpdate(list);
    }
}
