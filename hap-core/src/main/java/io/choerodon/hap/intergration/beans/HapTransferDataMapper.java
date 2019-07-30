/**
 * Copyright (c) 2016. Hand Enterprise Solution Company. All right reserved.
 * Project Name:HmapParent
 * Package Name:hmap.core.beans 
 * Date:2016/8/12 0012
 * Create By:zongyun.zhou@hand-china.com
 *
 */
package io.choerodon.hap.intergration.beans;

import net.sf.json.JSONObject;

public abstract class HapTransferDataMapper {
    public abstract String requestDataMap(JSONObject params);

    public abstract String responseDataMap(String params);

}
