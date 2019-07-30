package io.choerodon.hap.api.gateway.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.hap.api.ApiConstants;
import io.choerodon.hap.api.gateway.dto.ApiInterface;
import io.choerodon.hap.api.gateway.dto.ApiServer;
import io.choerodon.hap.api.gateway.service.IApiImportService;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

/**
 * 导入rest服务 Service - 实现类.
 *
 * @author xiangyu.qi@hand-china.com
 * @since 2017/9/20.
 **/

@Service("restImportServer")
public class ApiRestImportServiceImpl implements IApiImportService {

    private final Logger logger = LoggerFactory.getLogger(ApiRestImportServiceImpl.class);

    private static final String SCHEMES = "schemes";
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public ApiServer importServer(ApiServer server) {
        String importUrl = server.getImportUrl();

        HttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(importUrl);
        HttpResponse response = null;
        try {
            response = httpclient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == ApiConstants.HTTP_RESPONSE_CODE_200) {
                String conResult = EntityUtils.toString(response.getEntity());
                JsonNode jsonNode = objectMapper.readValue(conResult, JsonNode.class);
                //parse server info
                String serverName = jsonNode.get(ApiConstants.SERVER_INFO).get(ApiConstants.SERVER_TITLE).asText();

                String url = null;
                url = null != url ? url : ApiConstants.SERVER_HTTP;
                String serverUrl = url + jsonNode.get(ApiConstants.SERVER_HOST).asText()
                        +jsonNode.get(ApiConstants.SERVER_BASEPATH).asText();
                // 服务代码
                server.setCode(StringUtils.replace(serverName, " ", ""));
                // 服务名称
                server.setName(serverName);
                // 服务类型
                server.setServiceType(ApiConstants.SERVER_TYPE_REST);
                // 服务地址
                server.setDomainUrl(serverUrl);
                // 映射地址
                server.setMappingUrl(server.getCode());
                //parse interface info
                List<ApiInterface> interfaceList = new ArrayList<>();
                Iterator<Map.Entry<String, JsonNode>> it = jsonNode.get(ApiConstants.SERVER_PATHS).fields();
                while (it.hasNext()){
                    Map.Entry<String, JsonNode> path = it.next();
                    Iterator<Map.Entry<String, JsonNode>> itM = path.getValue().fields();
                    int methodSzie = path.getValue().size();
                    //  没有指定访问方式时，取get post
                    boolean multiMethod =  ApiConstants.SERVER_METHOD_SIZE == methodSzie;
                    while (itM.hasNext()){
                        Map.Entry<String, JsonNode> method = itM.next();
                        String methodType = method.getKey();
                        boolean notPostAngGet = !ApiConstants.REQUEST_POST.equalsIgnoreCase(methodType)
                                && !ApiConstants.REQUEST_GET.equalsIgnoreCase(methodType);
                        if(multiMethod && notPostAngGet) {
                            continue;
                        }

                        ApiInterface ife = new ApiInterface();
                        String code = method.getValue().get(ApiConstants.SERVER_OPERATIONID).asText();
                        // 接口代码
                        ife.setCode(code.toUpperCase());
                        // 接口名称
                        ife.setName(method.getValue().get(ApiConstants.SERVER_SUMMARY).asText());
                        // 接口地址
                        ife.setInterfaceUrl(path.getKey());
                        // 映射地址
                        ife.setMappingUrl(code);
                        // 是否启用
                        ife.setEnableFlag("Y");
                        // 请求方式
                        ife.setRequestMethod(methodType.toUpperCase());
                        // 请求头
                        processRequestHeader(ife,method.getValue());

                        interfaceList.add(ife);
                    }
                }
               server.setInterfaces(interfaceList);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }
        return server;
    }
    /**
     * TODO: 处理更多SPRINGMVC 请求头参数
     */
    private void processRequestHeader(ApiInterface ife, JsonNode method) throws JsonProcessingException {
        List<Map<String,String>> requestHeader = new ArrayList<>();
        Map<String, String> map = new HashMap<>(1);
        JsonNode consumes =  method.get(ApiConstants.SERVER_CONSUMES);

        if(null != consumes) {
            if (consumes.isTextual()) {
                map.put(HTTP.CONTENT_TYPE, consumes.textValue());
                requestHeader.add(map);
            } else if (consumes.isArray()) {
                // content-type 多个时，有application/json 使用application/json;否则使用第一个.
                Iterator<JsonNode> itTemp = consumes.iterator();
                String contentType = null;
                boolean isFirst = true;
                while (itTemp.hasNext()) {
                    String value = itTemp.next().asText();
                    contentType = isFirst ? value : contentType;
                    isFirst = false;
                    if (ApiConstants.CONTENY_TYPE_JSON.equalsIgnoreCase(value)) {
                        contentType = value;
                    }
                }
                map = new HashMap<>(1);
                map.put(HTTP.CONTENT_TYPE, contentType);
                requestHeader.add(map);
            }
        } else {    // 默认为application/json
            map.put(HTTP.CONTENT_TYPE, ApiConstants.CONTENY_TYPE_JSON);
            requestHeader.add(map);
        }
        if(!requestHeader.isEmpty()) {
            ife.setRequestHead(objectMapper.writeValueAsString(requestHeader));
        }
    }
}
