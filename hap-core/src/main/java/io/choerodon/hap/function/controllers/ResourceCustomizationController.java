package io.choerodon.hap.function.controllers;

import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.base.exception.BaseException;
import io.choerodon.hap.function.dto.ResourceCustomization;
import io.choerodon.hap.function.service.IResourceCustomizationService;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.dto.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 对资源合并配置项的操作.
 *
 * @author zhizheng.yang@hand-china.com
 */
@RestController
@RequestMapping(value = {"/sys/resourceCustomization", "/api/sys/resourceCustomization"})
public class ResourceCustomizationController extends BaseController {

    @Autowired
    private IResourceCustomizationService resourceCustomizationService;

    @Permission(type = ResourceType.SITE)
    @RequestMapping(value = "/query", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseData query(HttpServletRequest request, Long resourceId) {
        return new ResponseData(resourceCustomizationService.selectResourceCustomizationsByResourceId(resourceId));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/submit")
    public ResponseData submit(HttpServletRequest request, @RequestBody List<ResourceCustomization> resourceCustomizations,
                               BindingResult result) throws BaseException {
        getValidator().validate(resourceCustomizations, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(resourceCustomizationService.batchUpdate(resourceCustomizations));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/remove")
    public ResponseData remove(HttpServletRequest request, @RequestBody List<ResourceCustomization> resourceCustomizations)
            throws BaseException {
        resourceCustomizationService.batchDelete(resourceCustomizations);
        return new ResponseData();
    }

}
