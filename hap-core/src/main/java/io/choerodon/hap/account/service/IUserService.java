package io.choerodon.hap.account.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.hap.account.dto.User;
import io.choerodon.hap.account.exception.UserException;
import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.function.dto.MenuItem;
import io.choerodon.hap.function.dto.ResourceItemAssign;
import io.choerodon.hap.iam.api.dto.RoleAssignmentSearchDTO;
import io.choerodon.mybatis.service.IBaseService;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

/**
 * 用户服务接口.
 * HAP中此接口实现默认都是按照USER类型处理. 其他类型可自行实现此接口逻辑.
 *
 * @author njq.niu@hand-china.com
 * @since 2016/1/28
 */
public interface IUserService extends IBaseService<User>, ProxySelf<IUserService> {

    /**
     * 校验邮箱格式.
     *
     * @param email 邮箱名称
     * @throws UserException 邮箱格式异常
     */
    void validateEmail(String email) throws UserException;

    /**
     * 校验电话格式.
     *
     * @param phone 电话
     * @throws UserException 电话格式异常
     */
    void validatePhone(String phone) throws UserException;

    /**
     * 登录校验账户有效性.
     *
     * @param user 用户
     * @return 用户
     * @throws UserException 登录时异常
     */
    User login(User user) throws UserException;

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
     * 校验新旧密码.
     *
     * @param newPwd      新密码
     * @param newPwdAgain 校验新密码
     * @throws UserException 用户密码校验异常
     */
    void validatePassword(String newPwd, String newPwdAgain) throws UserException;

    /**
     * 修改密码.
     *
     * @param userId   用户ID
     * @param userName 用户名称
     * @param password 密码
     */
    void updatePassword(Long userId, String userName, String password);

    /**
     * 修改是否是首次登陆.
     *
     * @param userId 用户ID
     * @param status Y/N
     */
    void updateNotFirstLogin(Long userId, String status);

    /**
     * 条件查询用户.
     *
     * @param user     用户
     * @param pageNum  页码
     * @param pageSize 每页显示数量
     * @return 用户集合
     */
    List<User> selectUsers(User user, int pageNum, int pageSize);

    /**
     * 查询用户功能菜单.
     *
     * @param userId 用户ID
     * @return 菜单对象集合
     */
    List<MenuItem> queryFunction(Long userId);

    /**
     * 查询用户权限组件菜单.
     *
     * @param userId     用户ID
     * @param functionId 功能ID
     * @return 菜单对象集合
     */
    List<MenuItem> queryResourceItems(Long userId, Long functionId);

    /**
     * 修改用户权限组件分配.
     *
     * @param resourceItemAssignList 权限组件分配
     * @param userId                 用户ID
     * @param functionId             功能ID
     * @return 权限组件分配集合
     */
    List<ResourceItemAssign> updateResourceItemAssign(
            List<ResourceItemAssign> resourceItemAssignList,
            Long userId, Long functionId);

    /**
     * 根据用户ID和功能ID删除权限组件.
     *
     * @param userId     用户ID
     * @param functionId 功能ID
     */
    void deleteResourceItems(Long userId, Long functionId);

    /**
     * 用户管理修改密码.
     *
     * @param user          用户
     * @param passwordAgain 二次校验密码
     * @throws UserException 用户密码校验异常
     */
    void resetPassword(User user, String passwordAgain) throws UserException;

    /**
     * 修改当前用户密码.
     *
     * @param oldPassword      旧密码
     * @param newPassword      新密码
     * @param newPasswordAgain 新密码二次验证
     * @throws UserException 用户密码格式校验异常
     */
    void updateOwnerPassword(String oldPassword, String newPassword, String newPasswordAgain)
            throws UserException;

    /**
     * 第一次登录或密码失效时，修改密码.
     *
     * @param newPassword      新密码
     * @param newPasswordAgain 新密码二次验证
     * @throws UserException 用户密码格式校验异常
     */
    void firstAndExpiredLoginUpdatePassword(String newPassword, String newPasswordAgain)
            throws UserException;

    /**
     * 根据用户Id和角色Id集合获取权限组件分配Id集合.
     *
     * @param userId  用户Id
     * @param roleIds 角色Id集合
     * @return 权限组件分配Id集合
     */
    List<Long> getAllAssignElementIds(Long userId, List<Long> roleIds);

    /**
     * 根据UserDetails 返回一个User对象..
     *
     * @param userDetails spring security userDetails
     * @return User
     */
    User convertToUser(UserDetails userDetails);

    /**
     * 分页查询用户列表以及该用户拥有的角色.
     *
     * @param page                    页码
     * @param size                    分页大小
     * @param roleAssignmentSearchDTO 角色分配查询DTO
     * @return 用户分页列表
     */
    PageInfo<User> pagingQueryUsersWithRoles(int page, int size, RoleAssignmentSearchDTO roleAssignmentSearchDTO);

    /**
     * 根据用户名模糊查询用户列表(如果不传用户名 默认不查询 直接返回空列表).
     *
     * @param userName 用户名
     * @return 用户列表
     */
    List<User> fuzzyQueryUsersByUserName(String userName);
}
