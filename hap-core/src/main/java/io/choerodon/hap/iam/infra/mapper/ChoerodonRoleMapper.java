package io.choerodon.hap.iam.infra.mapper;


import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.choerodon.hap.iam.api.query.RoleQuery;
import io.choerodon.hap.iam.infra.dto.RoleDTO;
import io.choerodon.hap.iam.infra.enums.MemberType;
import io.choerodon.mybatis.common.Mapper;

/**
 * @author wuguokai
 */
public interface ChoerodonRoleMapper extends Mapper<RoleDTO> {

    /**
     * 根据成员Id和成员类型查询启用状态的成员角色Id列表.
     *
     * @param memberId   成员Id
     * @param memberType 成员类型 {@link  MemberType}
     * @return 启用状态的成员角色Id列表
     */
    List<Long> selectEnableRoleIdsByMemberIdAndMemberType(@Param("memberId") Long memberId, @Param("memberType") String memberType);

    /**
     * 根据成员Id和成员类型查询启用状态的成员角色信息列表.
     *
     * @param memberId   成员Id
     * @param memberType 成员类型  {@link  MemberType}
     * @return 启用状态的成员角色信息列表
     */
    List<RoleDTO> selectEnableRolesInfoByMemberIdAndMemberType(@Param("memberId") Long memberId, @Param("memberType") String memberType);

    /**
     * 条件查询启用状态的角色列表(供LOV_ROLE使用).
     *
     * @param role 角色DTO
     * @return 启用状态的角色列表
     */
    List<RoleDTO> selectEnableRolesInfo(RoleDTO role);

    /**
     * 根据用户Id查询已分配给用户的角色信息列表.
     *
     * @param userId 用户Id
     * @return 已分配给用户的角色信息列表
     */
    List<RoleDTO> selectRolesAssignToUserByUserId(@Param("userId") Long userId);

    /**
     * 根据用户Id条件查询未分配给用户的角色信息列表.
     *
     * @param role 角色DTO
     * @return 未分配给用户的角色信息列表
     */
    List<RoleDTO> selectRolesNotAssignToUser(RoleDTO role);

    List<RoleDTO> fulltextSearch(@Param("roleQuery") RoleQuery roleQuery, @Param("param") String param);

    RoleDTO roleWithPermissions(Long id);
}
