package io.choerodon.hap.cache.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.hap.api.gateway.dto.ApiInterface;
import io.choerodon.hap.api.gateway.dto.ApiServer;
import io.choerodon.hap.api.gateway.mapper.ApiInterfaceMapper;
import io.choerodon.hap.api.gateway.mapper.ApiServerMapper;
import io.choerodon.base.util.BaseConstants;
import io.choerodon.redis.impl.RedisCache;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author peng.jiang@hand-china.com
 * @since 2017/9/26.
 */
@Component(value = "serverCache")
public class ApiServerCache extends RedisCache<ApiServer> implements BaseConstants {

    private static final Logger logger = LoggerFactory.getLogger(ApiServerCache.class);

    private static final String REDIS_CATALOG = BaseConstants.HAP_CACHE + "api_config:";

    private static final String CACHE_HEAD = "Head:";

    private String getServerAndInterface = ApiServerMapper.class.getName() + ".getServerAndInterfaceByServerId";

    private String serverSelectAll = ApiServerMapper.class.getName() + ".selectAll";

    private String interfaceSelectAll = ApiInterfaceMapper.class.getName() + ".selectAll";

    private ObjectMapper objectMapper;

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    {
        setLoadOnStartUp(true);
        setType(ApiServer.class);
        setName("server");
    }

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    public ApiServer getValue(String serverUrl, String interfaceUrl) {

        String key = REDIS_CATALOG + serverUrl;
        String headKey = CACHE_HEAD + serverUrl;
        ApiServer server = getRedisTemplate().execute((RedisCallback<ApiServer>) (connection) -> {
            Object serverObject = hMGet(connection, key, headKey, ApiServer.class);
            if (serverObject != null) {
                ApiServer s = (ApiServer) serverObject;
                Object interfaceObject = hMGet(connection, key, interfaceUrl, ApiInterface.class);
                if (interfaceObject != null) {
                    ApiInterface i = (ApiInterface) interfaceObject;
                    s.setApiInterface(i);
                    return s;
                }
            }
            return null;
        });
        return server;
    }

    public void reload(Object o) {
        logger.info("reload cache serverId:{}", o);
        try (SqlSession sqlSession = getSqlSessionFactory().openSession()) {
            ApiServer server = sqlSession.selectOne(getServerAndInterface, o);
            if (server != null) {
                setValue(server);
            }
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("reload server cache error:", e);
            }
        }
    }

    public void setValue(ApiServer server) {
        List<ApiInterface> interfaces = server.getInterfaces();
        String key = REDIS_CATALOG + server.getMappingUrl();

        getRedisTemplate().execute((RedisCallback<ApiServer>) (connection) -> {
            //设置头缓存
            String headKey = CACHE_HEAD + server.getMappingUrl();
            server.setInterfaces(null);
            String serverString = objectToString(server);
            Map<byte[], byte[]> serverMap = new HashMap<>();
            serverMap.put(strSerializer.serialize(headKey), strSerializer.serialize(serverString));
            connection.hMSet(strSerializer.serialize(key), serverMap);

            //设置行缓存
            if (interfaces != null && interfaces.size() > 0) {
                Map<byte[], byte[]> interfaceMap = new HashMap<>();
                for (ApiInterface srInterface : interfaces) {
                    String interfaceKey = srInterface.getMappingUrl();
                    String interfaceString = objectToString(srInterface);
                    interfaceMap.put(strSerializer.serialize(interfaceKey), strSerializer.serialize(interfaceString));
                }
                connection.hMSet(strSerializer.serialize(key), interfaceMap);
            }
            return null;
        });
    }

    @Override
    public void initLoad() {

        Map<Long, ApiServer> serverMap = new HashMap<>();
        try (SqlSession sqlSession = getSqlSessionFactory().openSession()) {
            sqlSession.select(serverSelectAll, (resultContext) -> {
                ApiServer server = (ApiServer) resultContext.getResultObject();
                serverMap.put(server.getServerId(), server);
            });
            sqlSession.select(interfaceSelectAll, (resultContext) -> {
                ApiInterface item = (ApiInterface) resultContext.getResultObject();
                ApiServer server = serverMap.get(item.getServerId());
                if (server != null) {
                    List<ApiInterface> interfaces = server.getInterfaces();
                    if (interfaces == null) {
                        interfaces = new ArrayList<>();
                        server.setInterfaces(interfaces);
                    }
                    interfaces.add(item);
                }
            });
            serverMap.forEach((k, v) -> {
                setValue(v);
            });
            serverMap.clear();
        } catch (Throwable e) {
            if (logger.isErrorEnabled()) {
                logger.error("init lov cache error:", e);
            }
        }
    }

    public void remove(ApiServer server) {
        String key = REDIS_CATALOG + server.getMappingUrl();
        getRedisTemplate().execute((RedisCallback<ApiServer>) (connection) -> {
            byte[] keyBytes = strSerializer.serialize(key);
            connection.del(keyBytes);
            return null;
        });
    }

    public void removeInterface(ApiServer server, List<ApiInterface> list) {
        String groupKey = REDIS_CATALOG + server.getMappingUrl();
        list.forEach(apiInterface -> {
            String key = apiInterface.getMappingUrl();
            getRedisTemplate().execute((RedisCallback<ApiServer>) (connection) -> {
                byte[] groupKeyBytes = strSerializer.serialize(groupKey);
                byte[] keyBytes = strSerializer.serialize(key);
                connection.hDel(groupKeyBytes, keyBytes);
                return null;
            });
        });

    }

    protected String objectToString(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            if (logger.isWarnEnabled()) {
                logger.warn("invalid json: " + value);
            }
            throw new RuntimeException(e);
        }
    }

    protected Object stringToObject(String value, Class type) {
        try {
            return objectMapper.readValue(value, type);
        } catch (Exception e) {
            if (logger.isWarnEnabled()) {
                logger.warn("invalid value: " + value);
            }
            throw new RuntimeException(e);
        }
    }

    protected Object hMGet(RedisConnection connection, String mapKey, String pName, Class type) {
        byte[] mapKeyBytes = strSerializer.serialize(mapKey);
        List<byte[]> result = connection.hMGet(mapKeyBytes, strSerializer.serialize(pName));
        if (result.isEmpty() || result.get(0) == null) {
            return null;
        }
        String string = strSerializer.deserialize(result.get(0));
        Object obj = stringToObject(string, type);
        return obj;
    }

}
