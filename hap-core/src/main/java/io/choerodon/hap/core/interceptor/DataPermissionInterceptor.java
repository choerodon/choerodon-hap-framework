package io.choerodon.hap.core.interceptor;


import io.choerodon.hap.security.permission.DataPermissionService;
import io.choerodon.hap.security.permission.service.impl.DataPermissionCacheContainer;
import io.choerodon.base.helper.ApplicationContextHelper;
import io.choerodon.web.core.impl.RequestHelper;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * 数据屏蔽拦截器
 */
@Order(10)
@Component
@Intercepts(
        {
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        }
)
public class DataPermissionInterceptor implements Interceptor {

    @Autowired
    private DataPermissionService dataPermissionService;

    private DataPermissionCacheContainer dataPermissionCacheContainer;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        Object parameter = args[1];
        RowBounds rowBounds = (RowBounds) args[2];
        ResultHandler resultHandler = (ResultHandler) args[3];
        Executor executor = (Executor) invocation.getTarget();
        CacheKey cacheKey;
        BoundSql boundSql;
        //由于逻辑关系，只会进入一次
        if(args.length == 4){
            //4 个参数时
            boundSql = ms.getBoundSql(parameter);
            cacheKey = executor.createCacheKey(ms, parameter, rowBounds, boundSql);
        } else {
            //6 个参数时
            cacheKey = (CacheKey) args[4];
            boundSql = (BoundSql) args[5];
        }
        MetaObject metaObject = SystemMetaObject.forObject(boundSql);
        String oldSql = (String) metaObject.getValue("sql");
        if (dataPermissionCacheContainer == null){
            ApplicationContext context = ApplicationContextHelper.getApplicationContext();
            if (context != null){
                dataPermissionCacheContainer = context.getBean(DataPermissionCacheContainer.class);
            }
        }
        if(dataPermissionCacheContainer != null && dataPermissionCacheContainer.needPermission(oldSql)){
            metaObject.setValue("sql", dataPermissionService.getNewSql(oldSql, RequestHelper.getCurrentRequest()));
        }
        return executor.query(ms, parameter, rowBounds, resultHandler, cacheKey, boundSql);
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

}