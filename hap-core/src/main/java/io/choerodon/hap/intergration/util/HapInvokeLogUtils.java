package io.choerodon.hap.intergration.util;

import io.choerodon.hap.core.components.UserLoginInfoCollection;
import io.choerodon.hap.core.util.ExceptionUtil;
import io.choerodon.hap.intergration.beans.HapInvokeInfo;
import io.choerodon.hap.intergration.controllers.HapInvokeRequestBodyAdvice;
import io.choerodon.hap.intergration.dto.HapInterfaceInbound;
import io.choerodon.hap.intergration.dto.HapInterfaceOutbound;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Qixiangyu
 * @since 2017/2/21.
 */
public class HapInvokeLogUtils {

    public static void processRequestInfo(HapInterfaceInbound inbound, HttpServletRequest request) {
        inbound.setUserAgent(request.getHeader("User-Agent"));
        if (inbound.getRequestMethod() == null)
            inbound.setRequestMethod(request.getMethod());
        if (inbound.getInterfaceUrl() == null)
            inbound.setInterfaceUrl(request.getServletPath());
        if (inbound.getRequestHeaderParameter() == null)
            inbound.setRequestHeaderParameter(request.getQueryString());
        if (inbound.getRequestBodyParameter() == null)
            inbound.setRequestBodyParameter(HapInvokeRequestBodyAdvice.getAndRemoveBody());
        inbound.setIp(UserLoginInfoCollection.getIpAddress(request));
    }

    public static void processExceptionInfo(HapInterfaceInbound inbound, Throwable throwable) {
        if (throwable != null) {
            // 获取异常堆栈
            inbound.setStackTrace(ExceptionUtil.getRootCauseStackTrace(throwable));
            inbound.setRequestStatus(HapInvokeInfo.REQUEST_FAILURE);
        }
    }

    public static void processExceptionInfo(HapInterfaceOutbound outbound, Throwable throwable) {
        if (throwable != null) {
            // 获取异常堆栈
            outbound.setStackTrace(ExceptionUtil.getRootCauseStackTrace(throwable));
            outbound.setRequestStatus(HapInvokeInfo.REQUEST_FAILURE);
        }
    }


}
