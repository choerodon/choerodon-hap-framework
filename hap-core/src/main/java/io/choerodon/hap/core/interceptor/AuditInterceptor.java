package io.choerodon.hap.core.interceptor;

import io.choerodon.hap.audit.service.IAuditService;
import io.choerodon.hap.audit.service.IAuditTableNameProvider;
import io.choerodon.hap.audit.service.impl.DefaultAuditTableNameProvider;
import io.choerodon.hap.core.ITlTableNameProvider;
import io.choerodon.hap.core.annotation.AuditEnabled;
import io.choerodon.hap.core.impl.DefaultTlTableNameProvider;
import io.choerodon.hap.core.util.IDGenerator;
import io.choerodon.hap.system.dto.DTOClassInfo;
import io.choerodon.base.helper.ApplicationContextHelper;
import io.choerodon.mybatis.annotation.EnableExtensionAttribute;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.entity.BaseDTO;
import io.choerodon.mybatis.util.OGNL;
import io.choerodon.web.core.IRequest;
import io.choerodon.web.core.impl.RequestHelper;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import tk.mybatis.mapper.entity.EntityField;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

/**
 * 审计拦截器.
 *
 * @author shengyang.zhou@hand-china.com
 *         qiang.zeng@hand-china.com
 *         lijian.yin@hand-china.com
 */
