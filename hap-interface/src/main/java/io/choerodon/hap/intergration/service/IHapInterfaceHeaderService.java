package io.choerodon.hap.intergration.service;

import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.intergration.dto.HapInterfaceHeader;
import io.choerodon.mybatis.service.IBaseService;

import java.util.List;

/**
 * @author jiguang.sun@hand-china.com
 * @version 2016/7/21.
 */
public interface IHapInterfaceHeaderService extends IBaseService<HapInterfaceHeader>, ProxySelf<IHapInterfaceHeaderService> {


    List<HapInterfaceHeader> getAllHeader(HapInterfaceHeader interfaceHeader, int page, int pagesize);

    List<HapInterfaceHeader> getHeaderAndLineList(HapInterfaceHeader interfaceHeader);

    HapInterfaceHeader getHeaderAndLine(String sysName, String apiName);

    List<HapInterfaceHeader> getAllHeaderAndLine();

    List<HapInterfaceHeader> getAllHeaderAndLine(int page, int pagesize);

    List<HapInterfaceHeader> getHeaderByHeaderId(HapInterfaceHeader HapInterfaceHeader);

    HapInterfaceHeader getHeaderAndLineByLineId(HapInterfaceHeader HapInterfaceHeader);

    int updateHeader(HapInterfaceHeader hmsInterfaceHeader);

    void createInterface(HapInterfaceHeader interfaceHeader);

    void updateInterface(HapInterfaceHeader interfaceHeader);

}
