package io.choerodon.hap.iam.app.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.hap.iam.api.query.RoleQuery;
import io.choerodon.hap.iam.exception.ChoerodonRoleException;
import io.choerodon.hap.iam.infra.dto.RoleDTO;
import io.choerodon.hap.iam.infra.enums.MemberType;

import java.util.List;

/**
 * @author superlee
 * @author wuguokai
 */
public interface ChoerodonRoleService {

    /**
     * 根据成员Id和成员类型查询启用状态的成员角色Id列表.
     *
     * @param memberId   成员Id
     * @param memberType 成员类型 {@link  MemberType}
     * @return 启用状态的成员角色Id列表
     */
    List<Long> selectEnableRoleIdsByMemberIdAndMemberType(Long memberId, String memberType);

    /**
     * 根据成员Id和成员类型查询启用状态的成员角色信息列表.
     *
     * @param memberId   成员Id
     * @param memberType 成员类型  {@link  MemberType}
     * @return 启用状态的成员角色信息列表
     */
    List<RoleDTO> selectEnableRolesInfoByMemberIdAndMemberType(Long memberId, String memberType);

    /**
     * 根据用户Id查询已分配给用户的角色信息列表.
     *
     * @param userId 用户Id
     * @return 已分配给用户的角色信息列表
     */
    List<RoleDTO> selectRolesAssignToUserByUserId(Long userId);

    /**
     * 根据用户Id查询未分配给用户的角色信息列表.
     *
     * @param role 角色DTO
     * @return 未分配给用户的角色信息列表
     */
    List<RoleDTO> selectRolesNotAssignToUser(RoleDTO role);

    PageInfo<RoleDTO> pagingSearch(int page, int size, RoleQuery roleQuery);

    RoleDTO create(RoleDTO roleDTO) throws ChoerodonRoleException;

    RoleDTO update(RoleDTO roleDTO) throws ChoerodonRoleException;

    void delete(Long id) throws ChoerodonRoleException;

    RoleDTO enableRole(Long id) throws ChoerodonRoleException;

    RoleDTO disableRole(Long id) throws ChoerodonRoleException;

    void check(RoleDTO role) throws ChoerodonRoleException;

    RoleDTO queryWithPermissions(Long id);

    /**
     * 根据角色编码查询角色信息.
     *
     * @param code 角色编码
     * @return 角色信息
     */
    RoleDTO selectByCode(String code);

    /**
     * 根据角色名模糊查询角色列表(如果不传角色名 默认不查询 直接返回空列表).
     *
     * @param roleName 角色名
     * @return 角色列表
     */
    List<RoleDTO> fuzzyQueryRolesByRoleName(String roleName);
}
