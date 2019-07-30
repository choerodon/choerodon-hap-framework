package io.choerodon.hap.flexfield.controllers;

import io.choerodon.hap.flexfield.dto.ColumnName;
import io.choerodon.hap.flexfield.dto.FlexModelColumn;
import io.choerodon.hap.flexfield.service.IFlexModelColumnService;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.dto.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(value = {"/fnd/flex", "/api/fnd/flex"})
public class FlexModelColumnController extends BaseController {

    @Autowired
    private IFlexModelColumnService service;

    @Permission(type = ResourceType.SITE)
    @RequestMapping(value = "/model/column/query", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseData query(FlexModelColumn dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        return new ResponseData(service.selectOptions(dto, null, page, pageSize));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/model/column/submit")
    public ResponseData update(@RequestBody List<FlexModelColumn> dto, HttpServletRequest request, BindingResult result) {
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(dto));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/model/column/remove")
    public ResponseData delete(HttpServletRequest request, @RequestBody List<FlexModelColumn> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }

    @Permission(type = ResourceType.SITE)
    @RequestMapping(value = "/column/queryAll", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseData getTableColumn(HttpServletRequest request, String tableName) {
        return new ResponseData(service.getTableColumn(tableName));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/column/queryAllObj")
    public List<ColumnName> getTableColumnObj(HttpServletRequest request, String tableName) {
        return service.getTableColumnObj(tableName);
    }
}