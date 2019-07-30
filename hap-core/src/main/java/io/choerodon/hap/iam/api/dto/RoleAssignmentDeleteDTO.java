package io.choerodon.hap.iam.api.dto;

import java.util.List;
import java.util.Map;

/**
 * @author qiang.zeng
 */
public class RoleAssignmentDeleteDTO {

    private String memberType;
    /**
     * key: 用户Id
     * value: 角色Id列表
     */
    private Map<Long, List<Long>> data;

    public String getMemberType() {
        return memberType;
    }

    public void setMemberType(String memberType) {
        this.memberType = memberType;
    }

    public Map<Long, List<Long>> getData() {
        return data;
    }

    public void setData(Map<Long, List<Long>> data) {
        this.data = data;
    }
}
