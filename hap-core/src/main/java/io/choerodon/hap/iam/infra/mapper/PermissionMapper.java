package io.choerodon.hap.iam.infra.mapper;

import io.choerodon.hap.iam.infra.dto.PermissionDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author wuguokai
 */
public interface PermissionMapper extends Mapper<PermissionDTO> {
    Set<String> checkPermission(@Param("codes") Set<String> codes, @Param("memberId") Long memberid, @Param("memberRoleIds") Long[] memberRoleIds);

    List<PermissionDTO> selectByRoleId(@Param("roleId") Long roleId,
                                       @Param("params") String params);

}
