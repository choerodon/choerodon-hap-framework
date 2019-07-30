package io.choerodon.hap.api.gateway.controllers;

import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.hap.api.gateway.dto.ApiServer;
import io.choerodon.hap.api.gateway.service.IApiInvokeService;
import io.choerodon.hap.api.gateway.service.IApiServerService;
import io.choerodon.hap.api.gateway.service.impl.ApiInvokeServiceManager;
import io.choerodon.hap.api.logs.dto.ApiResponseData;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

/**
 * 访问映射控制器.
 *
 * @author lijian.yin@hand-china.com
 **/

@RestController
public class ApiInvokeContoller {

    @Autowired
    private ApiInvokeServiceManager srInvokeServiceManager;

    @Autowired
    private IApiServerService serverService;

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/api/rest/{serverUrl}/{interfaceUrl}")
    public ApiResponseData sentRequest(HttpServletRequest request, @PathVariable String serverUrl, @PathVariable String interfaceUrl,
                                       @RequestBody(required = false) JSONObject inbound) throws Exception {
        ApiServer server = serverService.getByMappingUrl(serverUrl, interfaceUrl);

        ApiResponseData apiResponseData = new ApiResponseData();
        List<IApiInvokeService> invokeServices = srInvokeServiceManager.getInvokeServices();
        for (IApiInvokeService service : invokeServices) {
            if (service.matchServerType(server.getServiceType())) {
                Object result = service.invoke(server, inbound);
                apiResponseData.setRows(Collections.singletonList(result));
                break;
            }
        }
        return apiResponseData;
    }
}
