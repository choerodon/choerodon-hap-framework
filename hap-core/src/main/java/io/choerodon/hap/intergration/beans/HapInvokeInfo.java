package io.choerodon.hap.intergration.beans;

import io.choerodon.hap.intergration.dto.HapInterfaceInbound;
import io.choerodon.hap.intergration.dto.HapInterfaceOutbound;

/**
 * Created by Qixiangyu on 2016/11/21.
 */
public class HapInvokeInfo {

    public static final String INVOKE_INFO_OUTBOUND = "HAP_OUTBOUND";

    public static final String INVOKE_INFO_INBOUND = "HAP_INBOUND";

    public static final ThreadLocal<Integer> HTTP_RESPONSE_CODE = new ThreadLocal<>();

    public static final ThreadLocal<String> OUTBOUND_REQUEST_PARAMETER = new ThreadLocal<>();

    public static final ThreadLocal<String> OUTBOUND_RESPONSE_DATA = new ThreadLocal<>();

    public static final String REQUEST_SUCESS = "success";

    public static final String REQUEST_FAILURE = "failure";

    public static final ThreadLocal<HapInterfaceInbound> INBOUND = new ThreadLocal<>();

    public static final ThreadLocal<HapInterfaceOutbound> OUTBOUND = new ThreadLocal<>();

    public static final ThreadLocal<Integer> TOKEN_TASK_COUNT = new ThreadLocal<>();

    public static void clearOutboundInfo(){
        HTTP_RESPONSE_CODE.remove();
        OUTBOUND_REQUEST_PARAMETER.remove();
        OUTBOUND_RESPONSE_DATA.remove();
    }

}
