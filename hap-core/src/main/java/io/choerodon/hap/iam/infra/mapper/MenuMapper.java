package io.choerodon.hap.iam.infra.mapper;

import io.choerodon.hap.iam.infra.dto.MenuDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author wuguokai
 * @author superlee
 */
public interface MenuMapper extends Mapper<MenuDTO> {
    /**
     * 根据用户Id查询member_role表查角色，再根据role_permission表查权限，
     * 根据menu_permission表反查用户拥有的菜单.
     *
     * @param memberType    成员类型（user）
     * @param memberId      成员Id（对应成员类型）
     * @param memberRoleIds 当前用户拥有角色
     * @return 当前用户拥有菜单
     */
    List<MenuDTO> selectMenusAfterCheckPermission(@Param("memberType") String memberType,
                                                  @Param("memberId") Long memberId, @Param("memberRoleIds") Long[] memberRoleIds);

    /**
     * 根据层级查菜单附带权限，不包含top菜单.
     *
     * @param level 菜单层级
     * @return 层级菜单(包含权限)
     */
    List<MenuDTO> selectMenusWithPermission(String level);
}
