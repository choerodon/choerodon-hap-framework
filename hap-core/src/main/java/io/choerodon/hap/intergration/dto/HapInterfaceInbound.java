package io.choerodon.hap.intergration.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.choerodon.base.util.BaseConstants;
import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Table(name = "sys_if_invoke_inbound")
public class HapInterfaceInbound extends BaseDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inboundId;

    @NotEmpty
    private String interfaceName;

    @NotEmpty
    private String interfaceUrl;

    private Date requestTime;

    private String requestHeaderParameter;

    private String requestBodyParameter;

    private String responseContent;

    @Column(name = "STACKTRACE")
    private String stackTrace;

    private String ip;

    private String referer;

    private String userAgent;

    private Long responseTime;

    private String requestMethod;

    private String requestStatus;

    @Transient
    private Integer page = 1;

    @Transient
    private Integer pagesize = 10;

    @Transient
    @JsonFormat(pattern = BaseConstants.DATE_TIME_FORMAT)
    private Date startDate;

    @Transient
    @JsonFormat(pattern = BaseConstants.DATE_TIME_FORMAT)
    private Date endDate;


    public void setInboundId(Long inboundId) {
        this.inboundId = inboundId;
    }

    public Long getInboundId() {
        return inboundId;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceUrl(String interfaceUrl) {
        this.interfaceUrl = interfaceUrl;
    }

    public String getInterfaceUrl() {
        return interfaceUrl;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public void setRequestHeaderParameter(String requestHeaderParameter) {
        this.requestHeaderParameter = requestHeaderParameter;
    }

    public String getRequestHeaderParameter() {
        return requestHeaderParameter;
    }

    public void setResponseContent(String responseContent) {
        this.responseContent = responseContent;
    }

    public String getResponseContent() {
        return responseContent;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public String getReferer() {
        return referer;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public Long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Long responseTime) {
        this.responseTime = responseTime;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getRequestBodyParameter() {
        return requestBodyParameter;
    }

    public void setRequestBodyParameter(String requestBodyParameter) {
        this.requestBodyParameter = requestBodyParameter;
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

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
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
}
