package io.choerodon.hap.dataset.model.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.model.DatasetExecutor;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.mybatis.entity.CustomEntityColumn;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.AnnotationUtils;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.entity.EntityTable;
import tk.mybatis.mapper.mapperhelper.EntityHelper;

import javax.persistence.Table;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author xausky
 */
public class ServiceDatasetExecutor implements DatasetExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceDatasetExecutor.class);
    private Class<?> generic;
    private ObjectMapper mapper;
    private IDatasetService service;
    private CommonLanguageAction languageAction;
    private CommonValidateAction validateAction;

    public ServiceDatasetExecutor(IDatasetService service, ObjectMapper mapper, String name, Configuration configuration) {
        this.mapper = mapper;
        this.service = service;
        this.generic = GenericTypeResolver.resolveTypeArgument(service.getClass(), IDatasetService.class);
        if (generic == null){
            LOGGER.warn("IDatasetService {} not def generic , skip multi-language and validate support.", service.getClass());
            return;
        }
        Table tableAnnotation = AnnotationUtils.findAnnotation(generic, Table.class);
        if (tableAnnotation == null) {
            LOGGER.warn("Class not annotation @Table {}, skip multi-language and validate support.", generic);
            return;
        }
        EntityHelper.getColumns(generic);
        String tableName = tableAnnotation.name().toLowerCase();
        EntityTable table = EntityHelper.getEntityTable(generic);
        if (table == null) {
            throw new DatasetException("dataset table notFound: " + tableName);
        }
        if (table.getEntityClassPKColumns().isEmpty()) {
            LOGGER.warn("Class not annotation @ID {}, skip multi-language and validate support.", generic);
            return;
        }
        String primaryKey = table.getEntityClassPKColumns().iterator().next().getProperty();
        Set<String> tlColumns = new TreeSet<>();
        Set<String> columns = new TreeSet<>();
        for (EntityColumn column : table.getEntityClassColumns()) {
            if (column instanceof CustomEntityColumn && Boolean.TRUE.equals(((CustomEntityColumn) column).isMultiLanguage())) {
                tlColumns.add(column.getColumn());
            }
            columns.add(column.getColumn());
        }
        if (!tlColumns.isEmpty()) {
            languageAction = new CommonLanguageAction(name, primaryKey, tableName, tlColumns, configuration);
        }
        validateAction = new CommonValidateAction(name, tableName, columns, configuration);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Object> queries(Map<String, Object> body, Integer pageNum, Integer pageSize, String sortname, Boolean isDesc) {
        return service.queries(body, pageNum, pageSize, sortname, isDesc);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Object> mutations(String json, Map.Entry<String, Object> parentKey) {
        try {
            List objs = mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, generic));
            return service.mutations(objs);
        } catch (IOException e) {
            throw new DatasetException("dataset.error", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> languages(Map<String, Object> body) {
        if (languageAction == null) {
            throw new DatasetException("dataset.language.unsupported");
        }
        return (Map<String, Object>) languageAction.invoke(body);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Boolean> validate(Map<String, Object> body, Map.Entry<String, Object> parentKey) {
        if (validateAction == null) {
            throw new DatasetException("dataset.validate.unsupported");
        }
        return (List<Boolean>)validateAction.invoke(body);
    }
}
