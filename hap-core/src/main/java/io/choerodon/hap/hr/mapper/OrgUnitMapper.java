package io.choerodon.hap.hr.mapper;

import io.choerodon.hap.hr.dto.HrOrgUnit;
import io.choerodon.mybatis.common.Mapper;

import java.util.List;

/**
 * 部门组织Mapper.
 *
 * @author jialong.zuo@hand-china.com
 */
public interface OrgUnitMapper extends Mapper<HrOrgUnit> {
    /**
     * 条件查询部门组织.
     *
     * @param hrOrgUnit 部门组织
     * @return 部门组织列表
     */
    List<HrOrgUnit> selectUnit(HrOrgUnit hrOrgUnit);
}
