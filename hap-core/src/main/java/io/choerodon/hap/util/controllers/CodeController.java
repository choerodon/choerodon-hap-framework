package io.choerodon.hap.util.controllers;

import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.hap.excel.annotation.ExcelExport;
import io.choerodon.hap.util.dto.Code;
import io.choerodon.hap.util.dto.CodeValue;
import io.choerodon.hap.util.service.ICodeService;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.dto.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

/**
 * 快速编码Controller.
 *
 * @author njq.niu@hand-china.com
 * @since 2016年3月2日
 */
@Controller
@RequestMapping(value = {"/sys", "/api/sys"})
public class CodeController extends BaseController {

    @Autowired
    private ICodeService codeService;

    /**
     * 获取快速编码对象.
     *
     * @param code     Code
     * @param page     起始页
     * @param pagesize 分页大小
     * @param request  HttpServletRequest
     * @return ResponseData
     */
    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @PostMapping(value = "/code/query")
    @ExcelExport(table = Code.class)
    @ResponseBody
    public ResponseData getCodes(Code code, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                 @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request, HttpServletResponse httpServletResponse) {
        return new ResponseData(codeService.selectCodes(code, page, pagesize));
    }

    /**
     * 查询快速编码值.
     *
     * @param value   CodeValue
     * @param request HttpServletRequest
     * @return ResponseData
     */
    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @PostMapping(value = "/codevalue/query")
    @ResponseBody
    public ResponseData getCodeValues(CodeValue value, HttpServletRequest request) {
        return new ResponseData(codeService.selectCodeValues(value));
    }

    /**
     * 删除快速编码.
     *
     * @param codes   codes
     * @param request HttpServletRequest
     * @return ResponseData
     */
    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/code/remove")
    public ResponseData removeCodes(@RequestBody List<Code> codes, HttpServletRequest request) {
        codeService.batchDelete(codes);
        return new ResponseData();
    }

    /**
     * 删除快速编码值.
     *
     * @param values  values
     * @param request HttpServletRequest
     * @return ResponseData
     */
    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/codevalue/remove")
    public ResponseData removeValues(@RequestBody List<CodeValue> values, HttpServletRequest request) {
        codeService.batchDeleteValues(values);
        return new ResponseData();
    }

    /**
     * 提交快速编码对象.
     *
     * @param codes   codes
     * @param result  BindingResult
     * @param request HttpServletRequest
     * @return ResponseData
     */
    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/code/submit")
    public ResponseData submitCode(@RequestBody List<Code> codes, BindingResult result, HttpServletRequest request) {
        getValidator().validate(codes, result);
        if (result.hasErrors()) {
            ResponseData rd = new ResponseData(false);
            rd.setMessage(getErrorMessage(result, request));
            return rd;
        }
        return new ResponseData(codeService.batchUpdate(codes));
    }

    /**
     * 根据parentCode,value,获取子快码列表
     *
     * @param parentCode 父快码的code
     * @param value      父快码的某快码value
     * @param request    HttpServletRequest
     * @return ResponseData
     */
    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @PostMapping(value = "/code/queryChildCodeValue")
    @ResponseBody
    public ResponseData getChildCodes(@RequestParam String parentCode,
                                      @RequestParam String value, HttpServletRequest request) {
        return new ResponseData(codeService.getChildCodeValue(parentCode, value));
    }

    /**
     * 根据parentCode,value,获取子快码列表
     *
     * @param codeValue 子快码
     * @param request   HttpServletRequest
     * @return ResponseData
     */
    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @PostMapping(value = "/code/getCodeValueById")
    @ResponseBody
    public ResponseData getCodeValueById(@RequestBody CodeValue codeValue, HttpServletRequest request) {
        return new ResponseData(Collections.singletonList(codeService.getCodeValueById(codeValue.getCodeValueId())));
    }
}
