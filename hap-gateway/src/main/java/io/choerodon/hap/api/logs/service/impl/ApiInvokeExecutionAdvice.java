package io.choerodon.hap.api.logs.service.impl;

import io.choerodon.hap.api.ApiConstants;
import io.choerodon.hap.api.gateway.dto.ApiServer;
import io.choerodon.hap.api.logs.dto.ApiInvokeRecord;
import io.choerodon.hap.api.logs.info.ApiInvokeInfo;
import io.choerodon.hap.intergration.exception.HapApiException;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.stereotype.Component;

/**
 * @author peng.jiang@hand-china.com
 * @since 2017/9/25.
 */
@Component(value = "apiInvokeExecutionAdvice")
public class ApiInvokeExecutionAdvice implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Long startTime = System.currentTimeMillis();
        ApiInvokeRecord apiInvokeRecord = ApiInvokeInfo.API_INVOKE_RECORD.get();
        Object result;
        Object[] args = invocation.getArguments();
        ApiServer server = (ApiServer) args[0];
        String parameter = (String) args[1];
        try {
            apiInvokeRecord.setServerCode(server.getCode());
            apiInvokeRecord.setServerName(server.getName());
            apiInvokeRecord.setInterfaceType(server.getServiceType());
            if (ApiConstants.SERVER_TYPE_SOAP.equals(server.getServiceType())) {
                apiInvokeRecord.setRequestUrl((server.getDomainUrl() != null ? server.getDomainUrl() : ""));
            } else {
                apiInvokeRecord.setRequestUrl((server.getDomainUrl() != null ? server.getDomainUrl() : "")
                        + (server.getApiInterface().getInterfaceUrl() != null ? server.getApiInterface().getInterfaceUrl() : ""));
            }

            result = invocation.proceed();
            if (ApiConstants.ENABLE_FLAG_Y.equals(server.getApiInterface().getInvokeRecordDetails())) {
                apiInvokeRecord.getApiInvokeRecordDetails().setRequestBodyParameter(parameter);
                apiInvokeRecord.getApiInvokeRecordDetails().setResponseContent(result.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
            //进入异常，记录请求详细信息
            apiInvokeRecord.getApiInvokeRecordDetails().setRequestBodyParameter(parameter);
            if (e instanceof HapApiException && ((HapApiException) e).getCode().startsWith(HapApiException.CODE_API_THIRD_REQUEST)) {
                apiInvokeRecord.getApiInvokeRecordDetails().setResponseContent(((HapApiException) e).getDescriptionKey());
            }
            throw e;
        } finally {

            apiInvokeRecord.setResponseTime(System.currentTimeMillis() - startTime);
        }

        return result;
    }

}
