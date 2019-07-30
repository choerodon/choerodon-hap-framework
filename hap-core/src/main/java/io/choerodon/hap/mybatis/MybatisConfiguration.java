package io.choerodon.hap.mybatis;

import io.choerodon.hap.core.interceptor.AuditInterceptor;
import io.choerodon.hap.core.interceptor.CacheJoinInterceptor;
import io.choerodon.hap.core.interceptor.RequestContextInterceptor;
import io.choerodon.hap.core.interceptor.SecurityTokenInterceptor;
import io.choerodon.redis.CacheResolve;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Configuration
@AutoConfigureAfter(MybatisAutoConfiguration.class)
public class MybatisConfiguration {
    @Autowired
    @Qualifier("cacheJoinType")
    private Map<String, CacheResolve> cacheJoinType;

    @Autowired
    private List<SqlSessionFactory> sqlSessionFactories;

    @PostConstruct
    public void addAuditInterceptor(){
        for (SqlSessionFactory sqlSessionFactory : sqlSessionFactories){
            CacheJoinInterceptor cacheJoinInterceptor = new CacheJoinInterceptor();
            cacheJoinInterceptor.setCacheJoinType(cacheJoinType);
            sqlSessionFactory.getConfiguration().addInterceptor(cacheJoinInterceptor);
            sqlSessionFactory.getConfiguration().addInterceptor(new RequestContextInterceptor());
            sqlSessionFactory.getConfiguration().addInterceptor(new SecurityTokenInterceptor());
            sqlSessionFactory.getConfiguration().addInterceptor(new AuditInterceptor());
        }
    }

}
