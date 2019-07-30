package io.choerodon.hap.code.rule.controllers;

import io.choerodon.hap.code.rule.dto.CodeRulesLine;
import io.choerodon.hap.code.rule.service.ICodeRulesLineService;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.mybatis.common.query.SortField;
import io.choerodon.mybatis.common.query.SortType;
import io.choerodon.mybatis.entity.Criteria;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.core.IRequest;
import io.choerodon.web.dto.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(value = {"/sys/code/rules/line", "/api/sys/code/rules/line"})
public class CodeRulesLineController extends BaseController {

    @Autowired
    private ICodeRulesLineService service;

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/query")
    public ResponseData query(CodeRulesLine dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        Criteria criteria = new Criteria(dto);
        criteria.setSortFields(Collections.singleton(new SortField(CodeRulesLine.FIELD_FIELD_SEQUENCE, SortType.ASC)));
        return new ResponseData(service.selectOptions(dto, criteria));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/submit")
    public ResponseData update(@RequestBody List<CodeRulesLine> dto, BindingResult result, HttpServletRequest request) {
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(dto));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/remove")
    public ResponseData delete(HttpServletRequest request, @RequestBody List<CodeRulesLine> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }
}