package io.choerodon.hap.fnd.controllers;

import io.choerodon.hap.fnd.dto.Company;
import io.choerodon.hap.fnd.service.ICompanyService;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.mybatis.common.query.Comparison;
import io.choerodon.mybatis.common.query.WhereField;
import io.choerodon.mybatis.entity.Criteria;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.dto.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 对公司的操作.
 *
 * @author jialong.zuo@hand-china.com
 * @since 2016/10/9.
 */
@RestController
@RequestMapping(value = {"/fnd/company", "/api/fnd/company"})
public class CompanyController extends BaseController {
    @Autowired
    private ICompanyService companyService;

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/query")
    public ResponseData select(Company company, HttpServletRequest request, HttpServletResponse response, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                               @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        Criteria criteria = new Criteria(company);
        criteria.where(new WhereField(Company.FIELD_COMPANY_CODE, Comparison.LIKE), Company.FIELD_COMPANY_ID, Company.FIELD_COMPANY_TYPE, Company.FIELD_PARENT_COMPANY_ID, Company.FIELD_COMPANY_FULL_NAME, Company.FIELD_COMPANY_SHORT_NAME);
        return new ResponseData(companyService.selectOptions(company, criteria, page, pagesize));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = {"/submit", "/update"})
    public ResponseData update(@RequestBody List<Company> companies, BindingResult result, HttpServletRequest request) {
        getValidator().validate(companies, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(companyService.batchUpdate(companies));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/remove")
    public ResponseData delete(@RequestBody List<Company> companies, HttpServletRequest request) {
        companyService.batchDelete(companies);
        return new ResponseData();
    }
}