@Order(1000)
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class AuditInterceptor implements Interceptor {

    private Logger logger = LoggerFactory.getLogger(AuditInterceptor.class);

    /**
     * 暂不使用
     */
    public static final ThreadLocal<String> LOCAL_AUDIT_SESSION = new ThreadLocal<>();

    private ApplicationContext beanFactory;

    private ITlTableNameProvider tableNameProvider = DefaultTlTableNameProvider.getInstance();

    private IAuditService iAuditService;

    @Autowired(required = false)
    private IAuditTableNameProvider auditTableNameProvider = DefaultAuditTableNameProvider.instance;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object param = invocation.getArgs()[1];
        Object targetDto = null;

        if (param instanceof Map) {
            Map<String, Object> temp = (Map<String, Object>) param;
            for (Map.Entry<String, Object> entry : temp.entrySet()) {
                if ("dto".equals(entry.getKey())) {
                    targetDto = temp.get("dto");
                    break;
                }
            }
        } else {
            targetDto = param;
        }

        if (null == targetDto || !(targetDto instanceof BaseDTO)) {
            return invocation.proceed();
        }
        // only enable for @AuditEnabled
        if (targetDto.getClass().getAnnotation(AuditEnabled.class) == null) {
            return invocation.proceed();
        }

        BaseDTO dto = (BaseDTO) targetDto;

        Object result;
        SqlCommandType type = mappedStatement.getSqlCommandType();

        switch (type) {
            case INSERT:
            case UPDATE:
                result = invocation.proceed();
                doAudit(dto, type.name(), mappedStatement, param);
                break;
            case DELETE:
                doAudit(dto, type.name(), mappedStatement, param);
                result = invocation.proceed();
                break;
            default:
                result = invocation.proceed();
                break;
        }
        return result;
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {

    }

    /**
     * 审计逻辑,整理产生审计记录参数.
     *
     * @param dto             SQL参数
     * @param type            SQL类型
     * @param mappedStatement MappedStatement对象 对应mapper配置文件中一个节点
     * @throws Exception 解析SQL、反射获取字段异常
     */
    private void doAudit(BaseDTO dto, String type, MappedStatement mappedStatement, Object param) throws Exception {
        loadService();

        Class clazz = dto.getClass();
        String tableName = getTableName(clazz);

        if (StringUtils.isBlank(tableName)) {
            return;
        }

        EntityField[] ids = DTOClassInfo.getIdFields(clazz);
        if (ids.length == 0) {
            if (logger.isDebugEnabled()) {
                logger.debug("Can not get PrimaryKey(s) of dto class:" + clazz);
            }
            return;
        }

        boolean hasExtensionAttribute = clazz.isAnnotationPresent(EnableExtensionAttribute.class);
        List<String> cols = new ArrayList<>();
        List<String> valueFields = new ArrayList<>();

        for (Map.Entry<String, EntityField> entry : DTOClassInfo.getEntityFields(clazz).entrySet()) {
            // 修复审计时间问题
            if ("lastUpdatedBy".equalsIgnoreCase(entry.getValue().getName())) {
                continue;
            }
            if (!entry.getValue().isAnnotationPresent(Transient.class)) {
                if (entry.getValue().getName().startsWith("attribute") && !hasExtensionAttribute) {
                    continue;
                } else {
                    cols.add(DTOClassInfo.getColumnName(entry.getValue()));
                    valueFields.add("#{" + DTOClassInfo.getColumnName(entry.getValue()) + "}");
                }
            }
        }

        EntityField[] entityFields = DTOClassInfo.getMultiLanguageFields(clazz);

        // 判别是否使用多语言注解
        boolean isMultiLanguageTable = clazz.getAnnotation(MultiLanguage.class) != null;

        // 获取当前语言
        IRequest request = RequestHelper.getCurrentRequest(true);
        String localLanguage = request.getLocale();

        // 多语言字段用别名TL. 非多语言字段别名BASE_TABLE.
        List<String> dynFields = new ArrayList<>();
        // 生成审计记录 参数集合
        Map<String, Object> auditParam = new HashMap<>(50);
        // 修改审计记录 参数集合
        Map<String, Object> updateParam = new HashMap<>(50);

        // 获取执行sql
        BoundSql boundSql = mappedStatement.getBoundSql(param);
        String sql = boundSql.getSql();
        auditParam.put("sql", sql);

        //获取where_clause表达式及占位符实参
        Map<String, Object> wps = getWhereClause(mappedStatement, param, isMultiLanguageTable);
        for (Map.Entry<String, Object> entry : wps.entrySet()) {
            auditParam.put(entry.getKey(), entry.getValue());
            if ("objectVersionNumber".equals(entry.getKey()) && !"delete".equalsIgnoreCase(type)) {
                updateParam.put(entry.getKey(), Integer.parseInt(entry.getValue().toString()) - 1);
            } else {
                updateParam.put(entry.getKey(), entry.getValue());
            }
        }
        String alias = wps.get("_ALIAS") == null ? "" : wps.get("_ALIAS").toString();
        for (String s : cols) {
            if (isMultiLanguageTable) {
                for (EntityField entityField : entityFields) {
                    if (DTOClassInfo.camelToUnderLine(entityField.getName()).toLowerCase().equals(s.toLowerCase())) {
                        s = "TL." + s;
                        break;
                    }
                }
                dynFields.add(s.contains("TL.") ? s : alias + "." + s);
            } else {
                dynFields.add(s);
            }
        }

        if (isMultiLanguageTable) {
            // 多语言表名
            String multiLanguageTableName = tableNameProvider.getTlTableName(tableName);
            // 获取主键
            auditParam.put("_MAJOR_FIELD", DTOClassInfo.camelToUnderLine(ids[0].getName()).toUpperCase());
            auditParam.put("_MAJOR_FIELD_B", alias + "." + DTOClassInfo.camelToUnderLine(ids[0].getName()).toUpperCase());
            auditParam.put("_LANGUAGE_TABLE_NAME", multiLanguageTableName);
        }

        AuditEnabled auditEnabled = (AuditEnabled) clazz.getAnnotation(AuditEnabled.class);
        //审计表名
        String auditTableName = StringUtils.defaultIfEmpty(auditEnabled.auditTable(),
                auditTableNameProvider.getAuditTableName(tableName));

        auditParam.put("_AUDIT_TABLE_NAME", auditTableName);
        auditParam.put("_COLS", cols);
        auditParam.put("_FIELD_VALUE", valueFields);
        auditParam.put("_AUDIT_TYPE", type);
        auditParam.put("_AUDIT_SESSION_ID", IDGenerator.getInstance().generate());
        auditParam.put("_DYN_FIELDS", dynFields);
        auditParam.put("_BASE_TABLE_NAME", tableName + " " + alias);
        // 使用 LAST_UPDATE_BY 存放audit operator
        auditParam.put("_LAST_UPDATED_BY", request.getUserId());

        updateParam.put("_AUDIT_TABLE_NAME", auditTableName + " " + alias);
        updateParam.put("_ALIAS", alias);

        if (isMultiLanguageTable) {
            Set<String> languages = OGNL.getSupportedLanguages();
            for (String language : languages) {
                auditParam.put("_LANGUAGE", language);
                updateParam.put("_LANG", language);
                try {
                    doDB(auditParam, updateParam);
                } catch (Exception e) {
                }
            }
        } else {
            auditParam.put("_LANGUAGE", localLanguage);
            updateParam.put("_LANG", localLanguage);
            try {
                doDB(auditParam, updateParam);
            } catch (Exception e) {
            }
        }
    }

    /**
     * 获取sql where条件 并将？替换为参数 eg #{name}.
     *
     * @param mappedStatement MappedStatement对象
     * @param param           参数
     * @return where条件字符串
     * @throws JSQLParserException  sql解析异常
     * @throws NoSuchFieldException 无字段异常
     */
    private Map<String, Object> getWhereClause(MappedStatement mappedStatement,
                                               Object param, boolean isMulti) throws JSQLParserException, NoSuchFieldException {
        BoundSql boundSql = mappedStatement.getBoundSql(param);
        String whereExpression = getWhereExpression(boundSql.getSql());
        int whereParamNum = StringUtils.countMatches(whereExpression, "?");
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        Configuration configuration = mappedStatement.getConfiguration();
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        String whereClause = whereExpression;
        Map<String, Object> wps = new HashMap<>(10);

        logger.debug("Audit 0001:" + parameterMappings.size());
        logger.debug("Audit 0002:" + whereParamNum);

        if (parameterMappings != null) {
            // 添加insert语句的审计逻辑
            if (whereParamNum == 0) {
                String majorField = null;
                Object value = null;
                EntityField[] ids = DTOClassInfo.getIdFields(param.getClass());
                majorField = ids[0].getName();
                try {
                    Field f = param.getClass().getDeclaredField(majorField);
                    f.setAccessible(true);
                    value = f.get(param);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                wps.put("_ALIAS", "BASE_TABLE");
                wps.put(majorField, value);
                String field = DTOClassInfo.camelToUnderLine(majorField);
                wps.put("_WHERE_CLAUSE", "BASE_TABLE." + field + "=#{" + majorField + "}");
                return wps;
            }

            for (int i = parameterMappings.size() - whereParamNum; i < parameterMappings.size(); ++i) {
                ParameterMapping parameterMapping = parameterMappings.get(i);

                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    String propertyName = parameterMapping.getProperty();
                    Object value;
                    if (boundSql.hasAdditionalParameter(propertyName)) {
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (param == null) {
                        value = null;
                    } else if (typeHandlerRegistry.hasTypeHandler(param.getClass())) {
                        value = param;
                    } else {
                        MetaObject typeHandler = configuration.newMetaObject(param);
                        value = typeHandler.getValue(propertyName);
                    }
                    if (propertyName.startsWith("dto.")) {
                        propertyName = propertyName.replaceFirst("dto.", "");
                    }
                    wps.put(propertyName, value);
                    whereClause = whereClause.replaceFirst("\\?", "#{" + propertyName + "}");
                }
            }
            String alias = "BASE_TABLE";
            if (isMulti) {
                String[] wheres = whereClause.split(" AND ");
                for (int i = 0, j = wheres.length; i < j; i++) {
                    String fieldName = wheres[i];
                    if (StringUtils.contains(fieldName, ".")) {
                        String[] fieldsStr = fieldName.split("\\.");
                        fieldName = fieldsStr[fieldsStr.length - 1];
                    }
                    wheres[i] = alias + "." + fieldName;
                }
                whereClause = StringUtils.join(wheres, " AND ");
            } else {
                if (StringUtils.contains(whereClause, ".")) {
                    alias = whereClause.substring(0, whereClause.indexOf("."));
                }
            }
            wps.put("_ALIAS", alias);
            wps.put("_WHERE_CLAUSE", whereClause);
        }
        return wps;
    }

    /**
     * 获取where条件.
     *
     * @param sql sql字符串
     * @return where条件字符串
     * @throws JSQLParserException JSQLParserException
     */
    private String getWhereExpression(String sql)
            throws JSQLParserException {
        Statement statement = CCJSqlParserUtil.parse(sql);

        // description: 新增一个判断,如果是INSERT则不返回where条件
        if (statement instanceof Insert) {
            return "";
        }
        Expression whereExpression = null;
        if (statement instanceof Update) {
            Update updateStatement = (Update) statement;
            whereExpression = updateStatement.getWhere();
        } else if (statement instanceof Delete) {
            Delete deleteStatement = (Delete) statement;
            whereExpression = deleteStatement.getWhere();
        }
        if (null == whereExpression) {
            return "";
        }
        return StringUtils.defaultIfBlank(whereExpression.toString(), "");
    }

    /**
     * 获取参数DTO Table注解name值.
     *
     * @param clazz DTO class
     * @return Table注解中name值
     */
    private String getTableName(Class clazz) {
        Table tbl = (Table) clazz.getAnnotation(Table.class);
        if (tbl == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("@Table not found on dto class:" + clazz);
            }
            return null;
        }
        String tableName = tbl.name();
        if (StringUtils.isBlank(tableName)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Can not get tableName of dto class:" + clazz);
            }
            return null;
        }
        return tableName;
    }

    /**
     * 执行审计记录.
     *
     * @param insertMap 添加审计记录参数
     * @param updateMap 修改审计记录参数
     * @throws SQLException SQL执行异常
     */
    private void doDB(Map insertMap, Map updateMap) throws SQLException {
        iAuditService.insertAudit(updateMap, insertMap);
    }

    /**
     * 加载注入的bean
     */
    private void loadService() {
        if (null == beanFactory) {
            beanFactory = ApplicationContextHelper.getApplicationContext();
        }
        if (iAuditService == null) {
            iAuditService = beanFactory.getBean(IAuditService.class);
        }
    }

    public static String generateAndSetAuditSessionId() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static void clearAuditSessionId() {
        LOCAL_AUDIT_SESSION.remove();
    }

}