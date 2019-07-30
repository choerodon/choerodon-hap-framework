package io.choerodon.hap.account.dto;

import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 用户角色分配DTO.
 *
 * @author shengyang.zhou@hand-china.com
 * @since 2016/6/9
 */
@Table(name = "sys_user_role")
public class UserRole extends BaseDTO {

    private static final long serialVersionUID = 2098581833914123800L;

    public static final String FIELD_SUR_ID = "surId";
    public static final String FIELD_USER_ID = "userId";
    public static final String FIELD_ROLE_ID = "roleId";

    /**
     * 表ID，主键，供其他表做外键.
     */
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long surId;

    @Column
    private Long userId;

    @Column
    private Long roleId;

    @Transient
    private String userName;

    public Long getRoleId() {
        return roleId;
    }

    public Long getSurId() {
        return surId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public void setSurId(Long surId) {
        this.surId = surId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}