package io.choerodon.hap.api.application.dto;

import io.choerodon.hap.api.gateway.dto.ApiServer;
import io.choerodon.base.annotation.Children;
import io.choerodon.hap.security.oauth.dto.Oauth2ClientDetails;
import io.choerodon.mybatis.entity.BaseDTO;
import org.hibernate.validator.constraints.Length;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 服务 DTO.
 *
 * @author lijian.yin@hand-china.com
 * @since 2017/11/15.
 **/

@Table(name = "api_config_application")
public class ApiApplication extends BaseDTO {

    public static final String FIELD_APPLICATION_ID = "applicationId";
    public static final String FIELD_CODE = "code";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String CLI_ID = "cliId";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationId;

    // 应用代码
    @NotEmpty
    @Length(max = 30)
    private String code;

    // 应用名称
    @Length(max = 200)
    private String name;

    // 应用描述
    @Length(max = 255)
    private String description;

    /**
     * 客户端ID
     */
    private Long cliId;

    @Transient
    private String clientId;

    @Transient
    @Children
    private Oauth2ClientDetails client;

    @Transient
    private List<ApiServer> servers;

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setCliId(Long cliId) {
        this.cliId = cliId;
    }

    public Long getCliId() {
        return cliId;
    }

    public Oauth2ClientDetails getClient() {
        return client;
    }

    public void setClient(Oauth2ClientDetails client) {
        this.client = client;
    }

    public List<ApiServer> getServers() {
        return servers;
    }

    public void setServers(List<ApiServer> servers) {
        this.servers = servers;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
