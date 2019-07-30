package io.choerodon.hap.dataset.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.model.DatasetExecutor;
import io.choerodon.hap.dataset.model.impl.ServiceDatasetExecutor;
import io.choerodon.hap.dataset.service.IDatasetRepositoryService;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.web.json.JacksonMapper;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xausky
 */
@Service
@Transactional(rollbackFor = DatasetException.class)
public class DatasetRepositoryServiceImpl implements IDatasetRepositoryService, ApplicationListener {
    private static final ObjectMapper MAPPER = new JacksonMapper();
    private static final Logger LOGGER = LoggerFactory.getLogger(DatasetRepositoryServiceImpl.class);
    private Map<String, DatasetExecutor> executors = new ConcurrentHashMap<>();
    @Autowired
    private SqlSessionTemplate template;

    public void contextInitialized(ApplicationContext context) {
        template.getConfiguration().setUseGeneratedKeys(true);
        context.getBeansOfType(IDatasetService.class);
        Map datasets = context.getBeansWithAnnotation(Dataset.class);
        for (Object service : datasets.values()) {
            if (service instanceof IDatasetService) {
                Dataset dataset = AnnotationUtils.findAnnotation(service.getClass(), Dataset.class);
                if (dataset == null) {
                    LOGGER.warn("IDatasetService class {} not annotation @Dataset skip Dataset create.", service.getClass());
                    continue;
                }
                executors.put(dataset.value(), new ServiceDatasetExecutor((IDatasetService) service, MAPPER, dataset.value(), template.getConfiguration()));
            } else {
                LOGGER.warn("{} annotation @Dataset but not implement IDatasetService skip Dataset create.", service.getClass());
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<?> queries(String name, Map<String, Object> body, int pageNum, int pageSize, String sortname, boolean isDesc) {
        DatasetExecutor executor = executors.get(name);
        if (executor == null) {
            throw new DatasetException("dataset.notFound", name);
        }
        return executor.queries(body, pageNum, pageSize, sortname, isDesc);
    }

    @Override
    public List<?> mutations(String name, String body) {
        DatasetExecutor executor = executors.get(name);
        if (executor == null) {
            throw new DatasetException("dataset.notFound", name);
        }
        return executor.mutations(body, null);
    }

    @Override
    public Map<String, Object> languages(String name, Map<String, Object> body) {
        DatasetExecutor executor = executors.get(name);
        Map<String, Object> result = executor.languages(body);
        body.put("success", true);
        return result;
    }

    @Override
    public List<Boolean> validate(String name, Map<String, Object> body) {
        DatasetExecutor executor = executors.get(name);
        if (executor == null) {
            throw new DatasetException("dataset.notFound", name);
        }
        return executor.validate(body, null);
    }

    @Override
    public DatasetExecutor getExecutor(String name) {
        return executors.get(name);
    }

    @Override
    public void putExecutor(String name, DatasetExecutor executor) {
        executors.put(name, executor);
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            contextInitialized(((ContextRefreshedEvent) event).getApplicationContext());
        }
    }
}
