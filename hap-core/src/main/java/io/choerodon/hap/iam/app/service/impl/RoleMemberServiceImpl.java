package io.choerodon.hap.iam.app.service.impl;

import io.choerodon.base.enums.ResourceType;
import io.choerodon.hap.account.dto.User;
import io.choerodon.hap.account.exception.RoleException;
import io.choerodon.hap.account.exception.UserException;
import io.choerodon.hap.account.service.IUserService;
import io.choerodon.hap.iam.api.dto.RoleAssignmentDeleteDTO;
import io.choerodon.hap.iam.app.service.RoleMemberService;
import io.choerodon.hap.iam.exception.MemberRoleException;
import io.choerodon.hap.iam.infra.dto.MemberRoleDTO;
import io.choerodon.hap.iam.infra.enums.MemberType;
import io.choerodon.hap.iam.infra.mapper.MemberRoleMapper;
import io.choerodon.mybatis.service.BaseServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.choerodon.hap.account.exception.UserException.USER_NOT_EXIST;
import static io.choerodon.hap.iam.exception.MemberRoleException.ERROR_MEMBER_ROLE_NOT_EXISTED;
import static io.choerodon.mybatis.entity.BaseDTO.STATUS_ADD;
import static io.choerodon.mybatis.entity.BaseDTO.STATUS_DELETE;

/**
 * @author qiang.zeng
 */
@Service
public class RoleMemberServiceImpl extends BaseServiceImpl<MemberRoleDTO> implements RoleMemberService {

    private MemberRoleMapper memberRoleMapper;

    private IUserService userService;

    public RoleMemberServiceImpl(MemberRoleMapper memberRoleMapper, IUserService userService) {
        this.memberRoleMapper = memberRoleMapper;
        this.userService = userService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<MemberRoleDTO> saveUserRolesByMemberIds(List<Long> memberIds, List<MemberRoleDTO> memberRoleDTOList) throws MemberRoleException, UserException {
        List<MemberRoleDTO> memberRoleDTOS = new ArrayList<>();
        for (Long memberId : memberIds) {
            memberRoleDTOList.forEach(m ->
                    m.setMemberId(memberId)
            );
            memberRoleDTOS.addAll(saveUserRolesByMemberId(memberId, memberRoleDTOList));
        }
        return memberRoleDTOS;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAllUserRoles(RoleAssignmentDeleteDTO roleAssignmentDeleteDTO) throws MemberRoleException {
        if (roleAssignmentDeleteDTO != null && roleAssignmentDeleteDTO.getData() != null) {
            Map<Long, List<Long>> data = roleAssignmentDeleteDTO.getData();
            for (Map.Entry<Long, List<Long>> entry : data.entrySet()) {
                Long userId = entry.getKey();
                List<Long> roleIds = entry.getValue();
                if (CollectionUtils.isNotEmpty(roleIds)) {
                    for (Long roleId : roleIds) {
                        deleteUserRoles(userId, roleId);
                    }
                }
            }
        }
    }

    @Override
    public void checkUserRoleExists(Long userId, Long roleId) throws RoleException {
        if (memberRoleMapper.selectCountByMemberIdAndRoleId(userId, roleId) != 1) {
            throw new RoleException(RoleException.MSG_INVALID_USER_ROLE, RoleException.MSG_INVALID_USER_ROLE, null);
        }
    }

    private List<MemberRoleDTO> saveUserRolesByMemberId(Long memberId, List<MemberRoleDTO> memberRoles) throws UserException, MemberRoleException {
        List<MemberRoleDTO> resultList = new ArrayList<>();
        User user = new User();
        user.setUserId(memberId);
        User userDTO = userService.selectByPrimaryKey(user);
        if (userDTO == null) {
            throw new UserException(USER_NOT_EXIST);
        }
        for (MemberRoleDTO memberRole : memberRoles) {
            String status = memberRole.get__status();
            if (!StringUtils.isEmpty(status)) {
                switch (status) {
                    case STATUS_ADD:
                        resultList.add(insertUserRole(memberId, memberRole.getRoleId()));
                        break;
                    case STATUS_DELETE:
                        deleteUserRoles(memberId, memberRole.getRoleId());
                        break;
                    default:
                        break;
                }
            }
        }
        return resultList;
    }

    /**
     * 根据用户Id和角色Id插入用户角色.
     *
     * @param memberId 用户Id
     * @param roleId   角色Id
     * @return 用户角色
     */
    private MemberRoleDTO insertUserRole(Long memberId, Long roleId) {
        MemberRoleDTO result = selectUserRole(memberId, roleId);
        if (result == null) {
            result = new MemberRoleDTO();
            result.setRoleId(roleId);
            result.setMemberId(memberId);
            result.setMemberType(MemberType.USER.value());
            result.setSourceId(0L);
            result.setSourceType(ResourceType.SITE.value());
            memberRoleMapper.insertSelective(result);
        }
        return result;
    }

    /**
     * 根据用户Id和角色Id删除用户角色.
     *
     * @param memberId 用户Id
     * @param roleId   角色Id
     * @throws MemberRoleException 用户角色不存在异常
     */
    private void deleteUserRoles(Long memberId, Long roleId) throws MemberRoleException {
        MemberRoleDTO mr = selectUserRole(memberId, roleId);
        if (mr == null) {
            throw new MemberRoleException(ERROR_MEMBER_ROLE_NOT_EXISTED, roleId, memberId);
        }
        memberRoleMapper.deleteByPrimaryKey(mr.getId());
    }

    /**
     * 根据用户Id和角色Id查询用户角色.
     *
     * @param memberId 用户Id
     * @param roleId   角色Id
     * @return 用户角色或者null
     */
    private MemberRoleDTO selectUserRole(Long memberId, Long roleId) {
        MemberRoleDTO memberRole = new MemberRoleDTO();
        memberRole.setRoleId(roleId);
        memberRole.setMemberId(memberId);
        memberRole.setMemberType(MemberType.USER.value());
        return memberRoleMapper.selectOne(memberRole);
    }
}
