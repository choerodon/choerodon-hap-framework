package io.choerodon.hap.flexfield.controllers;

import io.choerodon.hap.flexfield.dto.FlexModel;
import io.choerodon.hap.flexfield.service.IFlexModelService;
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
@RequestMapping(value = {"/fnd/flex/model", "/api/fnd/flex/model"})
public class FlexModelController extends BaseController {

    @Autowired
    private IFlexModelService service;

    @Permission(type = ResourceType.SITE)
    @RequestMapping(value = "/query", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseData query(FlexModel dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        return new ResponseData(service.select(dto, page, pageSize));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/submit")
    public ResponseData update(@RequestBody List<FlexModel> dto, HttpServletRequest request, BindingResult result) {
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
    public ResponseData delete(HttpServletRequest request, @RequestBody List<FlexModel> dto) {
        service.deleteFlexModel(dto);
        return new ResponseData();
    }
}