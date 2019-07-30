package io.choerodon.hap.core.interceptor;

import io.choerodon.web.core.IRequest;
import io.choerodon.web.core.impl.RequestHelper;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.RowBounds;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.sql.Statement;
import java.util.Properties;

/**
 * 自动绑定 IRequest 参数到 BoundSql.
 * <p>
 * 拦截点:
 * <ul>
 * <li>增删改查时的参数绑定</li>
 * <li>查询时的,cacheKey获取</li>
 * </ul>
 *
 * @author shengyang.zhou@hand-china.com
 */
@Order(8)
@Component
@Intercepts({
        @Signature(type = Executor.class, method = "createCacheKey", args = {MappedStatement.class, Object.class, RowBounds.class, BoundSql.class}),
        @Signature(type = StatementHandler.class, method = "parameterize", args = {Statement.class})})
public class RequestContextInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object target = invocation.getTarget();
        IRequest request = RequestHelper.getCurrentRequest(true);
        BoundSql boundSql = null;
        if (target instanceof StatementHandler) {
            StatementHandler handler = (StatementHandler) target;
            boundSql = handler.getBoundSql();
        } else if (target instanceof Executor) {
            boundSql = (BoundSql) invocation.getArgs()[3];
        }
        if (boundSql != null){
            boundSql.setAdditionalParameter("request", request);
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}