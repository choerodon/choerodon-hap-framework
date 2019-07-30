package io.choerodon.hap.intergration.controllers;

import com.codahale.metrics.annotation.Timed;
import io.choerodon.hap.core.components.UserLoginInfoCollection;
import io.choerodon.hap.intergration.annotation.HapInbound;
import io.choerodon.hap.intergration.annotation.HapOutbound;
import io.choerodon.hap.intergration.dto.HapInterfaceHeader;
import io.choerodon.hap.intergration.exception.HapApiException;
import io.choerodon.hap.intergration.service.IHapApiService;
import io.choerodon.hap.intergration.service.IHapInterfaceHeaderService;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.base.util.BaseConstants;
import io.choerodon.web.controller.BaseController;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * @author xiangyuQi
 * @since 2016/11/2.
 */
@Controller
public class HapApiController extends BaseController implements BaseConstants {

    private final Logger logger = LoggerFactory.getLogger(HapApiController.class);

    private static final int INTERFACE_EXPIRE = 10; //API访问限制.时间周期  单位.秒
    private static final int INTERFACE_NUM = 10; //API访问次数限制.最大访问次数
    private static final Long INTERFACE_INTERVAL = 500L; //API访问.时间间隔内只能访问一次  单位.毫秒

    @Autowired
    private IHapInterfaceHeaderService headerService;

    @Resource(name = "plsqlBean")
    private IHapApiService plsqlService;

    @Resource(name = "restBean")
    private IHapApiService restService;

    @Resource(name = "soapBean")
    private IHapApiService soapService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private String redisCatalog = HAP_CACHE + "interface:";

    @Permission(type = ResourceType.SITE)
    @ResponseBody
    @Timed
    @HapOutbound
    @HapInbound(apiName = "hap.invoke.apiname.interfacetranspond")
    @PostMapping(value = "/r/api")
    public JSONObject sentRequest(HttpServletRequest request, @RequestBody(required = false) JSONObject params)
            throws Exception {
        String sysName = request.getParameter("sysName");
        String apiName = request.getParameter("apiName");
        logger.info("sysName:{}  apiName:{} ", sysName, apiName);
        logger.info("requestBody:{}", params);

        HapInterfaceHeader hapInterfaceHeader = headerService.getHeaderAndLine(sysName, apiName);
        logger.info("return HmsInterfaceHeader:{}", hapInterfaceHeader);

        Map map = new HashMap<String, Object>();
        if (hapInterfaceHeader == null) {
            throw new HapApiException(HapApiException.ERROR_NOT_FOUND, "根据sysName和apiName没有找到数据");
        }
        if (!hapInterfaceHeader.getRequestFormat().equals("raw")) {
            throw new HapApiException(HapApiException.ERROR_REQUEST_FORMAT, "不支持的请求形式");
        }
        JSONObject json = null;

        /**
         * 注释部分为 API透传访问次数限制 ，功能可用，但不完善，后续在接口服务管理中完善
         */
//        Map<String,String> resultMap = requestLimit(request);   //API透传访问次数限制
//        String result = resultMap.get("result");
//        if (result.equals("success")){

        switch (hapInterfaceHeader.getInterfaceType()) {
            case "REST":
                json = restService.invoke(hapInterfaceHeader, params);
                break;
            case "SOAP":
                json = soapService.invoke(hapInterfaceHeader, params);
                break;
            case "PLSQL":
                json = plsqlService.invoke(hapInterfaceHeader, params);
                break;
            default:
                throw new HapApiException(HapApiException.ERROR_INTERFACE_TYPE, "不支持的接口类型");
        }
        return json;

//        }else {
//            return JSONObject.fromObject(resultMap);
//        }

    }

    @Permission(type = ResourceType.SITE)
    @ResponseBody
    @PostMapping("/api/user")
    public Principal user(Principal principal) {
        return principal;
    }


    /**
     * 测试.
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @return String
     * @throws Exception Exception
     */
    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/api/public/xml_test")
    @ResponseBody
    public String jf(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ServletInputStream servletInputStream = request.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(servletInputStream, "UTF-8"));
        String crmDataXml = FileCopyUtils.copyToString(reader);
        System.out.println(crmDataXml);
        return crmDataXml;
    }

    /**
     * API请求限制.
     *
     * @param request HttpServletRequest
     * @return Map<String, String>
     */
    public Map<String, String> requestLimit(HttpServletRequest request) {
        Long lastTime = System.currentTimeMillis(); //获取当前请求时间
        Map<String, String> map = new HashMap<>();//结果返回

        String ip = UserLoginInfoCollection.getIpAddress(request);
        String sysName = request.getParameter("sysName");
        String apiName = request.getParameter("apiName");

        String redisKey = redisCatalog + ip + "_" + sysName + "_" + apiName;

        Object redisLastTime = redisTemplate.opsForHash().get(redisKey, "lastTime");
        if (redisLastTime != null) {
            long interval = lastTime - Long.valueOf(redisLastTime.toString());
            if (interval < INTERFACE_INTERVAL) {
                map.put("result", "fail");
                map.put("msg", "Requests are too frequent");
            } else {
                Object num = redisTemplate.opsForHash().get(redisKey, "num");
                if (Integer.valueOf(num.toString()) >= INTERFACE_NUM) {
                    map.put("result", "fail");
                    map.put("msg", INTERFACE_EXPIRE + "seconds to reach the maximum number of requests");
                } else {
                    map.put("result", "success");
                    redisTemplate.opsForHash().put(redisKey, "lastTime", lastTime + "");
                    redisTemplate.opsForHash().increment(redisKey, "num", 1);
                }
            }
        } else {
            map.put("result", "success");
            redisTemplate.opsForHash().put(redisKey, "lastTime", lastTime + "");
            redisTemplate.opsForHash().put(redisKey, "num", "1");
            redisTemplate.expire(redisKey, INTERFACE_EXPIRE, TimeUnit.SECONDS);
        }
        return map;
    }

}
