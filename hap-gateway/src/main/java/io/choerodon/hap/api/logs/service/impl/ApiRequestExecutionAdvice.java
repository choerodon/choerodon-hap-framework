package io.choerodon.hap.api.logs.service.impl;

import com.google.common.base.Throwables;
import io.choerodon.hap.api.ApiConstants;
import io.choerodon.hap.api.gateway.dto.ApiServer;
import io.choerodon.hap.api.gateway.service.IApiServerService;
import io.choerodon.hap.api.logs.dto.ApiInvokeRecord;
import io.choerodon.hap.api.logs.dto.ApiResponseData;
import io.choerodon.hap.api.logs.info.ApiInvokeInfo;
import io.choerodon.hap.core.components.UserLoginInfoCollection;
import io.choerodon.hap.core.util.ExceptionUtil;
import io.choerodon.hap.intergration.controllers.HapInvokeRequestBodyAdvice;
import io.choerodon.hap.intergration.exception.HapApiException;
import io.choerodon.hap.message.components.InvokeApiManager;
import io.choerodon.message.IMessagePublisher;
import net.sf.json.JSONObject;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.UUID;

/**
 * @author peng.jiang@hand-china.com
 * @since 2017/9/25.
 */
@Component(value = "apiRequestExecutionAdvice")
public class ApiRequestExecutionAdvice implements MethodInterceptor {

    @Autowired
    private IMessagePublisher messagePublisher;

    @Autowired
    private IApiServerService serverService;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Long startTime = System.currentTimeMillis();
        ApiResponseData apiResponseData = new ApiResponseData();
        Throwable throwable = null;
        ApiInvokeRecord apiInvokeRecord = new ApiInvokeRecord();
        ApiInvokeInfo.API_INVOKE_RECORD.set(apiInvokeRecord);

        ApiServer server = null;
        try {

            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            OAuth2Authentication authentication = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();

            String clientId = authentication.getOAuth2Request().getClientId();

            //记录请求基本信息
            apiInvokeRecord.setInvokeId(UUID.randomUUID().toString());
            apiInvokeRecord.setRequestTime(new Date());
            apiInvokeRecord.setClientId(clientId);
            apiInvokeRecord.setClientId(clientId);
            apiInvokeRecord.setApiUrl(StringUtils.substringAfter(request.getRequestURI(), request.getContextPath()));
            apiInvokeRecord.setRequestMethod(request.getMethod());
            apiInvokeRecord.setIp(UserLoginInfoCollection.getIpAddress(request));
            apiInvokeRecord.setUserAgent(request.getHeader("User-Agent"));
            apiInvokeRecord.setReferer(StringUtils.abbreviate(request.getHeader("Referer"), 240));

            server = getServer(invocation);

            apiResponseData = (ApiResponseData) invocation.proceed();

            apiResponseData.setCode(ApiConstants.RESPONSEDATA_CODE_SUCCESS);

        } catch (Exception e) {
            throwable = e;
            apiResponseData.setSuccess(false);
            apiResponseData.setMessage(Throwables.getRootCause(throwable).getMessage().replaceAll("\"", ""));
            if (e instanceof HapApiException) {
                apiResponseData.setCode(((HapApiException) e).getCode());
            } else {
                apiResponseData.setCode(HapApiException.CODE_API_SYSTEM_EXCEPTION);
            }
            apiInvokeRecord.getApiInvokeRecordDetails().setApiRequestBodyParameter(HapInvokeRequestBodyAdvice.getAndRemoveBody());
            apiInvokeRecord.getApiInvokeRecordDetails().setApiResponseContent(JSONObject.fromObject(apiResponseData).toString());

        } finally {
            apiResponseData.setRequestId(apiInvokeRecord.getInvokeId());
            apiInvokeRecord.setResponseStatus(ApiInvokeInfo.RESPONSE_SUCESS);

            if (server != null && ApiConstants.ENABLE_FLAG_Y.equals(server.getApiInterface().getInvokeRecordDetails())) {
                apiInvokeRecord.getApiInvokeRecordDetails().setApiRequestBodyParameter(HapInvokeRequestBodyAdvice.getAndRemoveBody());
                apiInvokeRecord.getApiInvokeRecordDetails().setApiResponseContent(JSONObject.fromObject(apiResponseData).toString());
            }

            apiInvokeRecord.setApiResponseTime(System.currentTimeMillis() - startTime);

            processExceptionInfo(apiInvokeRecord, throwable);
            messagePublisher.message(InvokeApiManager.API_INVOKE, apiInvokeRecord);
            ApiInvokeInfo.clear();
        }
        return apiResponseData;
    }

    /**
     * 查找服务信息.
     *
     * @param invocation 方法调用信息
     * @return 服务信息
     */
    private ApiServer getServer(MethodInvocation invocation) {
        Object[] args = invocation.getArguments();
        String serverUrl = (String) args[1];
        String interfaceUrl = (String) args[2];
        return serverService.getByMappingUrl(serverUrl, interfaceUrl);
    }

    public static void processExceptionInfo(ApiInvokeRecord accessRecord, Throwable throwable) {
        if (throwable != null) {
            // 获取异常堆栈
            accessRecord.getApiInvokeRecordDetails().setStacktrace(ExceptionUtil.getRootCauseStackTrace(throwable));
            accessRecord.setResponseStatus(ApiInvokeInfo.RESPONSE_FAILURE);
        }
    }

}
