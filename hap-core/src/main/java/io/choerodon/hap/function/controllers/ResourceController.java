package io.choerodon.hap.function.controllers;

import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.base.exception.BaseException;
import io.choerodon.hap.function.dto.Resource;
import io.choerodon.hap.function.service.IResourceService;
import io.choerodon.mybatis.entity.Criteria;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.core.IRequest;
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

/**
 * 对资源的操作.
 *
 * @author xiawang.liu@hand-china.com
 * @author zhizheng.yang@hand-china.com
 * @author njq.niu@hand-china.com
 */

@RestController
@RequestMapping(value = {"/sys/resource", "/api/sys/resource"})
public class ResourceController extends BaseController {

    @Autowired
    private IResourceService resourceService;

    @Permission(type = ResourceType.SITE)
    @RequestMapping(value = "/query", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseData queryResource(HttpServletRequest request, Resource resource,
                                      @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        Criteria criteria = new Criteria(resource);
        criteria.select(Resource.FIELD_RESOURCE_ID, Resource.FIELD_URL, Resource.FIELD_TYPE, Resource.FIELD_NAME, Resource.FIELD_LOGIN_REQUIRE, Resource.FIELD_ACCESS_CHECK, Resource.FIELD_DESCRIPTION);
        criteria.selectExtensionAttribute();
        return new ResponseData(resourceService.selectOptions(resource, criteria, page, pagesize));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/submit")
    public ResponseData submitResource(HttpServletRequest request, @RequestBody List<Resource> resources,
                                       BindingResult result) throws BaseException {
        getValidator().validate(resources, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(resourceService.batchUpdate(resources));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/remove")
    public ResponseData removeResource(HttpServletRequest request, @RequestBody List<Resource> resources)
            throws BaseException {
        IRequest requestContext = createRequestContext(request);
        resourceService.batchDelete(requestContext, resources);
        return new ResponseData();
    }

}
