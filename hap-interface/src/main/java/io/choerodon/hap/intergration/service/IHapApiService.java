/**
 * Copyright (c) 2016. Hand Enterprise Solution Company. All right reserved. Project Name:HmapParent
 * Package Name:hmap.core.hms.service.impl Date:2016/8/12 0012 Create By:zongyun.zhou@hand-china.com
 *
 */
package io.choerodon.hap.intergration.service;

import io.choerodon.hap.intergration.dto.HapInterfaceHeader;
import net.sf.json.JSONObject;

public interface IHapApiService {
   JSONObject invoke(HapInterfaceHeader hapInterfaceHeader, JSONObject inbound) throws Exception;
}
