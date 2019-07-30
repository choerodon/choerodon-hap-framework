package io.choerodon.hap.iam.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.hap.account.dto.User;
import io.choerodon.hap.account.exception.UserException;
import io.choerodon.hap.account.service.IUserService;
import io.choerodon.hap.iam.api.dto.RoleAssignmentDeleteDTO;
import io.choerodon.hap.iam.api.dto.RoleAssignmentSearchDTO;
import io.choerodon.hap.iam.api.validator.MemberRoleValidator;
import io.choerodon.hap.iam.app.service.ChoerodonRoleService;
import io.choerodon.hap.iam.app.service.RoleMemberService;
import io.choerodon.hap.iam.exception.ChoerodonRoleException;
import io.choerodon.hap.iam.exception.MemberRoleException;
import io.choerodon.hap.iam.infra.dto.MemberRoleDTO;
import io.choerodon.hap.iam.infra.dto.RoleDTO;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.constant.PageConstant;
import io.choerodon.web.controller.BaseController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author qiang.zeng
 */
@RestController
@RequestMapping(value = "/v1")
public class RoleMemberController extends BaseController {

    private IUserService userService;

    private ChoerodonRoleService roleService;

    private RoleMemberService roleMemberService;

    private MemberRoleValidator memberRoleValidator;

    public RoleMemberController(IUserService userService, ChoerodonRoleService roleService, RoleMemberService roleMemberService, MemberRoleValidator memberRoleValidator) {
        this.userService = userService;
        this.roleService = roleService;
        this.roleMemberService = roleMemberService;
        this.memberRoleValidator = memberRoleValidator;
    }

    /**
     * 批量分配用户角色.
     *
     * @param memberIds         用户Id集合
     * @param memberRoleDTOList 用户角色列表
     * @return 用户角色列表
     * @throws ChoerodonRoleException 角色异常
     * @throws MemberRoleException    用户角色异常
     * @throws UserException          用户异常
     */
    @Permission
    @PostMapping(value = "/site/role_members")
    public ResponseEntity<List<MemberRoleDTO>> saveUserRolesByMemberIds(@RequestParam(name = "member_ids") List<Long> memberIds,
                                                                        @RequestBody List<MemberRoleDTO> memberRoleDTOList) throws ChoerodonRoleException, MemberRoleException, UserException {
        memberRoleValidator.distributionRoleValidator(memberRoleDTOList);
        return new ResponseEntity<>(roleMemberService.saveUserRolesByMemberIds(memberIds, memberRoleDTOList), HttpStatus.OK);
    }

    /**
     * 批量移除用户的角色.
     *
     * @param roleAssignmentDeleteDTO 角色分配删除DTO
     * @return 操作是否成功
     * @throws MemberRoleException 用户角色删除异常
     */
    @Permission
    @PostMapping(value = "/site/role_members/delete")
    public ResponseEntity delete(@RequestBody RoleAssignmentDeleteDTO roleAssignmentDeleteDTO) throws MemberRoleException {
        roleMemberService.deleteAllUserRoles(roleAssignmentDeleteDTO);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }


    /**
     * 分页查询用户列表以及该用户拥有的角色.
     *
     * @param page                    页码
     * @param size                    分页大小
     * @param roleAssignmentSearchDTO 角色分配查询DTO
     * @return 用户分页列表
     */
    @Permission
    @PostMapping(value = "/site/role_members/users/roles")
    public ResponseEntity<PageInfo<User>> pagingQueryUsersWithRoles(
            @RequestParam(defaultValue = PageConstant.PAGE, required = false) final int page,
            @RequestParam(defaultValue = PageConstant.SIZE, required = false) final int size,
            @RequestBody(required = false) RoleAssignmentSearchDTO roleAssignmentSearchDTO) {
        return new ResponseEntity<>(userService.pagingQueryUsersWithRoles(page, size, roleAssignmentSearchDTO), HttpStatus.OK);
    }

    /**
     * 根据用户名模糊查询全平台用户列表(如果不传用户名 默认不查询 直接返回空列表).
     *
     * @param userName 用户名
     * @return 用户列表
     */
    @Permission
    @GetMapping(value = "/all/users")
    public ResponseEntity<List<User>> queryAllUsers(@RequestParam(value = "userName", required = false) String userName) {
        return new ResponseEntity<>(userService.fuzzyQueryUsersByUserName(userName), HttpStatus.OK);
    }

    /**
     * 根据角色名模糊查询全平台角色列表(如果不传角色名 默认不查询 直接返回空列表).
     *
     * @param roleName 角色名
     * @return 角色列表
     */
    @Permission
    @GetMapping(value = "/all/roles")
    public ResponseEntity<List<RoleDTO>> queryAllRoles(@RequestParam(value = "roleName", required = false) String roleName) {
        return new ResponseEntity<>(roleService.fuzzyQueryRolesByRoleName(roleName), HttpStatus.OK);
    }
}
