package io.choerodon.hap.iam.app.service.impl;

import io.choerodon.base.enums.ResourceType;
import io.choerodon.hap.iam.api.dto.CheckPermissionDTO;
import io.choerodon.hap.iam.app.service.PermissionService;
import io.choerodon.hap.iam.infra.dto.PermissionDTO;
import io.choerodon.hap.iam.infra.mapper.PermissionMapper;
import io.choerodon.web.core.IRequest;
import io.choerodon.web.core.impl.RequestHelper;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author wuguokai
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    private PermissionMapper permissionMapper;

    public PermissionServiceImpl(PermissionMapper permissionMapper) {
        this.permissionMapper = permissionMapper;
    }

    @Override
    public List<CheckPermissionDTO> checkPermission(List<CheckPermissionDTO> checkPermissionDTOList) {
        IRequest request = RequestHelper.getCurrentRequest();
        if (request == null || ArrayUtils.isEmpty(request.getAllRoleId())) {
            checkPermissionDTOList.forEach(i -> i.setApprove(false));
            return checkPermissionDTOList;
        }
        Long userId = request.getUserId();
        Long[] memberRoleIds = request.getAllRoleId();
        Set<String> resultCodes = new HashSet<>(checkSitePermission(checkPermissionDTOList, userId, memberRoleIds));
        checkPermissionDTOList.forEach(p -> {
            p.setApprove(false);
            if (resultCodes.contains(p.getCode())) {
                p.setApprove(true);
            }
        });
        return checkPermissionDTOList;
    }

    @Override
    public Set<PermissionDTO> queryByRoleIds(List<Long> roleIds) {
        Set<PermissionDTO> permissions = new HashSet<>();
        roleIds.forEach(roleId -> {
            List<PermissionDTO> permissionList = permissionMapper.selectByRoleId(roleId, null);
            permissions.addAll(permissionList);
        });
        return permissions;
    }

    private Set<String> checkSitePermission(final List<CheckPermissionDTO> checkPermissionDTOList, Long userId, final Long[] memberRoleIds) {
        Set<String> siteCodes = checkPermissionDTOList.stream().filter(i -> ResourceType.SITE.value().equals(i.getResourceType()))
                .map(CheckPermissionDTO::getCode).collect(Collectors.toSet());
        return permissionMapper.checkPermission(siteCodes, userId, memberRoleIds);
    }
}
