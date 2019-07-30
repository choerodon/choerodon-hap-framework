package io.choerodon.hap.system.controllers.sys;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.hap.system.dto.ParameterConfig;
import io.choerodon.hap.system.service.IParameterConfigService;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.hap.util.dto.CodeValue;
import io.choerodon.hap.util.dto.Lov;
import io.choerodon.hap.util.service.ICodeService;
import io.choerodon.hap.util.service.ILovService;
import io.choerodon.mybatis.util.SqlMapper;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.core.IRequest;
import io.choerodon.web.core.impl.RequestHelper;
import io.choerodon.web.dto.ResponseData;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 对参数配置的操作.
 *
 * @author qiang.zeng
 * @since 2017/11/6
 */
@RestController
@RequestMapping(value = {"/sys/parameter/config", "/api/sys/parameter/config"})
public class ParameterConfigController extends BaseController {

    private final Logger logger = LoggerFactory.getLogger(ParameterConfigController.class);

    private static final String UPPER_VALUE = "VALUE";
    private static final String LOWER_VALUE = "value";
    private static final String UPPER_TEXT = "TEXT";
    private static final String LOWER_TEXT = "text";

    @Autowired
    private ILovService lovService;
    @Autowired
    private ICodeService codeService;
    @Autowired
    @Qualifier("sqlSessionFactory")
    private SqlSessionFactory sqlSessionFactory;
    @Autowired
    private IParameterConfigService parameterConfigService;
    @Autowired
    private ObjectMapper objectMapper;

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/query")
    public ResponseData query(HttpServletRequest request, @RequestParam(required = false) String code, @RequestParam(required = false) Long targetId) {
        IRequest iRequest = RequestHelper.createServiceRequest(request);
        return new ResponseData(parameterConfigService.selectByCodeAndTargetId(code, targetId));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/submit")
    public ResponseData update(@RequestBody List<ParameterConfig> parameterConfigs, BindingResult result, HttpServletRequest request) {
        getValidator().validate(parameterConfigs, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(parameterConfigService.batchUpdate(parameterConfigs));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/remove")
    public ResponseData delete(HttpServletRequest request, @RequestBody List<ParameterConfig> parameterConfigs) {
        parameterConfigService.batchDelete(parameterConfigs);
        return new ResponseData();
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/getLov")
    @SuppressWarnings("unchecked")
    public ResponseData getLov(HttpServletRequest request, @RequestParam(required = false) String sourceCode) throws IOException {
        JSONArray lovData;
        Lov lov = lovService.selectLovByCache(sourceCode);
        if (null != lov) {
            String textField = lov.getTextField();
            String valueField = lov.getValueField();
            if (StringUtils.isNotEmpty(lov.getCustomUrl())) {
                Cookie[] cookies = request.getCookies();
                StringBuilder sb = new StringBuilder();
                for (Cookie cookie : cookies) {
                    sb.append(cookie.getName()).append("=").append(cookie.getValue()).append(";");
                }
                String cookieValue = sb.toString();

                CloseableHttpClient httpclient = HttpClients.createDefault();
                String path = request.getContextPath();
                String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
                HttpGet httpget = new HttpGet(basePath + lov.getCustomUrl());
                httpget.addHeader(new BasicHeader("Cookie", cookieValue));
                HttpEntity entity;
                CloseableHttpResponse response = httpclient.execute(httpget);
                entity = response.getEntity();
                JSONObject jsonObject = JSONObject.fromObject(EntityUtils.toString(entity));
                String rows = String.valueOf(jsonObject.get("rows"));
                lovData = JSONArray.fromObject(rows);
            } else {
                List data = lovService.selectDatas(sourceCode, null, 1, 0);
                if (CollectionUtils.isNotEmpty(data) && (data.get(0) instanceof HashMap)) {
                    for (Object aData : data) {
                        Map map = (Map) aData;
                        Object value = map.get(valueField) != null ? map.get(valueField) : "";
                        Object text = map.get(textField) != null ? map.get(textField) : "";
                        map.put("valueField", value);
                        map.put("textField", text);
                    }
                    return new ResponseData(data);
                }
                lovData = JSONArray.fromObject(data);
            }
            if (lovData != null) {
                for (Object aLovData : lovData) {
                    JSONObject jsonObject = (JSONObject) aLovData;
                    Object value = jsonObject.get(valueField) != null ? jsonObject.get(valueField).toString() : "";
                    Object text = jsonObject.get(textField) != null ? jsonObject.get(textField).toString() : "";
                    jsonObject.put("valueField", value);
                    jsonObject.put("textField", text);
                    initAttribute(jsonObject);
                }
            }
            return new ResponseData(lovData);
        }
        return new ResponseData();
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/getCode")
    public ResponseData getCode(HttpServletRequest request, @RequestParam(required = false) String sourceCode, @RequestParam(required = false) String codeValueField) {
        List<CodeValue> enabledCodeValues = codeService.getCodeValuesByCode(sourceCode);
        JSONArray codeData = new JSONArray();
        if (StringUtils.isEmpty(codeValueField)) {
            codeValueField = "codeValueId";
        }
        if (CollectionUtils.isNotEmpty(enabledCodeValues)) {
            codeData = JSONArray.fromObject(enabledCodeValues);
            int size = codeData.size();
            for (Object aCodeData : codeData) {
                JSONObject jsonObject = (JSONObject) aCodeData;
                Object value = jsonObject.get(codeValueField) != null ? jsonObject.get(codeValueField).toString() : "";
                Object text = jsonObject.get("meaning") != null ? jsonObject.get("meaning").toString() : "";
                jsonObject.put("valueField", value);
                jsonObject.put("textField", text);
                initAttribute(jsonObject);
            }
        }
        return new ResponseData(codeData);
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/queryReportParameter")
    public ResponseData queryByReportCode(HttpServletRequest request, @RequestParam(required = false) String reportCode) {
        List<ParameterConfig> parameterConfigs = parameterConfigService.selectByReportCode(reportCode);
        return parseParameter(parameterConfigs);
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/checkDefaultValue")
    public ResponseData checkDefaultValue(HttpServletRequest request, @RequestBody String sql) throws IOException {
        return executeSql(objectMapper.readValue(sql, String.class), request);
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/executeDefaultValue")
    public ResponseData executeDefaultValue(HttpServletRequest request, @RequestBody ParameterConfig config) throws IOException {
        return executeSql(config.getDefaultValue(), request);
    }

    /**
     * 执行默认值sql，返回执行结果.
     *
     * @param sql     默认值sql
     * @param request HttpServletRequest
     * @return 默认值sql执行结果
     */
    private ResponseData executeSql(String sql, HttpServletRequest request) {
        ResponseData responseData = null;
        if (StringUtils.isNotEmpty(sql)) {
            try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
                responseData = new ResponseData();
                Map<String, String> result = getValueAndText(RequestHelper.getCurrentRequest(), sqlSession, sql);
                responseData.setMessage("默认Value: " + result.get("defaultValue") + " 默认Text: " + result.get("defaultText"));
                return responseData;
            } catch (Throwable e) {
                if (logger.isErrorEnabled()) {
                    logger.error(e.getMessage(), e);
                }
                responseData = new ResponseData(false);
                responseData.setMessage(e.getMessage());
                return responseData;
            }
        }
        return null;
    }

    /**
     * 处理JSONObject值为空的情况.
     *
     * @param jsonObject JSONObject
     */
    @SuppressWarnings("unchecked")
    private void initAttribute(JSONObject jsonObject) {
        Iterator iterator = jsonObject.keys();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            Object value = jsonObject.get(key);
            if (value.equals("null")) {
                jsonObject.replace(key, "");
            }
        }
    }

    private ResponseData parseParameter(List<ParameterConfig> parameterConfigs) {
        if (CollectionUtils.isNotEmpty(parameterConfigs)) {
            for (ParameterConfig parameterConfig : parameterConfigs) {
                if ("sql".equalsIgnoreCase(parameterConfig.getDefaultType()) && StringUtils.isNotEmpty(parameterConfig.getDefaultValue())) {
                    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
                        Map<String, String> result = getValueAndText(RequestHelper.getCurrentRequest(), sqlSession, parameterConfig.getDefaultValue());
                        parameterConfig.setDefaultText(result.get("defaultText"));
                        parameterConfig.setDefaultValue(result.get("defaultValue"));
                    } catch (Throwable e) {
                        if (logger.isErrorEnabled()) {
                            logger.error(e.getMessage(), e);
                        }
                        ResponseData responseData = new ResponseData(false);
                        responseData.setMessage("参数[" + parameterConfig.getTableFieldName() + "]  默认值sql出错:<br><br>" + e.getMessage());
                        return responseData;
                    }
                } else if ("currentDate".equalsIgnoreCase(parameterConfig.getDefaultType())) {
                    Date today = new Date();
                    parameterConfig.setDefaultValue(new SimpleDateFormat("yyyy-MM-dd").format(today));
                }
            }
        }
        return new ResponseData(parameterConfigs);
    }

    private Map<String, String> getValueAndText(IRequest request, SqlSession sqlSession, String sql) {
        Map<String, String> result = new HashMap<>(2);
        SqlMapper sqlMapper = new SqlMapper(sqlSession);
        List<HashMap> results = sqlMapper.selectList("<script>\n\t" + sql + "</script>", request, HashMap.class);
        StringBuilder defaultValue = new StringBuilder();
        String defaultText = "";
        if (results.size() == 1) {
            defaultValue = new StringBuilder(getValue(results.get(0)));
            defaultText = getText(results.get(0));
        } else if (results.size() > 1) {
            for (HashMap map : results) {
                defaultValue.append(getValue(map)).append(",");
            }
        }
        result.put("defaultValue", defaultValue.toString());
        result.put("defaultText", defaultText);
        return result;
    }

    private String getValue(HashMap map) {
        if (null == map.get(UPPER_VALUE) && null == map.get(LOWER_VALUE)) {
            return "";
        }
        if (map.get(UPPER_VALUE) != null) {
            return map.get(UPPER_VALUE).toString();
        }
        if (map.get(LOWER_VALUE) != null) {
            return map.get(LOWER_VALUE).toString();
        }
        return "";
    }

    private String getText(HashMap map) {
        if (null == map.get(UPPER_TEXT) && null == map.get(LOWER_TEXT)) {
            return "";
        }
        if (map.get(UPPER_TEXT) != null) {
            return map.get(UPPER_TEXT).toString();
        }
        if (map.get(LOWER_TEXT) != null) {
            return map.get(LOWER_TEXT).toString();
        }
        return "";
    }

}