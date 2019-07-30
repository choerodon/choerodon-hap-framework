package io.choerodon.hap.cache.impl;

import io.choerodon.hap.security.permission.dto.DataPermissionRule;
import io.choerodon.hap.security.permission.dto.DataPermissionRuleAssign;
import io.choerodon.hap.security.permission.dto.DataPermissionRuleDetail;
import io.choerodon.hap.security.permission.mapper.DataPermissionRuleAssignMapper;
import io.choerodon.hap.security.permission.mapper.DataPermissionRuleDetailMapper;
import io.choerodon.hap.security.permission.mapper.DataPermissionRuleMapper;
import io.choerodon.redis.impl.HashStringRedisCache;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @Author jialong.zuo@hand-china.com on 2017/9/11.
 */
@Component(value = "dataPermissionRuleCache")
public class DataPermissionRuleCache extends HashStringRedisCache<Map> {

    private String ruleSql = DataPermissionRuleMapper.class.getName() + ".selectAll";
    private String ruleDetailSql = DataPermissionRuleDetailMapper.class.getName() + ".selectAll";
    private String ruleAssignSql = DataPermissionRuleAssignMapper.class.getName() + ".selectAll";
    private Logger logger = LoggerFactory.getLogger(DataPermissionRuleCache.class);

    {
        setLoadOnStartUp(true);
        setType(Map.class);
        setName("data_permission_rule");
    }


    @Override
    public Map getValue(String key) {
        return super.getValue(key);
    }


    @Override
    public void setValue(String key, Map dataMaskRule) {
        super.setValue(key, dataMaskRule);
    }


    protected void initLoad() {
        Map<String, Map> ruleMaps = new HashMap<>();
        Map<String, DataPermissionRule> tempDataPermissionRule = new HashMap<>();
        Map<String, DataPermissionRuleDetail> tempDataPermissionRuleDetail = new HashMap<>();
        Map<String, DataPermissionRuleAssign> tempDataPermissionRuleAssign = new HashMap<>();

        try (SqlSession sqlSession = getSqlSessionFactory().openSession()) {
            sqlSession.select(ruleSql, (resultContext) -> {
                DataPermissionRule dataMaskRuleManage = (DataPermissionRule) resultContext.getResultObject();
                tempDataPermissionRule.put(dataMaskRuleManage.getRuleId().toString(), dataMaskRuleManage);
            });

            sqlSession.select(ruleDetailSql, (resultContext) -> {
                DataPermissionRuleDetail dataMaskRuleManageDetail = (DataPermissionRuleDetail) resultContext.getResultObject();
                tempDataPermissionRuleDetail.put(dataMaskRuleManageDetail.getDetailId().toString(), dataMaskRuleManageDetail);
            });

            sqlSession.select(ruleAssignSql, (resultContext) -> {
                DataPermissionRuleAssign dataMaskRuleAssign = (DataPermissionRuleAssign) resultContext.getResultObject();
                tempDataPermissionRuleAssign.put(dataMaskRuleAssign.getAssignId().toString(), dataMaskRuleAssign);
            });
        } catch (Throwable e) {
            if (logger.isErrorEnabled()) {
                logger.error("init lov cache error:", e);
            }
        }

        doCreateCacheMap(tempDataPermissionRule, tempDataPermissionRuleDetail, tempDataPermissionRuleAssign, ruleMaps);

        ruleMaps.forEach((k, v) -> {
            setValue(k, v);
        });
    }


    public void doCreateCacheMap(Map<String, DataPermissionRule> tempDataMaskRuleManage,
                                 Map<String, DataPermissionRuleDetail> tempDataMaskRuleManageDetail,
                                 Map<String, DataPermissionRuleAssign> tempDataMaskRuleAssign, Map<String, Map> ruleMaps) {
        if (tempDataMaskRuleAssign.size() == 0) {
            tempDataMaskRuleManage.forEach((k, v) -> {
                ruleMaps.put(v.getRuleId().toString(), new HashMap());
            });
            return;
        }
        tempDataMaskRuleAssign.forEach((k, v) -> {

            DataPermissionRuleDetail detail = tempDataMaskRuleManageDetail.get(v.getDetailId().toString());
            DataPermissionRule rule = tempDataMaskRuleManage.get(detail.getRuleId().toString());

            Map<String, Map> ruleDetail = Optional.ofNullable(ruleMaps.get(rule.getRuleId().toString())).orElseGet(() -> {
                Map map = new HashMap();
                ruleMaps.put(rule.getRuleId().toString(), map);
                return map;
            });

            Map<String, Set> rangeMask = Optional.ofNullable(ruleDetail.get(v.getAssignField().toString())).orElseGet(() -> {
                Map map = new HashMap();
                ruleDetail.put(v.getAssignField(), map);
                return map;
            });

            Set<String> ruleAssign = Optional.ofNullable(rangeMask.get(v.getAssignFieldValue().toString())).orElseGet(() -> {
                Set set = new HashSet();
                rangeMask.put(v.getAssignFieldValue(), set);
                return set;
            });

            if (null != detail.getPermissionFieldSqlValue()) {
                ruleAssign.add(detail.getPermissionFieldSqlValue());
            } else if (null != detail.getPermissionFieldValue()) {
                ruleAssign.add(detail.getPermissionFieldValue().toString());
            }

        });
    }

}
