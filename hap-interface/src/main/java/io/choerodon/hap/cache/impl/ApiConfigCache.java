package io.choerodon.hap.cache.impl;

import io.choerodon.hap.intergration.dto.HapInterfaceHeader;
import io.choerodon.hap.intergration.mapper.HapInterfaceHeaderMapper;
import io.choerodon.hap.intergration.mapper.HapInterfaceLineMapper;
import io.choerodon.redis.impl.HashStringRedisCache;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * @author qiang.zeng
 * @since 2016/8/1
 */
@Component(value = "apiCache")
public class ApiConfigCache<T> extends HashStringRedisCache<HapInterfaceHeader> {

    private static final Logger logger = LoggerFactory.getLogger(ApiConfigCache.class);
    private String apiSql = HapInterfaceHeaderMapper.class.getName() + ".getAllHeaderAndLine";
    private String lineSql = HapInterfaceLineMapper.class.getName() + ".getHeaderLineByLineId";

    {
        setLoadOnStartUp(true);
        setType(HapInterfaceHeader.class);
        setName("api");
    }

    @Override
    public HapInterfaceHeader getValue(String key) {
        return super.getValue(key);
    }

    @Override
    public void setValue(String key, HapInterfaceHeader headerAndLineDTO) {
        super.setValue(key, headerAndLineDTO);
    }


    public void initLoad() {

        try (SqlSession sqlSession = getSqlSessionFactory().openSession()) {
            sqlSession.select(apiSql, (resultContext) -> {
                HapInterfaceHeader headerAndLineDTO = (HapInterfaceHeader) resultContext.getResultObject();
                logger.info("cache result:{}", headerAndLineDTO.getInterfaceCode() + headerAndLineDTO.getLineCode());
                setValue(headerAndLineDTO.getInterfaceCode() + HapInterfaceHeader.CACHE_SEPARATOR + headerAndLineDTO.getLineCode(), headerAndLineDTO);
            });

        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("init api cache error:", e);
            }
        }

    }


    public void reload(Object lineId) {
        logger.info("test  lineId:{}", lineId);
        try (SqlSession sqlSession = getSqlSessionFactory().openSession()) {
            HapInterfaceHeader headerAndLineDTO = sqlSession.selectOne(lineSql, lineId);
            if (headerAndLineDTO != null)
                setValue(headerAndLineDTO.getInterfaceCode() + HapInterfaceHeader.CACHE_SEPARATOR + headerAndLineDTO.getLineCode(), headerAndLineDTO);

        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("reload api cache error:", e);
            }
        }


    }


}
