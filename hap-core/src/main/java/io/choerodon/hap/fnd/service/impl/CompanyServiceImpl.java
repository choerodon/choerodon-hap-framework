package io.choerodon.hap.fnd.service.impl;

import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.fnd.dto.Company;
import io.choerodon.hap.fnd.service.ICompanyService;
import io.choerodon.hap.system.dto.DTOStatus;
import io.choerodon.mybatis.service.BaseServiceImpl;
import io.choerodon.mybatis.common.query.Comparison;
import io.choerodon.mybatis.common.query.WhereField;
import io.choerodon.mybatis.entity.Criteria;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static io.choerodon.hap.system.dto.DTOStatus.ADD;
import static io.choerodon.hap.system.dto.DTOStatus.DELETE;
import static io.choerodon.hap.system.dto.DTOStatus.UPDATE;

/**
 * 公司服务接口实现.
 *
 * @author jialong.zuo@hand-china.com
 * @since 2016/10/9.
 */
@Service
@Dataset("Company")
@Transactional(rollbackFor = Exception.class)
public class CompanyServiceImpl extends BaseServiceImpl<Company> implements ICompanyService, IDatasetService<Company> {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Company> batchUpdate(List<Company> list) {
        Criteria criteria = new Criteria();
        criteria.update(Company.FIELD_COMPANY_SHORT_NAME, Company.FIELD_COMPANY_FULL_NAME, Company.FIELD_COMPANY_TYPE
                , Company.FIELD_COMPANY_LEVEL_ID, Company.FIELD_PARENT_COMPANY_ID, Company.FIELD_CHIEF_POSITION_ID,
                Company.FIELD_START_DATE_ACTIVE, Company.FIELD_END_DATE_ACTIVE, Company.FIELD_ZIPCODE, Company.FIELD_FAX,
                Company.FIELD_PHONE, Company.FIELD_CONTACT_PERSON, Company.FIELD_ADDRESS);
        criteria.updateExtensionAttribute();
        for (Company company : list) {
            if (company.get__status().equalsIgnoreCase(DTOStatus.UPDATE)) {
                self().updateByPrimaryKeyOptions(company, criteria);
            } else {
                insertSelective(company);
            }
        }
        return list;
    }


    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            Company company = new Company();
            BeanUtils.populate(company, body);
            Criteria criteria = new Criteria(company);
            criteria.where(new WhereField(Company.FIELD_COMPANY_CODE, Comparison.LIKE), Company.FIELD_COMPANY_ID, Company.FIELD_COMPANY_TYPE, Company.FIELD_PARENT_COMPANY_ID, Company.FIELD_COMPANY_FULL_NAME, Company.FIELD_COMPANY_SHORT_NAME);
            return super.selectOptions(company, criteria, page, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DatasetException("dataset.error", e);
        }
    }

    @Override
    public List<Company> mutations(List<Company> companies) {
        for (Company company : companies) {
            switch (company.get__status()) {
                case ADD:
                    self().batchUpdate(Collections.singletonList(company));
                    break;
                case DELETE:
                    super.deleteByPrimaryKey(company);
                    break;
                case UPDATE:
                    self().batchUpdate(Collections.singletonList(company));
                    break;
            }
        }
        return companies;
    }

}
