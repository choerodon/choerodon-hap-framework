package io.choerodon.hap.api.logs.dto;

import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * api调用记录详情 DTO.
 *
 * @author peng.jiang@hand-china.com
 * @since 2017/9/23.
 */

@Table(name = "api_invoke_record_details")
public class ApiInvokeRecordDetails extends BaseDTO {

    public static final String FIELD_RECORD_DETAILS_ID = "recordDetailsId";
    public static final String FIELD_RECORD_ID = "recordId";
    public static final String FIELD_API_REQUEST_BODY_PARAMETER = "apiRequestBodyParameter";
    public static final String FIELD_API_RESPONSE_CONTENT = "apiResponseContent";
    public static final String FIELD_REQUEST_HEADER_PARAMETER = "requestHeaderParameter";
    public static final String FIELD_REQUEST_BODY_PARAMETER = "requestBodyParameter";
    public static final String FIELD_RESPONSE_CONTENT = "responseContent";
    public static final String FIELD_STACKTRACE = "stacktrace";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordDetailsId;

    /**
     * 请求记录Id
     */
    private Long recordId;

    /**
     * API请求body参数
     */
    private String apiRequestBodyParameter;

    /**
     * API响应内容
     */
    private String apiResponseContent;

    /**
     * 请求header参数
     */
    private String requestHeaderParameter;

    /**
     * 请求body参数
     */
    private String requestBodyParameter;

    /**
     * 响应内容
     */
    private String responseContent;

    /**
     * 错误堆栈
     */
    private String stacktrace;

    public Long getRecordDetailsId() {
        return recordDetailsId;
    }

    public void setRecordDetailsId(Long recordDetailsId) {
        this.recordDetailsId = recordDetailsId;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public void setApiRequestBodyParameter(String apiRequestBodyParameter) {
        this.apiRequestBodyParameter = apiRequestBodyParameter;
    }

    public String getApiRequestBodyParameter() {
        return apiRequestBodyParameter;
    }

    public void setApiResponseContent(String apiResponseContent) {
        this.apiResponseContent = apiResponseContent;
    }

    public String getApiResponseContent() {
        return apiResponseContent;
    }

    public void setRequestHeaderParameter(String requestHeaderParameter) {
        this.requestHeaderParameter = requestHeaderParameter;
    }

    public String getRequestHeaderParameter() {
        return requestHeaderParameter;
    }

    public void setRequestBodyParameter(String requestBodyParameter) {
        this.requestBodyParameter = requestBodyParameter;
    }

    public String getRequestBodyParameter() {
        return requestBodyParameter;
    }

    public void setResponseContent(String responseContent) {
        this.responseContent = responseContent;
    }

    public String getResponseContent() {
        return responseContent;
    }

    public void setStacktrace(String stacktrace) {
        this.stacktrace = stacktrace;
    }

    public String getStacktrace() {
        return stacktrace;
    }

}
