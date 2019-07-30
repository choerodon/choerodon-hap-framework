package io.choerodon.hap.api.logs.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.choerodon.base.util.BaseConstants;
import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

/**
 * api调用记录 DTO.
 *
 * @author peng.jiang@hand-china.com
 * @since 2017/9/23.
 */

@Table(name = "api_invoke_record")
public class ApiInvokeRecord extends BaseDTO {

    public static final String FIELD_RECORD_ID = "recordId";
    public static final String FIELD_INVOKE_ID = "invokeId";
    public static final String FIELD_APPLICATION_CODE = "applicationCode";
    public static final String FIELD_SERVER_CODE = "serverCode";
    public static final String FIELD_SERVER_NAME = "serverName";
    public static final String FIELD_API_URL = "apiUrl";
    public static final String FIELD_CLIENT_ID = "clientId";
    public static final String FIELD_REQUEST_TIME = "requestTime";
    public static final String FIELD_REQUEST_METHOD = "requestMethod";
    public static final String FIELD_IP = "ip";
    public static final String FIELD_API_RESPONSE_TIME = "apiResponseTime";
    public static final String FIELD_REQUEST_URL = "requestUrl";
    public static final String FIELD_REFERER = "referer";
    public static final String FIELD_USER_AGENT = "userAgent";
    public static final String FIELD_RESPONSE_TIME = "responseTime";
    public static final String FIELD_INTERFACE_TYPE = "interfaceType";
    public static final String FIELD_RESPONSE_CODE = "responseCode";
    public static final String FIELD_RESPONSE_STATUS = "responseStatus";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordId;

    /**
     * 调用标识
     */
    private String invokeId;

    /**
     * 应用代码
     */
    private String applicationCode;

    /**
     * 服务代码
     */
    private String serverCode;

    /**
     * 服务名称
     */
    private String serverName;

    /**
     * API路径
     */
    private String apiUrl;

    /**
     * clientId
     */
    private String clientId;

    /**
     * 请求时间
     */
    private Date requestTime;

    /**
     * 请求方式
     */
    private String requestMethod;

    /**
     * ip地址
     */
    private String ip;

    /**
     * API响应时间
     */
    private Long apiResponseTime;

    /**
     * 请求路径
     */
    private String requestUrl;

    private String referer;

    private String userAgent;

    /**
     * 响应时间
     */
    private Long responseTime;

    /**
     * 接口类型 SOAP/REST
     */
    private String interfaceType;

    /**
     * 请求状态代码
     */
    private String responseCode;

    /**
     * 请求状态
     */
    private String responseStatus;

    @Transient
    @JsonFormat(pattern = BaseConstants.DATE_TIME_FORMAT)
    private Date startDate;

    @Transient
    @JsonFormat(pattern = BaseConstants.DATE_TIME_FORMAT)
    private Date endDate;

    @Transient
    private Integer page = 1;

    @Transient
    private Integer pagesize = 10;

    @Transient
    private ApiInvokeRecordDetails apiInvokeRecordDetails;

    public ApiInvokeRecord() {
        this.apiInvokeRecordDetails = new ApiInvokeRecordDetails();
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerName() {
        return serverName;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }

    public void setApiResponseTime(Long apiResponseTime) {
        this.apiResponseTime = apiResponseTime;
    }

    public Long getApiResponseTime() {
        return apiResponseTime;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setResponseTime(Long responseTime) {
        this.responseTime = responseTime;
    }

    public Long getResponseTime() {
        return responseTime;
    }

    public void setInterfaceType(String interfaceType) {
        this.interfaceType = interfaceType;
    }

    public String getInterfaceType() {
        return interfaceType;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(String responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getInvokeId() {
        return invokeId;
    }

    public void setInvokeId(String invokeId) {
        this.invokeId = invokeId;
    }

    public String getServerCode() {
        return serverCode;
    }

    public void setServerCode(String serverCode) {
        this.serverCode = serverCode;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPagesize() {
        return pagesize;
    }

    public void setPagesize(Integer pagesize) {
        this.pagesize = pagesize;
    }

    public String getApplicationCode() {
        return applicationCode;
    }

    public void setApplicationCode(String applicationCode) {
        this.applicationCode = applicationCode;
    }

    public ApiInvokeRecordDetails getApiInvokeRecordDetails() {
        return apiInvokeRecordDetails;
    }

    public void setApiInvokeRecordDetails(ApiInvokeRecordDetails apiInvokeRecordDetails) {
        this.apiInvokeRecordDetails = apiInvokeRecordDetails;
    }
}
