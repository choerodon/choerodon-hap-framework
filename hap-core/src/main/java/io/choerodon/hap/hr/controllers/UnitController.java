package io.choerodon.hap.hr.controllers;

import io.choerodon.hap.hr.dto.HrOrgUnit;
import io.choerodon.hap.hr.service.IOrgUnitService;
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
import java.util.List;

/**
 * 对部门的操作.
 *
 * @author jialong.zuo@hand-china.com
 * @since 2016/9/16.
 */
@RestController
@RequestMapping(value = {"/hr/unit", "/api/hr/unit"})
public class UnitController extends BaseController {

    @Autowired
    private IOrgUnitService service;

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/query")
    public ResponseData query(HttpServletRequest request, HrOrgUnit unit,
                              @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        Criteria criteria = new Criteria(unit);
        criteria.where(new WhereField(HrOrgUnit.FIELD_UNIT_CODE, Comparison.LIKE), HrOrgUnit.FIELD_UNIT_ID, HrOrgUnit.FIELD_PARENT_ID, HrOrgUnit.FIELD_NAME, HrOrgUnit.FIELD_UNIT_TYPE);
        return new ResponseData(service.selectOptions(unit, criteria, page, pagesize));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/queryall")
    public ResponseData queryAllUnits(HttpServletRequest request) {
        Criteria criteria = new Criteria();
        criteria.select(HrOrgUnit.FIELD_NAME, HrOrgUnit.FIELD_UNIT_CODE, HrOrgUnit.FIELD_DESCRIPTION, HrOrgUnit.FIELD_UNIT_ID, HrOrgUnit.FIELD_PARENT_ID);
        criteria.selectExtensionAttribute();
        HrOrgUnit unit = new HrOrgUnit();
        unit.setEnabledFlag("Y");
        return new ResponseData(service.selectOptions(unit, criteria));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/submit")
    public ResponseData update(HttpServletRequest request, @RequestBody List<HrOrgUnit> units, final BindingResult result) {
        getValidator().validate(units, result);
        if (result.hasErrors()) {
            ResponseData rd = new ResponseData(false);
            rd.setMessage(getErrorMessage(result, request));
            return rd;
        }
        return new ResponseData(service.batchUpdate(units));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/remove")
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HrOrgUnit> units) {
        service.batchDelete(units);
        return new ResponseData();
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/checkunitcode")
    public boolean CheckUnitCode(HttpServletRequest request, String unitCode) {
        HrOrgUnit unit = new HrOrgUnit();
        unit.setUnitCode(unitCode);
        return service.select(unit, 1, 10).size() <= 0;
    }

}
