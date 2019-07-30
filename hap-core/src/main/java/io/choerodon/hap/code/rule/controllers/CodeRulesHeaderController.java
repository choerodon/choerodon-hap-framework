package io.choerodon.hap.code.rule.controllers;

import io.choerodon.hap.code.rule.dto.CodeRulesHeader;
import io.choerodon.hap.code.rule.service.ICodeRulesHeaderService;
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

@RestController
@RequestMapping(value = {"/sys/code/rules/header", "/api/sys/code/rules/header"})
public class CodeRulesHeaderController extends BaseController {

    @Autowired
    private ICodeRulesHeaderService service;

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/query")
    public ResponseData query(CodeRulesHeader dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        Criteria criteria = new Criteria(dto);
        criteria.where(new WhereField(CodeRulesHeader.FIELD_HEADER_ID), new WhereField(CodeRulesHeader.FIELD_RULE_CODE, Comparison.LIKE), new WhereField(CodeRulesHeader.FIELD_RULE_NAME, Comparison.LIKE));
        return new ResponseData(service.selectOptions(dto, criteria, page, pageSize));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/submit")
    public ResponseData update(@RequestBody List<CodeRulesHeader> dto, BindingResult result,
                               HttpServletRequest request) {
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
    public ResponseData delete(HttpServletRequest request, @RequestBody List<CodeRulesHeader> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }
}