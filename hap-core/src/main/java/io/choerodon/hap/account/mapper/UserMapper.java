package io.choerodon.hap.account.mapper;

import io.choerodon.hap.account.dto.User;
import io.choerodon.hap.iam.api.dto.RoleAssignmentSearchDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户Mapper.
 *
 * @author shengyang.zhou@hand-china.com
 */
public interface UserMapper extends Mapper<User> {

    /**
     * 根据用户名查询用户.
     *
     * @param userName 用户名称
     * @return 用户
     */
    User selectByUserName(String userName);

    /**
     * 根据员工代码查询用户.
     *
     * @param employeeCode 员工代码
     * @return 用户集合
     */
    List<User> selectUserNameByEmployeeCode(String employeeCode);

    /**
     * 根据用户ID集合查询用户集合.
     *
     * @param userIds 用户ID集合
     * @return 用户集合
     */
    List<User> selectByIdList(List<Long> userIds);

    /**
     * 修改密码.
     *
     * @param userId      用户ID
     * @param passwordNew 新密码
     * @return 影响行数
     */
    int updatePassword(@Param("userId") Long userId, @Param("password") String passwordNew);

    /**
     * 修改首次登陆标记.
     *
     * @param userId 用户ID
     * @param status 是否第一次登录状态
     * @return 影响行数
     */
    int updateFirstLogin(@Param("userId") Long userId, @Param("status") String status);

    /**
     * 更新用户信息.
     *
     * @param user 用户
     * @return 影响行数
     */
    int updateBasic(User user);

    /**
     * 条件查询用户.
     *
     * @param user 用户
     * @return 用户集合
     */
    List<User> selectUsers(User user);

    /**
     * 条件查询用户.
     *
     * @param user 用户
     * @return 用户集合
     */
    List<User> selectUsersOption(User user);

    /**
     * 根据角色分配搜索DTO模糊查询用户列表.
     *
     * @param roleAssignmentSearchDTO 角色分配搜索DTO
     * @return 用户列表
     */
    List<User> fuzzyQueryUsersByRoleAssignmentSearchDTO(@Param("roleAssignmentSearchDTO") RoleAssignmentSearchDTO roleAssignmentSearchDTO, @Param("param") String param);
}