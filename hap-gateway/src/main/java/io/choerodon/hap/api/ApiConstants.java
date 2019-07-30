package io.choerodon.hap.api;

/**
 * 常量.
 *
 * @author peng.jiang@hand-china.com
 * @since 2017/9/23.
 */
public interface ApiConstants
{
    String SOAP_HTTP_TRANSPORT = "http://schemas.xmlsoap.org/soap/http";
    String SOAP12_HTTP_BINDING_NS = "http://www.w3.org/2003/05/soap/bindings/HTTP/";
    String SOAP_MICROSOFT_TCP = "http://schemas.microsoft.com/wse/2003/06/tcp";

    String SOAP11 = "SOAP11";
    String SOAP12 = "SOAP12";

    String SERVER_TYPE_REST = "REST";
    String SERVER_TYPE_SOAP = "SOAP";

    String AUTH_TYPE_NONE = "NONE";
    String AUTH_TYPE_BASIC = "BASIC";
    String AUTH_TYPE_OAUTH2 = "OAUTH2";

    String NAMESPACE_PRE = "hap";

    /**
     * SOAP 报文
     *
     */
    String SOAP11_NAMESPACE = "http://schemas.xmlsoap.org/soap/envelope/";
    String SOAP12_NAMESPACE = "http://www.w3.org/2003/05/soap-envelope";
    String SOAP_MESSAGE = "<soap:Envelope xmlns:soap=\"${soapNamespace}\" xmlns:" + NAMESPACE_PRE + "=\"${namespace}\">\n"
            + "<soap:Header>\n ${wssSecurity} </soap:Header>\n"
            + "  <soap:Body>\n  ${soapBody} \n</soap:Body>\n</soap:Envelope>";
    String SOAP_SECURITY_PRE = "<wsse:Security xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" soap:mustUnderstand=\"1\">";
    String SOAP_SECURTIY_SUF = "</wsse:Security>";

    String SOAP_VAR_NAMESPACE = "namespace";

    String SERVER_ELEMENT_FORM_DEFAULT_QUALIFIED = "qualified";
    String SERVER_ELEMENT_FORM_DEFAULT_UNQUALIFIED = "unqualified";

    String SERVER_INFO = "info";
    String SERVER_TITLE = "title";
    String SERVER_SWAGGER = "swagger";
    String SERVER_HOST = "host";
    String SERVER_BASEPATH = "basePath";
    String SERVER_PATHS = "paths";
    String SERVER_SUMMARY = "summary";
    String SERVER_OPERATIONID = "operationId";
    String SERVER_CONSUMES = "consumes";

    /**
     * get post head put delete options patch
     */
    int SERVER_METHOD_SIZE = 7;

    String REQUEST_POST = "POST";
    String REQUEST_GET = "GET";
    String REQUEST_DELETE = "DELETE";
    String REQUEST_PUT = "PUT";

    String SERVER_HTTP = "http://";
    String ENABLE_FLAG_N = "N";
    String ENABLE_FLAG_Y = "Y";

    /**
     * request response 默认数据格式转换
     */
    String DEFAULT_TRANSFERDATA_MAAPPER = "io.choerodon.hap.intergration.util.HapTransferDataDefaultMapper";

    String ENCODE_UTF8 = "UTF-8";

    String CONTENY_TYPE_JSON = "application/json";
    String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";

    String WSS_PASSWORD_TYPE_NONE = "None";
    String WSS_PASSWORD_TYPE_PASSWORD_TEXT = "PasswordText";
    String WSS_PASSWORD_TYPE_PASSWORD_DIGEST = "PasswordDigest";


    int HTTP_RESPONSE_CODE_401 = 401;
    int HTTP_RESPONSE_CODE_200 = 200;

    String RESPONSEDATA_CODE_SUCCESS = "SUCCESS";

    String XML = "xml";

    int NUMBER_2 = 2;

    String SOAP_WSDL = "wsdl";


}

