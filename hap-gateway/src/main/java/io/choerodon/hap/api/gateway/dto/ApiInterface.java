package io.choerodon.hap.api.gateway.dto;

import io.choerodon.hap.api.application.dto.ApiAccessLimit;
import io.choerodon.base.annotation.Children;
import io.choerodon.mybatis.entity.BaseDTO;
import org.hibernate.validator.constraints.Length;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;

/**
 * 接口 DTO.
 *
 * @author lijian.yin@hand-china.com
 **/

@Table(name = "api_config_interface")
public class ApiInterface extends BaseDTO {

    public static final String FIELD_INTERFACE_ID = "interfaceId";
    public static final String FIELD_SERVER_ID = "serverId";
    public static final String FIELD_CODE = "code";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_MAPPING_URL = "mappingUrl";
    public static final String FIELD_SOAP_VERSION = "soapVersion";
    public static final String FIELD_ENABLE_FLAG = "enableFlag";
    public static final String FIELD_MAPPING_CLASS = "mappingClass";
    public static final String FIELD_REQUEST_METHOD = "requestMethod";
    public static final String FIELD_REQUEST_HEAD = "requestHead";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_SOAP_ACTION = "soapAction";
    public static final String FIELD_INVOKE_RECORD_DETAILS = "invokeRecordDetails";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long interfaceId;

    /**
     * 服务Id
     */
    private Long serverId;

    // 接口代码
    @NotEmpty
    @Length(max = 30)
    private String code;

    // 接口名称
    @NotEmpty
    @Length(max = 200)
    private String name;

    // 接口地址
    @Length(max = 200)
    private String interfaceUrl;

    // 映射地址
    @NotEmpty
    @Length(max = 200)
    private String mappingUrl;

    // 是否启用
    @NotEmpty
    @Length(max = 1)
    private String enableFlag;

    // soap版本
    @Length(max = 50)
    private String soapVersion;

    // 映射类
    @Length(max = 255)
    private String mappingClass;

    //请求方式
    @Length(max = 10)
    private String requestMethod;

    // 请求头
    @Length(max = 2000)
    private String requestHead;

    // 接口描述
    @Length(max = 255)
    private String description;

    // soapAction
    @Length(max = 255)
    private String soapAction;

    // 是否记录调用详情
    @Length(max = 1)
    private String invokeRecordDetails;

    @Transient
    @Children
    private ApiAccessLimit apiAccessLimit;

    public Long getInterfaceId() {
        return interfaceId;
    }

    public void setInterfaceId(Long interfaceId) {
        this.interfaceId = interfaceId;
    }

    public Long getServerId() {
        return serverId;
    }

    public void setServerId(Long serverId) {
        this.serverId = serverId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setInterfaceUrl(String interfaceUrl) {
        this.interfaceUrl = interfaceUrl;
    }

    public String getInterfaceUrl() {
        return interfaceUrl;
    }

    public void setMappingUrl(String mappingUrl) {
        this.mappingUrl = mappingUrl;
    }

    public String getMappingUrl() {
        return mappingUrl;
    }

    public void setEnableFlag(String enableFlag) {
        this.enableFlag = enableFlag;
    }

    public String getEnableFlag() {
        return enableFlag;
    }

    public void setMappingClass(String mappingClass) {
        this.mappingClass = mappingClass;
    }

    public String getMappingClass() {
        return mappingClass;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestHead(String requestHead) {
        this.requestHead = requestHead;
    }

    public String getRequestHead() {
        return requestHead;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getSoapVersion() {
        return soapVersion;
    }

    public void setSoapVersion(String soapVersion) {
        this.soapVersion = soapVersion;
    }

    public String getSoapAction() {
        return soapAction;
    }

    public void setSoapAction(String soapAction) {
        this.soapAction = soapAction;
    }

    public String getInvokeRecordDetails() {
        return invokeRecordDetails;
    }

    public void setInvokeRecordDetails(String invokeRecordDetails) {
        this.invokeRecordDetails = invokeRecordDetails;
    }

    public ApiAccessLimit getApiAccessLimit() {
        return apiAccessLimit;
    }

    public void setApiAccessLimit(ApiAccessLimit apiAccessLimit) {
        this.apiAccessLimit = apiAccessLimit;
    }
}
