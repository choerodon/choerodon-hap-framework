package io.choerodon.hap.intergration.service.impl;

import io.choerodon.hap.intergration.beans.HapInvokeInfo;
import io.choerodon.hap.intergration.beans.HapTransferDataMapper;
import io.choerodon.hap.intergration.dto.HapInterfaceHeader;
import io.choerodon.hap.intergration.service.IHapApiService;
import io.choerodon.hap.intergration.service.IHapAuthenticationService;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.DefaultValueProcessor;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.util.StringUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

/**
 * Created by user on 2016/7/28.
 */
@Service(value = "restBean")
public class HapRestApiServiceImpl implements IHapApiService {

    private final Logger logger = LoggerFactory.getLogger(HapRestApiServiceImpl.class);

    @Autowired
    private IHapAuthenticationService authenticationService;

    public String get(String url, HapInterfaceHeader headerAndLineDTO, JSONObject params) throws Exception {
        String resultData = "";
        // get请求把参数拼在url后面，根据自己需求更改
        if (params != null && params.size() > 0) {
            Iterator iterator = params.keys();
            url += "?";
            StringBuffer parm = new StringBuffer();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                parm.append(key + "=" + URLEncoder.encode(params.get(key).toString(), "utf-8") + "&");
            }
            String p = parm.substring(0, parm.length() - 1);
            // 供出站AOP获取参数信息
            HapInvokeInfo.OUTBOUND_REQUEST_PARAMETER.set(p);
            url += p;
        }

