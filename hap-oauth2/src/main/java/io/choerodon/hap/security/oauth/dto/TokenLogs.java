package io.choerodon.hap.security.oauth.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.choerodon.mybatis.common.query.Comparison;
import io.choerodon.mybatis.common.query.Where;
import io.choerodon.base.util.BaseConstants;
import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author qixiangyu
 */
@Table(name = "sys_token_logs")
public class TokenLogs extends BaseDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户ID
     */
    @NotNull
    @Where
    private Long userId;

    /**
     * 客户端ID
     */
    @Where
    private String clientId;

    /**
     * token
     */
    @Where
    private String token;

    /**
     * token获取日期
     */
    private Date tokenAccessTime;

    /**
     * token失效日期
     */
    @Where(comparison = Comparison.GREATER_THAN)
    @JsonFormat(pattern = BaseConstants.DATE_TIME_FORMAT)
    private Date tokenExpiresTime;

    /**
     * 是否有效
     */
    @Where
    private String revokeFlag;

    /**
     * token获取方式
     */
    private String tokenAccessType;

    /**
     * 查询当前token状态,online,all
     */
    @Transient
    private String tokenStatus;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setTokenAccessTime(Date tokenAccessTime) {
        this.tokenAccessTime = tokenAccessTime;
    }

    public Date getTokenAccessTime() {
        return tokenAccessTime;
    }

    public void setTokenAccessType(String tokenAccessType) {
        this.tokenAccessType = tokenAccessType;
    }

    public String getTokenAccessType() {
        return tokenAccessType;
    }

    public String getRevokeFlag() {
        return revokeFlag;
    }

    public void setRevokeFlag(String revokeFlag) {
        this.revokeFlag = revokeFlag;
    }

    public Date getTokenExpiresTime() {
        return tokenExpiresTime;
    }

    public void setTokenExpiresTime(Date tokenExpiresTime) {
        this.tokenExpiresTime = tokenExpiresTime;
    }

    public String getTokenStatus() {
        return tokenStatus;
    }

    public void setTokenStatus(String tokenStatus) {
        this.tokenStatus = tokenStatus;
    }
}
