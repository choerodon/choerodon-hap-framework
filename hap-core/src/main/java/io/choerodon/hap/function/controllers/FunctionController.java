package io.choerodon.hap.function.controllers;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.hap.excel.dto.ColumnInfo;
import io.choerodon.hap.excel.dto.ExportConfig;
import io.choerodon.hap.excel.service.IExportService;
import io.choerodon.hap.function.dto.Function;
import io.choerodon.hap.function.dto.Resource;
import io.choerodon.hap.function.service.IFunctionService;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.mybatis.common.query.Comparison;
import io.choerodon.mybatis.common.query.WhereField;
import io.choerodon.mybatis.entity.Criteria;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.core.IRequest;
import io.choerodon.web.dto.ResponseData;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 对功能的操作.
 *
 * @author wuyichu
 * @author njq.niu@hand-china.com
 * @author xiawang.liu@hand-china.com
 */
@RestController
@RequestMapping(value = {"/sys/function", "/api/sys/function"})
public class FunctionController extends BaseController {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private IExportService exportService;

    @Autowired
    private IFunctionService functionService;

    @Permission(type = ResourceType.SITE)
    @RequestMapping(value = "/export")
    public void createXLS(HttpServletRequest request, @RequestParam String config,
                          HttpServletResponse httpServletResponse) throws IOException {
        IRequest requestContext = createRequestContext(request);
        JavaType type = objectMapper.getTypeFactory().constructParametrizedType(ExportConfig.class, ExportConfig.class, Function.class, ColumnInfo.class);
        ExportConfig<Function, ColumnInfo> exportConfig = objectMapper.readValue(config, type);
        exportService.exportAndDownloadExcel("FunctionMapper.selectAll", exportConfig, request, httpServletResponse);

    }

    @Permission(type = ResourceType.SITE)
    @RequestMapping(value = "/fetchResource", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseData fetchResource(final HttpServletRequest request, final Long functionId, final Integer isExit,
                                      final Resource resource, @RequestParam(defaultValue = DEFAULT_PAGE) final int page,
                                      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) final int pagesize) {
        Function function = new Function();
        function.setFunctionId(functionId);
        return new ResponseData(functionService.selectExitResourcesByFunction(function, resource, page, pagesize));
    }

    @Permission(type = ResourceType.SITE)
    @RequestMapping(value = "/fetchNotResource", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseData fetchNotResource(final HttpServletRequest request, final Long functionId, final Integer isExit,
                                         final Resource resource, @RequestParam(defaultValue = DEFAULT_PAGE) final int page,
                                         @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) final int pagesize) {
        Function function = new Function();
        if (StringUtils.isEmpty(resource.getUrl())) {
            resource.setUrl(null);
        }
        if (StringUtils.isEmpty(resource.getName())) {
            resource.setName(null);
        }
        if (StringUtils.isEmpty(resource.getType())) {
            resource.setType(null);
        }
        function.setFunctionId(functionId);
        return new ResponseData(functionService.selectNotExitResourcesByFunction(function, resource, page, pagesize));
    }

    @Permission(type = ResourceType.SITE)
    @RequestMapping(value = "/menus", method = {RequestMethod.GET, RequestMethod.POST})
    public Object queryMenuTree(HttpServletRequest request) {
        return functionService.selectRoleFunctions();
    }

    @Permission(type = ResourceType.SITE)
    @RequestMapping(value = "/query", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseData query(final Function function, @RequestParam(defaultValue = DEFAULT_PAGE) final int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) final int pagesize, final HttpServletRequest request) {
        Criteria criteria = new Criteria(function);
        criteria.where(new WhereField(Function.FIELD_FUNCTION_CODE, Comparison.LIKE), Function.FIELD_FUNCTION_NAME, Function.FIELD_PARENT_FUNCTION_NAME, Function.FIELD_PARENT_FUNCTION_ID, new WhereField(Function.FIELD_MODULE_CODE, Comparison.LIKE), Function.FIELD_RESOURCE_ID);
        return new ResponseData(functionService.selectOptions(function, criteria, page, pagesize));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/remove")
    public ResponseData remove(@RequestBody final List<Function> functions, final BindingResult result,
                               final HttpServletRequest request) {
        functionService.batchDelete(functions);
        return new ResponseData();
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/submit")
    public ResponseData submit(@RequestBody final List<Function> functions, final BindingResult result,
                               final HttpServletRequest request) {
        getValidator().validate(functions, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(functionService.batchUpdate(functions));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/updateFunctionResource")
    public ResponseData updateFunctionResource(final HttpServletRequest request, @RequestBody final Function function) {
        ResponseData data = new ResponseData();
        functionService.updateFunctionResources(function, function.getResources());
        data.setSuccess(true);
        return data;
    }
}
