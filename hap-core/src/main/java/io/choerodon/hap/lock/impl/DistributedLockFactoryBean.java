package io.choerodon.hap.lock.impl;

import io.choerodon.hap.lock.DistributedLockProvider;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import tk.mybatis.mapper.util.StringUtil;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 创建分布式锁模板实例的工厂Bean.
 *
 * @author qixiangyu
 */
@Component
public class DistributedLockFactoryBean implements FactoryBean<DistributedLockProvider> {
    private static final Logger logger = LoggerFactory.getLogger(DistributedLockFactoryBean.class);

    private LockInstanceMode mode = LockInstanceMode.MULTIPLE;

    private DistributedLockProvider distributedLockProvider;
    private List<RedissonClient> redissons;

    @PostConstruct
    public void init() {
        logger.debug("init template");
        Properties pro = new Properties();
        InputStream inputStream = null;
        Config config = null;
        List<String> urls = null;
        List<String> passwords = null;
        try {
            inputStream = DistributedLockFactoryBean.class.getClassLoader().getResourceAsStream("hap-default-config.properties");
            pro.load(inputStream);
            urls = getArray(pro.getProperty("redisson.server.url"));
            passwords = getArray(pro.getProperty("redissson.server.password"));
        } catch (IOException e) {
            logger.error("load hap-default-config.properties failure", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.error("", e);
                }
            }
        }
        redissons = new ArrayList<>();
        for (int i = 0; i < urls.size(); i++) {
            String url = urls.get(i);
            String password = null;
            if (passwords != null && passwords.size() > i) {
                password = passwords.get(i);
            }
            config = new Config();
            if (StringUtil.isNotEmpty(password)) {
                config.useSingleServer().setAddress(url).setPassword(password);
            } else {
                config.useSingleServer().setAddress(url);
            }
            Assert.notNull(config, "config can not be null");
            redissons.add(Redisson.create(config));
        }
    }

    @PreDestroy
    public void destroy() {
        logger.debug("destory template");
        for (RedissonClient redisson : redissons) {
            redisson.shutdown();
        }
    }

    @Override
    public DistributedLockProvider getObject() throws Exception {
        switch (mode) {
        case MULTIPLE:
            if (redissons != null && redissons.size() > 0) {
                distributedLockProvider = new MultipleDistributedLockProvider(redissons);
            }
            break;
        }
        return distributedLockProvider;
    }

    @Override
    public Class<?> getObjectType() {
        return DistributedLockProvider.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public void setMode(String mode) {
        if (StringUtils.isBlank(mode)) {
            throw new IllegalArgumentException("Can't Find mode");
        }
        this.mode = LockInstanceMode.parse(mode);
        if (this.mode == null) {
            throw new IllegalArgumentException("Not Support Mode");
        }
    }

    private enum LockInstanceMode {
        MULTIPLE;

        public static LockInstanceMode parse(String name) {
            for (LockInstanceMode modeIns : LockInstanceMode.values()) {
                if (modeIns.name().equals(name.toUpperCase())) {
                    return modeIns;
                }
            }
            return null;
        }
    }

    private List<String> getArray(String str) {
        List<String> results = new ArrayList<>();
        if (StringUtil.isNotEmpty(str)) {
            String[] strArray = str.split(",");
            for (String s : strArray) {
                if (StringUtil.isNotEmpty(s)) {
                    results.add(s);
                }
            }
        }
        return results;
    }
}
