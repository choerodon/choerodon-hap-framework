package io.choerodon.hap.iam.app.service;

import io.choerodon.hap.iam.api.dto.CheckPermissionDTO;
import io.choerodon.hap.iam.infra.dto.PermissionDTO;
import io.choerodon.mybatis.service.IBaseService;

import java.util.List;
import java.util.Set;

/**
 * @author wuguokai
 */
public interface PermissionService extends IBaseService<PermissionDTO> {

    List<CheckPermissionDTO> checkPermission(List<CheckPermissionDTO> checkPermissionDTOList);

    Set<PermissionDTO> queryByRoleIds(List<Long> roleIds);
}
