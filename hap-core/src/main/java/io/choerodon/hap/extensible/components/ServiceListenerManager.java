package io.choerodon.hap.extensible.components;

import io.choerodon.hap.core.AppContextInitListener;
import io.choerodon.hap.core.annotation.ServiceListener;
import io.choerodon.hap.core.util.CommonUtils;
import io.choerodon.hap.extensible.base.IServiceListener;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author shengyang.zhou@hand-china.com
 */
@Component
public class ServiceListenerManager implements AppContextInitListener {

    private Map<Class<?>, List<IServiceListener>> listenerMapping = new HashMap<>();

    @Override
    public void contextInitialized(ApplicationContext applicationContext) {
        Map<String, IServiceListener> map = applicationContext.getBeansOfType(IServiceListener.class);
        map.forEach((k, v) -> {
            ServiceListener annotation = AopUtils.getTargetClass(v).getAnnotation(ServiceListener.class);
            if (annotation == null) {
                System.err.println(v + " has no @ServiceListener");
            } else {
                Class<?> clazz = annotation.target();
                List<IServiceListener> list = listenerMapping.computeIfAbsent(clazz, k1 -> new ArrayList<>());
                list.add(v);
            }
        });

        listenerMapping.forEach((k, v) -> {
            v.sort((o1, o2) -> {
                ServiceListener a1 = o1.getClass().getAnnotation(ServiceListener.class);
                ServiceListener a2 = o2.getClass().getAnnotation(ServiceListener.class);
                if (CommonUtils.in(a2, (Object) a1.before())) {
                    return -1;
                }
                if (CommonUtils.in(a1, (Object) a2.before())) {
                    return 1;
                }
                if (CommonUtils.in(a2, (Object) a1.after())) {
                    return 1;
                }
                if (CommonUtils.in(a1, (Object) a2.after())) {
                    return -1;
                }
                return 0;
            });
        });

    }

    public List<IServiceListener> getRegisteredServiceListener(Class<?> clazz) {
        return listenerMapping.get(clazz);
    }
}
