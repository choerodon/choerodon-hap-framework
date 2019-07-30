package io.choerodon.hap.api.application.dto;

import io.choerodon.mybatis.entity.BaseDTO;
import org.hibernate.validator.constraints.Length;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 透传限制 DTO.
 *
 * @author lijian.yin@hand-china.com
 * @since 2017/11/15.
 **/

@Table(name = "api_client_interface_limit")
public class ApiAccessLimit extends BaseDTO {

    public static final String FIELD_ID = "id";
    public static final String FIELD_CLIENT_ID = "clientId";
    public static final String FIELD_INTERFACE_CODE = "interfaceCode";
    public static final String FIELD_SERVER_CODE = "serverCode";
    public static final String FIELD_ACCESS_FLAG = "accessFlag";
    public static final String FIELD_ACCESS_FREQUENCY = "accessFrequency";


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Length(max = 100)
    private String clientId;

    @NotNull
    private String serverCode;

    @NotNull
    private String interfaceCode;

    @Length(max = 1)
    private String accessFlag;

    /**
     * 每小时执行次数
     */
    private Long accessFrequency;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientId() {
        return clientId;
    }

    public String getServerCode() {
        return serverCode;
    }

    public void setServerCode(String serverCode) {
        this.serverCode = serverCode;
    }

    public String getInterfaceCode() {
        return interfaceCode;
    }

    public void setInterfaceCode(String interfaceCode) {
        this.interfaceCode = interfaceCode;
    }

    public void setAccessFlag(String accessFlag) {
        this.accessFlag = accessFlag;
    }

    public String getAccessFlag() {
        return accessFlag;
    }

    public void setAccessFrequency(Long accessFrequency) {
        this.accessFrequency = accessFrequency;
    }

    public Long getAccessFrequency() {
        return accessFrequency;
    }

}
