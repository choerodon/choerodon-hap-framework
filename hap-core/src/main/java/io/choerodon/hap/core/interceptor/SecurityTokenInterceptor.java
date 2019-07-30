/*
 * #{copyright}#
 */
package io.choerodon.hap.core.interceptor;

import io.choerodon.hap.security.TokenUtils;
import io.choerodon.mybatis.entity.BaseDTO;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Properties;

/**
 * 防篡改Token支持.
 *
 * @author shengyang.zhou@hand-china.com
 */
@Order(9)
@Component
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class,
                RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class,
                RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class SecurityTokenInterceptor implements Interceptor {

    public static final ThreadLocal<String> LOCAL_SECURITY_KEY = new ThreadLocal<>();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object result = invocation.proceed();

        String securityKey = LOCAL_SECURITY_KEY.get();
        if (securityKey == null) {
            return result;
        }

        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        if (mappedStatement.getSqlCommandType() == SqlCommandType.SELECT) {
            if (result instanceof List) {
                for (Object o : (List) result) {
                    if (o instanceof BaseDTO) {
                        TokenUtils.generateAndSetToken(securityKey, (BaseDTO) o);
                    }
                }

                // report count result of _COUNT sql
                // this code should not be written here, but i don't want to create a new Class
                if (((List) result).size() == 1 && mappedStatement.getId().endsWith("_COUNT")) {
                    Logger logger = LoggerFactory.getLogger(mappedStatement.getId());
                    if (logger.isDebugEnabled()) {
                        logger.debug("count: " + ((List) result).get(0));
                    }
                }
            }
        } else if (mappedStatement.getSqlCommandType() == SqlCommandType.INSERT) {
            Object domain = invocation.getArgs()[1];
            if (domain instanceof BaseDTO) {
                TokenUtils.generateAndSetToken(securityKey, (BaseDTO) domain);
            }
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

}
