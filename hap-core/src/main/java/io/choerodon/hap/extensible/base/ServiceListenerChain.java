package io.choerodon.hap.extensible.base;

import io.choerodon.hap.extensible.components.ServiceListenerManager;
import io.choerodon.web.core.IRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shengyang.zhou@hand-china.com
 */
public class ServiceListenerChain<T> {

    private ServiceListenerManager manager;
    private Object service;

    private List<IServiceListener<T>> serviceListeners = new ArrayList<>();

    private int index = 0;

    ServiceListenerChain() {

    }

    public ServiceListenerChain(ServiceListenerManager manager, Object service) {
        super();
        this.manager = manager;
        this.service = service;
        List<IServiceListener> list = manager.getRegisteredServiceListener(service.getClass());
        if (list != null) {
            for (IServiceListener serviceListener : list) {
                serviceListeners.add(serviceListener);
            }
        }
    }

    public T beforeInsert(IRequest iRequest, T record) {
        if (index < serviceListeners.size()) {
            IServiceListener<T> current = serviceListeners.get(index++);
            current.beforeInsert(iRequest, record, this);
        }

        return record;
    }

    public T afterInsert(IRequest request, T record) {
        if (index < serviceListeners.size()) {
            IServiceListener<T> current = serviceListeners.get(index++);
            current.afterInsert(request, record, this);
        }

        return record;
    }

    public T beforeUpdate(IRequest iRequest, T record) {
        if (index < serviceListeners.size()) {
            IServiceListener<T> current = serviceListeners.get(index++);
            current.beforeUpdate(iRequest, record, this);
        }

        return record;
    }

    public T afterUpdate(IRequest request, T record) {
        if (index < serviceListeners.size()) {
            IServiceListener<T> current = serviceListeners.get(index++);
            current.afterUpdate(request, record, this);
        }

        return record;
    }

    public T beforeDelete(IRequest iRequest, T record) {
        if (index < serviceListeners.size()) {
            IServiceListener<T> current = serviceListeners.get(index++);
            current.beforeDelete(iRequest, record, this);
        }

        return record;
    }

    public T afterDelete(IRequest request, T record) {
        if (index < serviceListeners.size()) {
            IServiceListener<T> current = serviceListeners.get(index++);
            current.afterDelete(request, record, this);
        }

        return record;
    }

    public ServiceListenerChain copy() {
        ServiceListenerChain copy = new ServiceListenerChain();
        copy.manager = manager;
        copy.service = service;
        copy.serviceListeners.addAll(serviceListeners);
        copy.index = 0;
        return copy;
    }
}
