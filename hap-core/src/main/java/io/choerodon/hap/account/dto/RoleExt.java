package io.choerodon.hap.account.dto;

/**
 * 角色扩展类.
 *
 * @author shengyang.zhou@hand-china.com
 * @since 2016/6/9
 */

public class RoleExt extends Role {
    private Long surId;

    private Long userId;

    public Long getSurId() {
        return surId;
    }

    public void setSurId(Long surId) {
        this.surId = surId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
