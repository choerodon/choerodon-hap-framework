package io.choerodon.hap.api.gateway.service.impl;

import io.choerodon.hap.api.ApiConstants;
import io.choerodon.hap.api.gateway.dto.ApiServer;
import io.choerodon.hap.api.gateway.service.IApiImportService;
import io.choerodon.hap.api.gateway.WsdlParser;
import org.springframework.stereotype.Service;

/**
 * soap服务导入 service - 实现类.
 *
 * @author peng.jiang@hand-china.com
 * @since 2017/9/20.
 */
@Service("soapImportServer")
public class ApiSoapImportServiceImpl implements IApiImportService {


    @Override
    public ApiServer importServer(ApiServer srServer) {

        ApiServer result = new ApiServer();
        result.setCode(srServer.getCode());
        result.setServiceType(ApiConstants.SERVER_TYPE_SOAP);
        try {
            WsdlParser wsdlParse = new WsdlParser();
            result = wsdlParse.parseWSDL( result , srServer.getImportUrl());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}