package io.choerodon.hap.hr.mapper;

import io.choerodon.hap.hr.dto.Position;
import io.choerodon.mybatis.common.Mapper;

import java.util.List;

/**
 * 岗位Mapper.
 *
 * @author hailin.xu@hand-china.com
 */
public interface PositionMapper extends Mapper<Position> {
    /**
     * 通过员工编码查询岗位.
     *
     * @param employeeCode 员工编码
     * @return 岗位列表
     */
    List<Position> getPositionByEmployeeCode(String employeeCode);

    /**
     * 通过岗位编码查询岗位.
     *
     * @param positionCode 岗位编码
     * @return 岗位
     */
    Position getPositionByCode(String positionCode);

}
