package io.choerodon.hap.cache.impl;

import io.choerodon.hap.code.rule.CodeRuleConstants;
import io.choerodon.hap.code.rule.dto.CodeRulesHeader;
import io.choerodon.hap.code.rule.dto.CodeRulesLine;
import io.choerodon.hap.code.rule.mapper.CodeRulesHeaderMapper;
import io.choerodon.hap.code.rule.mapper.CodeRulesLineMapper;
import io.choerodon.base.util.BaseConstants;
import io.choerodon.redis.impl.HashStringRedisCache;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author xiangyu.qi@hand-china.com on 2017/8/23.
 */
@Component(value = "sysCodeRuleCache")
public class SysCodeRuleCache extends HashStringRedisCache<List<CodeRulesLine>> {

    private final static String SEQ_CATEGORY = "seq:";

    private final Logger logger = LoggerFactory.getLogger(SysCodeRuleCache.class);

    private String allEnableRuleSqlid = CodeRulesHeaderMapper.class.getName() + ".select";

    private String codeRuleLineSqlId = CodeRulesLineMapper.class.getName() + ".select";

    @Autowired
    private CodeRulesLineMapper lineMapper;

    {
        //setType(List.class);
        setLoadOnStartUp(true);
        setName("code_rule");
    }

    public void removeSeq(String key) {
        //删除序列缓存
        getRedisTemplate().execute((RedisCallback<Object>) (connection) -> {
            return connection.del(strSerializer.serialize(getSeqFullKey(key)));
        });
    }