        logger.info("request url:{}", url);
        HttpURLConnection httpURLConnection = null;
        try {
            URL restServiceURL = new URL(url);
            httpURLConnection = (HttpURLConnection) restServiceURL.openConnection();
            httpURLConnection.setRequestMethod("GET");

            if (headerAndLineDTO.getRequestAccept() != null) {
                httpURLConnection.setRequestProperty("Accept", headerAndLineDTO.getRequestAccept());
            }
            if (headerAndLineDTO.getAuthFlag().equals("Y")) {
                processAuthAuthorization(headerAndLineDTO, httpURLConnection);
            }

            httpURLConnection.connect();
            HapInvokeInfo.HTTP_RESPONSE_CODE.set(httpURLConnection.getResponseCode());
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode != 200) {
                if (responseCode == 401) {
                    // 更新token 重新请求 此时token失效
                    authenticationService.updateToken(headerAndLineDTO);
                    Integer count = HapInvokeInfo.TOKEN_TASK_COUNT.get();
                    if (count != null && count > 0) {
                        logger.info("try get access_token times:" + count);
                        httpURLConnection.disconnect();
                        HapInvokeInfo.TOKEN_TASK_COUNT.set(count - 1);
                        return get(url, headerAndLineDTO, params);
                    }
                }
                throw new RuntimeException(
                        "HTTP GET Request Failed with Error code : " + httpURLConnection.getResponseCode());
            }
            resultData = getResponseDate(httpURLConnection);

        } catch (MalformedURLException e) {
            logger.error(e.getMessage());
            throw e;

        } catch (IOException e) {
            logger.error(e.getMessage());
            throw e;
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        logger.info("responseData:{}", resultData);
        return resultData;
    }

    public String post(String url, HapInterfaceHeader headerAndLineDTO, String params) throws Exception {
        String resultData = "";
        HapInvokeInfo.OUTBOUND_REQUEST_PARAMETER.set(params);
        HttpURLConnection connection = null;
        try {

            URL myURL = new URL(url);
            connection = (HttpURLConnection) myURL.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            if (!params.isEmpty()) {
                connection.setDoOutput(true);
            }

            if (headerAndLineDTO.getRequestContentType() != null) {
                connection.setRequestProperty("Content-Type", headerAndLineDTO.getRequestContentType());
            } else {
                connection.setRequestProperty("Content-Type", "application/json");
            }

            if (headerAndLineDTO.getAuthFlag().equals("Y")) {
                processAuthAuthorization(headerAndLineDTO, connection);
            }

            connection.connect();

            if (!params.isEmpty()) {
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(params.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();
            }
            HapInvokeInfo.HTTP_RESPONSE_CODE.set(connection.getResponseCode());
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                if (responseCode == 401) {
                    // 更新token 重新请求 此时token失效
                    authenticationService.updateToken(headerAndLineDTO);
                    Integer count = HapInvokeInfo.TOKEN_TASK_COUNT.get();
                    if (count != null && count > 0) {
                        logger.info("try get access_token times:" + count);
                        connection.disconnect();
                        HapInvokeInfo.TOKEN_TASK_COUNT.set(count - 1);
                        return post(url, headerAndLineDTO, params);
                    }
                }
                throw new RuntimeException("HTTP GET Request Failed with Error code : " + connection.getResponseCode());
            }

            resultData = getResponseDate(connection);
        } catch (MalformedURLException e) {
            logger.error(e.getMessage(), e);
            throw e;

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw e;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        logger.info("responseData:{}", resultData);
        return resultData;

    }

    @Override
    public JSONObject invoke(HapInterfaceHeader headerAndLineDTO, JSONObject inbound) throws Exception {
        String url = headerAndLineDTO.getDomainUrl() + headerAndLineDTO.getIftUrl();
        logger.info("request url:{}", url);
        String data = null;
        JSONObject json = null;
        String inboundParam = " ";

        HapTransferDataMapper mapper = null;
        // 如果用户定义了包装类，那么就需要将inbound进行包装，本类一般都是客户化开发
        // 根据需要来做，使用时需要进行动态加载 by zongyun.zhou@hand-china.com
        if (StringUtil.isNotEmpty(headerAndLineDTO.getMapperClass())) {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            try {
                Class c = cl.loadClass(headerAndLineDTO.getMapperClass());
                mapper = (HapTransferDataMapper) c.newInstance();
            } catch (ClassNotFoundException e) {
                logger.error("ClassNotFoundException:" + e.getMessage());
                throw e;
            } catch (InstantiationException e) {
                logger.error("InstantiationException:" + e.getMessage());
                throw e;
            } catch (IllegalAccessException e) {
                logger.error("IllegalAccessException:" + e.getMessage());
                throw e;
            }
        }

        if (headerAndLineDTO.getRequestMethod().equals("POST")) {
            if (inbound != null) {
                inboundParam = inbound.toString();
                if (mapper != null)
                    inboundParam = mapper.requestDataMap(inbound);
            }
            logger.info("params Xml :{}", inboundParam.toString());

            data = this.post(url, headerAndLineDTO, inboundParam);
            if (mapper != null) {
                data = mapper.responseDataMap(data);
            }

        } else if (headerAndLineDTO.getRequestMethod().equals("GET")) {
            data = this.get(url, headerAndLineDTO, inbound);
            if (mapper != null) {
                data = mapper.responseDataMap(data);
            }
        }
        data = data.toString().replaceAll("null", "\"\"");
        // 供出站AOP获取返回参数
        HapInvokeInfo.OUTBOUND_RESPONSE_DATA.set(data);
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.registerDefaultValueProcessor(String.class, new DefaultValueProcessor() {
            public Object getDefaultValue(Class type) {
                return JSONNull.getInstance();
            }
        });
        json = JSONObject.fromObject(data, jsonConfig);
        return json;
    }

    private void processAuthAuthorization(HapInterfaceHeader headerAndLineDTO, HttpURLConnection connection) {
        if (HapAuthenticationServiceImpl.AUTH_TPYE_BASIC.equalsIgnoreCase(headerAndLineDTO.getAuthType())) {
            String basicBase64;
            String e = headerAndLineDTO.getAuthUsername() + ":" + headerAndLineDTO.getAuthPassword();
            basicBase64 = new String(Base64.encodeBase64(e.getBytes()));
            connection.setRequestProperty("Authorization", "Basic " + basicBase64);
        } else {
            String accessToken = authenticationService.getToken(headerAndLineDTO);
            // 获取token失败
            if (StringUtil.isEmpty(accessToken)) {
                logger.error("get access_token failure,check your config");
                throw new RuntimeException("get access_token failure,check your config");
            }
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);
        }
    }

    private String getResponseDate(HttpURLConnection connection) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            StringBuilder results = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                results.append(line);
            }
            return results.toString();
        }
    }
}
