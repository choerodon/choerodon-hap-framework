/*
 * #{copyright}#
 */

package io.choerodon.hap.security.dto;

import java.io.Serializable;
import java.util.Set;

/**
 * 权限认证用户基类.
 * 
 * @author wuyichu
 * @deprecated
 */
public class User implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 8517761026555775283L;

    /**
     * 用户名.
     */
    private String userName;

    /**
     * 密码.
     */
    private String password;

    /**
     * 是否可用.
     */
    private boolean enabled;

    /**
     * 用户角色.
     */
    private Set<String> roles;

    public User(String userName, String password, boolean enabled, Set<String> roles) {
        super();
        this.userName = userName;
        this.password = password;
        this.enabled = enabled;
        this.roles = roles;
    }

    public User(String userName, String password, Set<String> roles) {
        this(userName, password, true, roles);
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName
     *            the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     *            the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled
     *            the enabled to set
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return the roles
     */
    public Set<String> getRoles() {
        return roles;
    }

    /**
     * @param roles
     *            the roles to set
     */
    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    @Override
    public int hashCode() {
        return userName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User) {
            return userName.equals(((User) obj).userName);
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append(": ");
        sb.append("Username: ").append(this.userName).append("; ");
        sb.append("Password: [PROTECTED]; ");
        sb.append("Enabled: ").append(this.enabled).append("; ");
        if (!roles.isEmpty()) {
            sb.append("Granted Authorities: ");

            boolean first = true;
            for (String role : roles) {
                if (!first) {
                    sb.append(",");
                }
                first = false;
                sb.append(role);
            }
        } else {
            sb.append("该用户没有任何的授权");
        }

        return sb.toString();
    }

}