    public void reload(CodeRulesHeader header) {
        CodeRulesLine lineCondition = new CodeRulesLine();
        lineCondition.setHeaderId(header.getHeaderId());
        try (SqlSession sqlSession = getSqlSessionFactory().openSession()) {
            List<CodeRulesLine> lines = sqlSession.selectList(codeRuleLineSqlId, lineCondition);
            //根据序号排序
            lines.sort((a, b) -> a.getFieldSequence().compareTo(b.getFieldSequence()));
            setValue(header.getRuleCode(), lines);
            lines.forEach(line -> {
                //处理序列情况
                if (CodeRuleConstants.FIELD_TYPE_SEQUENCE.equalsIgnoreCase(line.getFiledType())) {
                    line.setRuleCode(header.getRuleCode());
                    byte[] fullSeqKey = strSerializer.serialize(getSeqFullKey(header.getRuleCode()));
                    loadSeq(line, fullSeqKey);
                }
            });
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("reolad code rule cache exception: ", e);
            }
        }
    }


    public void updateLine(CodeRulesLine line) {
        List<CodeRulesLine> lines = getValue(line.getRuleCode());
        int index = 0;
        for (CodeRulesLine t : lines) {
            if (t.getLineId().equals(line.getLineId())) {
                lines.set(index, line);
            }
            index++;
        }
        setValue(line.getRuleCode(), lines);
    }

    public Long incr(CodeRulesLine line) {
        byte[] fullSeqKey = strSerializer.serialize(getSeqFullKey(line.getRuleCode()));
        loadSeq(line, fullSeqKey);
        return (Long) getRedisTemplate().execute((RedisCallback<Object>) (connection) -> {
            return connection.incr(fullSeqKey);
        });
    }

    public Long decr(CodeRulesLine line) {
        byte[] fullSeqKey = strSerializer.serialize(getSeqFullKey(line.getRuleCode()));
        //loadSeq(line,fullSeqKey);
        return (Long) getRedisTemplate().execute((RedisCallback<Object>) (connection) -> {
            return connection.decr(fullSeqKey);
        });
    }

    public void reset(CodeRulesLine line) {
        //高并发情况，避免重复重置，再次检查是否需要重置（重新从缓存中拿到当前序列信息，检查重置日期是否已经被更新）
        synchronized (line.getRuleCode().intern()) {
            boolean isRest = true;
            List<CodeRulesLine> lineList = getValue(line.getRuleCode());
            for (CodeRulesLine rulesLine : lineList) {
                if (CodeRuleConstants.FIELD_TYPE_SEQUENCE.equals(rulesLine.getFiledType())) {
                    if (rulesLine.getResetDate() == null && line.getResetDate() == null) {
                        isRest = false;
                    } else if (rulesLine.getResetDate() != null && line.getResetDate() != null && rulesLine.getResetDate().equals(line.getResetDate())) {
                        isRest = false;
                    }
                }
            }
            if (isRest) {
                return;
            }
            Long resetValue = line.getStartValue() - 1;
            byte[] fullSeqKey = strSerializer.serialize(getSeqFullKey(line.getRuleCode()));
            byte[] seqValue = strSerializer.serialize((resetValue.toString()));
            getRedisTemplate().execute((RedisCallback<Object>) (connection) -> {
                connection.set(fullSeqKey, seqValue);
                return null;
            });
            line.setCurrentValue(resetValue + CodeRuleConstants.INCR_NUMBER);
            line.setResetDate(new Date());
            lineMapper.updateByPrimaryKeySelective(line);
            updateLine(line);
        }
    }


    @SuppressWarnings("unchecked")
    protected void initLoad() {
        try (SqlSession sqlSession = getSqlSessionFactory().openSession()) {
            CodeRulesHeader condition = new CodeRulesHeader();
            condition.setEnableFlag(BaseConstants.YES);
            //获取所有启用的编码规则
            List<CodeRulesHeader> enableRules = sqlSession.selectList(allEnableRuleSqlid, condition);
            CodeRulesLine lineCondition = new CodeRulesLine();
            if (enableRules == null) {
                return;
            }
            enableRules.forEach(t -> {
                lineCondition.setHeaderId(t.getHeaderId());
                List<CodeRulesLine> lines = sqlSession.selectList(codeRuleLineSqlId, lineCondition);
                //根据序号排序
                lines.sort((a, b) -> a.getFieldSequence().compareTo(b.getFieldSequence()));
                setValue(t.getRuleCode(), lines);
                lines.forEach(line -> {
                    //处理序列情况
                    if (CodeRuleConstants.FIELD_TYPE_SEQUENCE.equalsIgnoreCase(line.getFiledType())) {
                        line.setRuleCode(t.getRuleCode());
                        byte[] fullSeqKey = strSerializer.serialize(getSeqFullKey(t.getRuleCode()));
                        loadSeq(line, fullSeqKey);
                    }
                });
            });

        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("init code rule cache exception: ", e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<CodeRulesLine> stringToObject(String value) {
        try {
            return getObjectMapper().readValue(value, getObjectMapper().getTypeFactory().constructParametricType(
                    ArrayList.class, CodeRulesLine.class));
        } catch (Exception e) {
            if (logger.isWarnEnabled()) {
                logger.warn("invalid value: " + value);
            }
            throw new RuntimeException(e);
        }
    }


    protected String getSeqFullKey(String key) {
        return new StringBuilder(getCategory()).append(":").append(getName()).append(":").append(key).toString();
    }


    private void loadSeq(CodeRulesLine line, byte[] fullSeqKey) {
        boolean exists = (boolean) getRedisTemplate().execute((RedisCallback<Object>) (connection) -> {
            return connection.exists(fullSeqKey);
        });
        if (!exists) {
            //不存在则加载seq到缓存
            if (line.getCurrentValue() == null) {
                line.setCurrentValue(line.getStartValue() - 1);
            }
            getRedisTemplate().execute((RedisCallback<Object>) (connection) -> {
                connection.set(fullSeqKey, strSerializer.serialize(line.getCurrentValue().toString()));
                return null;
            });
            Long stepNumber = line.getStepNumber();
            if (stepNumber == null || stepNumber <= 0) {
                stepNumber = CodeRuleConstants.INCR_NUMBER;
            }
            line.setCurrentValue(line.getCurrentValue() + stepNumber);
            //sqlSession.update(updateRuleSeqLineSqlId,line);
            lineMapper.updateByPrimaryKeySelective(line);
            updateLine(line);
        }
    }

}
