package io.choerodon.hap.api.gateway.service.impl;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import io.choerodon.hap.api.ApiConstants;
import io.choerodon.hap.api.gateway.dto.ApiServer;
import io.choerodon.hap.api.gateway.service.IApiInvokeService;
import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.core.util.FreemarkerUtil;
import io.choerodon.hap.intergration.exception.HapApiException;
import io.choerodon.hap.intergration.util.JSONAndMap;
import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.message.WSSecUsernameToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * soap服务 映射调用Service - 实现类.
 *
 * @author peng.jiang@hand-china.com
 * @since 2017/9/25.
 */
@Service
public class ApiSoapInvokeServiceImpl implements IApiInvokeService, ProxySelf<IApiInvokeService> {

    private static final Logger logger = LoggerFactory.getLogger(ApiSoapInvokeServiceImpl.class);

    @Autowired
    HttpRequestAuthorization httpRequestAuthorization;

    @Override
    public String serverType() {
        return ApiConstants.SERVER_TYPE_SOAP;
    }

    @Override
    public Object invoke(ApiServer server, JSONObject inbound) throws Exception {
        logger.info("inbound:{}", inbound);
        String xml = translateSendXml(server, inbound);
        String data = self().apiInvoke(server, xml);
        return translateReponseData(data);
    }

    @Override
    public String apiInvoke(ApiServer server, String parameter) throws Exception {
        return sendRequest(server, parameter);
    }

    /**
     * 拼接请求的xml.
     *
     * @param server  服务
     * @param inbound 请求内容
     * @return 请求的xml
     * @throws Exception freemarker转换模板异常
     */
    private String translateSendXml(ApiServer server, JSONObject inbound) throws Exception {
        String xml = "";
        if (inbound != null) {
            if (server.getElementFormDefault().equals(ApiConstants.SERVER_ELEMENT_FORM_DEFAULT_QUALIFIED)) {
                xml = JSONAndMap.jsonToXml(inbound.toString(), ApiConstants.NAMESPACE_PRE + ":");
            } else {
                xml = JSONAndMap.jsonToXml(inbound.toString(), null);
            }
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(server.getApiInterface().getInterfaceUrl(), xml);
        xml = JSONAndMap.jsonToXml(jsonObject.toString(), ApiConstants.NAMESPACE_PRE + ":");
        Map<String, String> dataMap = new HashMap<>(10);
        dataMap.put("namespace", server.getNamespace());
        dataMap.put("soapBody", xml);
        if (server.getApiInterface().getSoapVersion().equals(ApiConstants.SOAP11)) {
            dataMap.put("soapNamespace", ApiConstants.SOAP11_NAMESPACE);
        } else {
            dataMap.put("soapNamespace", ApiConstants.SOAP12_NAMESPACE);
        }
        if (server.getWssPasswordType()
                .equals(ApiConstants.WSS_PASSWORD_TYPE_PASSWORD_TEXT)) {
            Document document = getDocument();
            // UsernameToken
            WSSecUsernameToken ut = new WSSecUsernameToken();
            ut.setPasswordType(WSConstants.PASSWORD_TEXT);
            ut.setUserInfo(server.getUsername(), server.getPassword());
            ut.prepare(document);
            dataMap.put("wssSecurity", getSecurityStr(ut.getUsernameTokenElement()));
        } else if (server.getWssPasswordType()
                .equals(ApiConstants.WSS_PASSWORD_TYPE_PASSWORD_DIGEST)) {
            Document document = getDocument();
            // UsernameToken
            WSSecUsernameToken ut = new WSSecUsernameToken();
            ut.setPasswordType(WSConstants.PASSWORD_DIGEST);
            ut.setUserInfo(server.getUsername(), server.getPassword());
            ut.addCreated();
            ut.addNonce();
            ut.prepare(document);
            dataMap.put("wssSecurity", getSecurityStr(ut.getUsernameTokenElement()));
        }

        try {
            xml = FreemarkerUtil.translateData(ApiConstants.SOAP_MESSAGE, dataMap);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new HapApiException(e.getMessage(), "Failed to generate the encoding, process variable fail");
        }

        return xml;
    }

    /**
     * 处理返回数据.
     *
     * @param data 返回数据（String）
     * @return 返回数据（JSONObject）
     * @throws Exception xml转换异常
     */
    private JSONObject translateReponseData(String data) throws Exception {
        JSONObject jsonObject;
        try {
            Map<String, Object> map = JSONAndMap.xml2map(data);
            jsonObject = JSONObject.fromObject(map);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new HapApiException(HapApiException.CODE_API_SYSTEM_EXCEPTION, HapApiException.ERROR_XML_TO_MAP);
        }
        return jsonObject;
    }

    /**
     * 获取加密报文.
     *
     * @param ut 节点
     * @return 加密保温
     */
    private String getSecurityStr(Element ut) {
        try (StringWriter writer = new StringWriter()) {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.transform(new DOMSource(ut), new StreamResult(writer));
            return ApiConstants.SOAP_SECURITY_PRE + writer.getBuffer().toString() + ApiConstants.SOAP_SECURTIY_SUF;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "";
    }

    private Document getDocument() throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        return dbf.newDocumentBuilder().newDocument();
    }

    /**
     * 用户把SOAP请求发送给服务器端，并返回服务器点返回的输入流.
     *
     * @param xml 把最终的xml传入
     * @return 服务器端返回的输入流，供客户端读取
     * @throws Exception
     */
    private String sendRequest(ApiServer server, String xml) throws Exception {

        CloseableHttpClient httpclient = HttpClients.createDefault();
        String requestUrl = translateRequestUrl(server);
        StringEntity entiy = new StringEntity(xml, "UTF-8");
        try {
            HttpPost httpPost = new HttpPost(requestUrl);
            httpPost.setEntity(entiy);
            httpRequestAuthorization.setHttpRequestAuthorization(httpPost, server, true);
            HttpResponse response = httpclient.execute(httpPost);

            // 更新token 重新请求 此时的token失效
            if (response.getStatusLine().getStatusCode() == ApiConstants.HTTP_RESPONSE_CODE_401
                    && ApiConstants.AUTH_TYPE_OAUTH2.equalsIgnoreCase(server.getAuthType())) {
                httpRequestAuthorization.updateToken(httpPost, server);
                response = httpclient.execute(httpPost);
            }

            String data = EntityUtils.toString(response.getEntity(), "UTF-8");

            if (response.getStatusLine().getStatusCode() != ApiConstants.HTTP_RESPONSE_CODE_200) {
                throw new HapApiException(HapApiException.CODE_API_THIRD_REQUEST + "_" + response.getStatusLine().getStatusCode(), data);
            }
            return data;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        } finally {
            httpclient.close();
        }

    }

    /**
     * 拼接请求Url.
     *
     * @param server 服务
     * @return 请求Url
     */
    private String translateRequestUrl(ApiServer server) {
        String requestUrl = "";
        if (server.getDomainUrl() != null) {
            requestUrl += server.getDomainUrl();
        }
        return requestUrl;
    }


}


