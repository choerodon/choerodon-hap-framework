package io.choerodon.hap.system.mapper;

import io.choerodon.hap.system.dto.DashBoard;
import io.choerodon.mybatis.common.Mapper;

import java.util.List;

/**
 * 
 * @author zhizheng.yang@hand-china.com
 */
public interface DashBoardMapper extends Mapper<DashBoard> {

    List<DashBoard> selectDashBoards(DashBoard dashBoard);

}
