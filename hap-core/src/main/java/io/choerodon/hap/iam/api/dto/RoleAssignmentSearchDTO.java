package io.choerodon.hap.iam.api.dto;

/**
 * @author qiang.zemng
 */
public class RoleAssignmentSearchDTO {

    private String userName;

    private String roleName;

    private String[] param;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String[] getParam() {
        return param;
    }

    public void setParam(String[] param) {
        this.param = param;
    }
}
