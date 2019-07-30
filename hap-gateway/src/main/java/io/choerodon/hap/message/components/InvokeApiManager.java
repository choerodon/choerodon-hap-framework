package io.choerodon.hap.message.components;

import io.choerodon.hap.api.application.dto.ApiApplication;
import io.choerodon.hap.api.application.mapper.ApiApplicationMapper;
import io.choerodon.hap.api.logs.InvokeApiStrategy;
import io.choerodon.hap.api.logs.dto.ApiInvokeRecord;
import io.choerodon.message.IMessageConsumer;
import io.choerodon.message.annotation.QueueMonitor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * api 消息接收
 * @author peng.jiang@hand-china.com
 * @since 017/9/23.
 */
@Component
@QueueMonitor(queue = InvokeApiManager.API_INVOKE)
public class InvokeApiManager implements IMessageConsumer<ApiInvokeRecord>, InitializingBean {

    public static final String API_INVOKE = "api_invoke";

    private InvokeApiStrategy invokeApiStrategy;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ApiApplicationMapper apiApplicationMapper;


    @Value("${api.invoke.logStrategy.class:io.choerodon.hap.api.logs.components.DefaultInvokeApiStrategy}")
    private String logStrategyClass;

    @Override
    public void onMessage(ApiInvokeRecord message, String pattern) {
        ApiApplication apiApplication = new ApiApplication();
        apiApplication.setClientId(message.getClientId());
        List<ApiApplication> list = apiApplicationMapper.selectApplications(apiApplication);
        if (list != null && !list.isEmpty()){
            message.setApplicationCode(list.get(0).getCode());
        }
        invokeApiStrategy.saveApiInvokeRecord(message);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        invokeApiStrategy = (InvokeApiStrategy) Class.forName(logStrategyClass).newInstance();
        applicationContext.getAutowireCapableBeanFactory().autowireBean(invokeApiStrategy);
    }

    public InvokeApiStrategy getInvokeApiStrategy(){
        return invokeApiStrategy;
    }

}
