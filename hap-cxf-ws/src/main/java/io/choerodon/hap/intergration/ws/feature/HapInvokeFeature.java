package io.choerodon.hap.intergration.ws.feature;

import io.choerodon.hap.intergration.ws.interceptor.HapInvokeInInterceptor;
import io.choerodon.hap.intergration.ws.interceptor.HapInvokeOutInterceptor;
import org.apache.cxf.Bus;
import org.apache.cxf.annotations.Provider;
import org.apache.cxf.common.injection.NoJSR250Annotations;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.interceptor.InterceptorProvider;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Created by Qixiangyu on 2016/12/2. 对cxf客户端和服务端的监控
 */
@NoJSR250Annotations
@Provider(value = Provider.Type.Feature)
@Component(value = "hapInvokeFeature")
public class HapInvokeFeature extends AbstractFeature implements InitializingBean {

    @Autowired
    private ApplicationContext applicationContext;

    // public static final int DEFAULT_LIMIT = 48 * 1024;
    private static final HapInvokeInInterceptor IN = new HapInvokeInInterceptor();

    private static final HapInvokeOutInterceptor OUT = new HapInvokeOutInterceptor();

    @Override
    protected void initializeProvider(InterceptorProvider provider, Bus bus) {
        provider.getInInterceptors().add(IN);
        // FaultInterceptors
        // 是出错的拦截器，正常情况还是走handleMessage处理，当出错拦截器本身出错才会走handleFault
        // provider.getInFaultInterceptors().add(IN);
        provider.getOutInterceptors().add(OUT);
        // provider.getOutFaultInterceptors().add(OUT);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        applicationContext.getAutowireCapableBeanFactory().autowireBean(IN);
        applicationContext.getAutowireCapableBeanFactory().autowireBean(OUT);
    }

}
