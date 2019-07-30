package io.choerodon.hap.function.controllers;

import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.base.exception.BaseException;
import io.choerodon.hap.function.dto.Resource;
import io.choerodon.hap.function.dto.ResourceItem;
import io.choerodon.hap.function.service.IResourceItemService;
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
 * 对权限组件的操作.
 *
 * @author qiang.zeng@hand-china.com
 * @since 2017/6/30.
 */
@RestController
@RequestMapping(value = {"/sys/resourceItem", "/api/sys/resourceItem"})
public class ResourceItemController extends BaseController {

    @Autowired
    private IResourceItemService resourceItemService;

    @Permission(type = ResourceType.SITE)
    @RequestMapping(value = "/query", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseData queryResourceItems(HttpServletRequest request, Resource resource) {
        return new ResponseData(resourceItemService.selectResourceItems(createRequestContext(request), resource));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/submit")
    public ResponseData submitResourceItems(HttpServletRequest request, @RequestBody List<ResourceItem> resourceItems,
                                            BindingResult result) throws BaseException {
        getValidator().validate(resourceItems, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(resourceItemService.batchUpdate(resourceItems));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/remove")
    public ResponseData removeResourceItems(HttpServletRequest request, @RequestBody List<ResourceItem> resourceItems)
            throws BaseException {
        resourceItemService.batchDelete(resourceItems);
        return new ResponseData();
    }

}
