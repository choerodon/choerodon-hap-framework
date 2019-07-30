package io.choerodon.hap.intergration.service.impl;

import io.choerodon.hap.intergration.beans.HapInvokeInfo;
import io.choerodon.hap.intergration.dto.HapInterfaceHeader;
import io.choerodon.hap.intergration.exception.HapApiException;
import io.choerodon.hap.intergration.service.IHapApiService;
import io.choerodon.hap.intergration.service.IHapAuthenticationService;
import io.choerodon.hap.intergration.util.JSONAndMap;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.util.StringUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

/**
 * Created by user on 2016/7/29.
 */
@Service(value = "soapBean")
public class HapSoapApiServiceImpl implements IHapApiService {

    private static final Logger logger = LoggerFactory.getLogger(HapSoapApiServiceImpl.class);

    @Autowired
    private IHapAuthenticationService authenticationService;

    public Map soapSend(HapInterfaceHeader headerAndLineDTO, String xml) throws Exception {

        // 测试使用
        // xml = getSoapRequest("上海");
        // url = "http://www.webxml.com.cn/WebServices/WeatherWebService.asmx";

        StringBuffer data = new StringBuffer();
        try {
            InputStream is = getSoapInputStream(xml, headerAndLineDTO);// 得到输入流
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                data.append(line);
            }
            bufferedReader.close();
            is.close();

        } catch (Exception e) {
            throw e;
        }
        Map<String, Object> map = null;
        try {
            map = JSONAndMap.xml2map(data.toString());
            HapInvokeInfo.OUTBOUND_RESPONSE_DATA.set(data.toString());
        } catch (Exception e) {
            throw new HapApiException(HapApiException.ERROR_XML_TO_MAP, "ERROR XML 2 MAP");
        }
        return map;
    }

    /**
     * 用户把SOAP请求发送给服务器端，并返回服务器点返回的输入流
     *
     * @param xml 把最终的xml传入
     * @return 服务器端返回的输入流，供客户端读取
     * @throws Exception
     */
    public InputStream getSoapInputStream(String xml, HapInterfaceHeader headerAndLineDTO) throws Exception {
        try {
            String requestUrl = headerAndLineDTO.getDomainUrl() + headerAndLineDTO.getIftUrl();
            URL url = new URL(requestUrl);
            URLConnection conn = url.openConnection();
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Length", Integer.toString(xml.length()));
            conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            if (!StringUtil.isEmpty(headerAndLineDTO.getSoapAction())) {
                conn.setRequestProperty("SOAPAction", headerAndLineDTO.getSoapAction());
            }

            String basicBase64;
            if ("Y".equalsIgnoreCase(headerAndLineDTO.getAuthFlag())) {
                if (HapAuthenticationServiceImpl.AUTH_TPYE_BASIC.equalsIgnoreCase(headerAndLineDTO.getAuthType())) {
                    String e1 = headerAndLineDTO.getAuthUsername() + ":" + headerAndLineDTO.getAuthPassword();
                    basicBase64 = new String(Base64.encodeBase64(e1.getBytes()));
                    conn.setRequestProperty("Authorization", "Basic " + basicBase64);
                } else {
                    String accessToken = authenticationService.getToken(headerAndLineDTO);
                    // 获取token失败
                    if (StringUtil.isEmpty(accessToken)) {
                        logger.error("get access_token failure,check your config");
                        throw new RuntimeException("get access_token failure,check your config");
                    }
                    conn.setRequestProperty("Authorization", "Bearer " + accessToken);
                }
            }
            // http://WebXml.com.cn/getWeatherbyCityName
            // http://WebXml.com.cn/qqCheckOnline
            // conn.setRequestProperty("SOAPAction",
            // "http://WebXml.com.cn/getWeatherbyCityName");
            conn.connect();
            try (OutputStream os = conn.getOutputStream();
                 OutputStreamWriter osw = new OutputStreamWriter(os, "utf-8")) {
                osw.write(xml);
                osw.flush();
                InputStream is = conn.getInputStream();
                return is;
            }
        } catch (IOException e) {
            // TODO 更好的判断方式
            if ("Y".equalsIgnoreCase(headerAndLineDTO.getAuthFlag())) {
                if (HapAuthenticationServiceImpl.AUTH_TYPE_OAUTH2.equalsIgnoreCase(headerAndLineDTO.getAuthType())) {
                    if (e.getMessage().contains("response code: 401")) {
                        // 更新token 重新请求 此时token失效
                        logger.warn(e.getMessage());
                        authenticationService.updateToken(headerAndLineDTO);
                        Integer count = HapInvokeInfo.TOKEN_TASK_COUNT.get();
                        if (count != null && count > 0) {
                            logger.info("try get access_token times:" + count);
                            HapInvokeInfo.TOKEN_TASK_COUNT.set(count - 1);
                            return getSoapInputStream(xml, headerAndLineDTO);
                        }
                    }
                }
            }
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public JSONObject invoke(HapInterfaceHeader headerAndLineDTO, JSONObject inbound) throws Exception {

        logger.info("inbound:{}", inbound);
        String xml = "";
        try {
            ;
            if (inbound != null) {
                xml = JSONAndMap.jsonToXml(inbound.toString(), headerAndLineDTO.getNamespace());
            }
        } catch (Exception e) {
            logger.info("jsonToMap error:{}", e);
            throw new HapApiException(HapApiException.ERROR_JSON_TO_MAP, "jsonToMap error");
        }
        xml = headerAndLineDTO.getBodyHeader() + xml + headerAndLineDTO.getBodyTail();

        HapInvokeInfo.OUTBOUND_REQUEST_PARAMETER.set(xml);
        // String url =
        // headerAndLineDTO.getDomainUrl()+headerAndLineDTO.getIftUrl();
        Map result = this.soapSend(headerAndLineDTO, xml);
        JSONObject jsonObject = JSONObject.fromObject(result);
        return jsonObject;
    }
}
