package io.choerodon.hap.intergration.exception;

import io.choerodon.base.exception.BaseException;

/**
 * Copyright (c) 2016. Hand Enterprise Solution Company. All right reserved.
 * Project Name:HmapParent
 * Package Name:hmap.core.hms.exception
 * Date:2016/8/15
 * Create By:jiguang.sun@hand-china.com
 */

public class HapApiException extends BaseException {

    private static final long serialVersionUID = -3250576758107608016L;

    public static final String EXCEPTION_CODE = "hms.interface";

    //根据sysName和apiName没有找到数据
    public static final String ERROR_NOT_FOUND = "error.request.url.not.found";

    //不支持的请求形式
    public static final String ERROR_REQUEST_FORMAT = "error.requestFormat.not.support";

    //不支持的接口类型
    public static final String ERROR_INTERFACE_TYPE = "error.interfaceType.not.support";

    //rest http请求失败
    public static final String ERROR_HTTP_REQUEST = "HTTP.GET.Request.Failed";

    //map2Xml 错误
    public static final String ERROR_MAP_TO_XML = "error.format_MapToXml";

    //Xml2Map 错误
    public static final String ERROR_XML_TO_MAP = "error.format_xmlToMap";

    //Json2Map 错误
    public static final String ERROR_JSON_TO_MAP = "error.format_jsonToMap";

    /**
     * 服务注册异常处理
     * @author peng.jiang@hand-china.com on 2017/10/9
     */


    //服务注册异常
    public static final String CODE_SERVER_REGISTRATION_EXCEPTION = "SERVER_REGISTRATION_EXCEPTION";

    // 请求返回错误
    // public static final String CODE_API_SERVER_RESPONSE_ERROR= "SERVER_RESPONSE_ERROR";

    //系统异常
    public static final String CODE_API_SYSTEM_EXCEPTION = "API_SYSTEM_EXCEPTION";

    //访问限制过滤器错误代码
    public static final String CODE_API_ACCESS_LIMIT = "API_ACCESS_LIMIT_ERROR";

    //请求第三方错误代码
    public static final String CODE_API_THIRD_REQUEST = "API_THIRD_PARTY_REQUEST";

    //请求超出限制次数
    public static final String MSG_ERROR_API_OVERSTEP = "Number of requests exceeded";

    //没有权限
    public static final String MSG_ERROR_API_NO_PERMISSION = "Access without permission";

    //未找到服务
    public static final String MSG_ERROR_API_NO_SERVER = "No service found";

    //server 未开启
    public static final String MSG_ERROR_API_SERVER_DISABLE = "Service not open";

    //interface 未开启
    public static final String MSG_ERROR_API_INTERFACE_DISABLE = "Interface not open";

    //interface 限制未开启
    public static final String MSG_ERROR_API_INTERFACE_LIMIT_DISABLE = "Interface limit not open";

    //错误的url
    public static final String MSG_ERROR_API_URL = "Request url error";

    protected HapApiException(String code, String descriptionKey, Object[] parameters) {
        super(code, descriptionKey, parameters);
    }

    public HapApiException(String code, String descriptionKey){
        super(code,descriptionKey,null);

    }
}
