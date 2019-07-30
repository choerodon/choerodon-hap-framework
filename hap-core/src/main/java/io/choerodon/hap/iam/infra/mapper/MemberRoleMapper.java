package io.choerodon.hap.iam.infra.mapper;

import io.choerodon.hap.iam.infra.dto.MemberRoleDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author carllhw
 */
public interface MemberRoleMapper extends Mapper<MemberRoleDTO> {
    /**
     * 根据成员Id和角色Id查询成员角色数量.
     *
     * @param memberId 成员Id
     * @param roleId   角色Id
     * @return 成员角色数量
     */
    int selectCountByMemberIdAndRoleId(@Param("memberId") Long memberId, @Param("roleId") Long roleId);
}
