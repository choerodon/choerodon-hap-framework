package io.choerodon.hap.intergration.service;

import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.intergration.dto.HapInterfaceHeader;
import io.choerodon.hap.intergration.dto.HapInterfaceLine;
import io.choerodon.mybatis.service.IBaseService;
import io.choerodon.web.core.IRequest;

import java.util.List;

/**
 * @author user
 * @since 2016/7/26.
 */
public interface IHapInterfaceLineService extends IBaseService<HapInterfaceLine>, ProxySelf<IHapInterfaceLineService> {

    List<HapInterfaceLine> getLineAndLineTl(IRequest request, HapInterfaceLine hapInterfaceLine);

    List<HapInterfaceLine> getLinesByHeaderId(IRequest request, HapInterfaceLine lineAndLineTlDTO, int page, int pagesize);

    int insertLine(IRequest request, HapInterfaceLine hapInterfaceLine);

    int updateLine(IRequest request, HapInterfaceLine hapInterfaceLine);

    int batchDeleteByHeaders(IRequest request, List<HapInterfaceHeader> lists);
}
