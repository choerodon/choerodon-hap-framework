package io.choerodon.hap.code.rule.service.impl;

import io.choerodon.hap.cache.impl.SysCodeRuleCache;
import io.choerodon.hap.code.rule.CodeRuleConstants;
import io.choerodon.hap.code.rule.ICodeRuleResetStrategy;
import io.choerodon.hap.code.rule.dto.CodeRulesLine;
import io.choerodon.hap.code.rule.exception.CodeRuleException;
import io.choerodon.hap.code.rule.service.ICodeRulesLineService;
import io.choerodon.hap.code.rule.service.ISysCodeRuleProcessService;
import io.choerodon.hap.core.AppContextInitListener;
import io.choerodon.hap.core.interceptor.SecurityTokenInterceptor;
import io.choerodon.hap.core.util.FreemarkerUtil;
import io.choerodon.hap.security.TokenUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author xiangyu.qi@hand-china.com on 2017/8/23.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SysCodeRuleProcessService implements ISysCodeRuleProcessService, AppContextInitListener {

    private final Logger logger = LoggerFactory.getLogger(SysCodeRuleProcessService.class);

    @Autowired
    private SysCodeRuleCache ruleCache;


    @Autowired
    private ICodeRulesLineService codeRulesLineService;

    private List<ICodeRuleResetStrategy> strategyList;

    @Override
    public String getRuleCode(String ruleCode) throws CodeRuleException{
        return getRuleCode(ruleCode,null);
    }

    @Override
    public String getRuleCode(String ruleCode, Map<String,String> variables) throws CodeRuleException {

        // 缓存中获取的集合保证按照序号排序的
        List<CodeRulesLine> lineList = ruleCache.getValue(ruleCode);
        if (lineList == null || lineList.isEmpty()) {
            logger.error(CodeRuleException.ERROR_NOT_FOUND);
            throw new CodeRuleException(CodeRuleException.ERROR_NOT_FOUND,"can't find cache by :"+ruleCode);
        }
        StringBuilder result = new StringBuilder();
        Date now = new Date();
        lineList.forEach(line -> {
            line.setRuleCode(ruleCode);
            String fieldVaule = null;
            switch (line.getFiledType()) {
                case CodeRuleConstants.FIELD_TYPE_SEQUENCE:
                    fieldVaule = getSeq(line);
                    break;
                case CodeRuleConstants.FIELD_TYPE_VARIABLE:
                case CodeRuleConstants.FIELD_TYPE_CONSTANT:
                    fieldVaule = line.getFiledValue();
                    break;
                case CodeRuleConstants.FIELD_TYPE_DATE:
                    SimpleDateFormat format = new SimpleDateFormat(line.getDateMask());
                    fieldVaule = format.format(now);
                    break;
                case CodeRuleConstants.FIELD_TYPE_UUID:
                    fieldVaule= UUID.randomUUID().toString();
                    break;
                default:
            }
            result.append(fieldVaule);
        });

        return translateData(result.toString(),variables);
    }


    private String getSeq(CodeRulesLine line) {
        boolean resetFlag = false;
        Date now = new Date();
        //判断重置
        for(ICodeRuleResetStrategy strategy: strategyList){
            resetFlag = strategy.isResetSequence(line.getResetFrequency(),line.getResetDate(),now);
            if(resetFlag){
                ruleCache.reset(line);
                break;
            }
        }
        Long incrSeq = ruleCache.incr(line);
        Long incrNum = incrSeq - line.getStartValue();
        Long stepNumber = line.getStepNumber();
        if (stepNumber == null || stepNumber <= 0) {
            stepNumber = CodeRuleConstants.INCR_NUMBER;
        }
        if (!resetFlag && incrNum > 0 && incrNum % stepNumber == 0) {
            if (SecurityTokenInterceptor.LOCAL_SECURITY_KEY.get() != null) {
                TokenUtils.generateAndSetToken(SecurityTokenInterceptor.LOCAL_SECURITY_KEY.get(), line);
            }
            line.setCurrentValue(incrSeq + stepNumber);
            codeRulesLineService.updateRecord(line);
            ruleCache.updateLine(line);
        }
        int seqLength = String.valueOf(incrSeq).length();
        if (seqLength > line.getSeqLength()) {
            ruleCache.decr(line);
            logger.error(CodeRuleException.ERROR_SEQ_LENGTH_TO_LONG);
            throw new RuntimeException(new CodeRuleException(CodeRuleException.ERROR_SEQ_LENGTH_TO_LONG,"ruleCode:"+line.getRuleCode()));
        } else {
            String format = "%0" + line.getSeqLength() + "d";
            return String.format(format, incrSeq);
        }
    }

    /**
     * 转换模板数据[freemarker].
     *
     * @param content
     * @param data
     * @return
     * @throws Exception
     */
    private String translateData(String content, Map data) throws CodeRuleException {
        if (content == null) {
            return "";
        }
        try (StringWriter out = new StringWriter()) {
            Configuration config = FreemarkerUtil.getConfiguration();
            Template template = new Template(null, content, config);
            template.process(data, out);
            return out.toString();
        }catch (Exception e) {
            throw new CodeRuleException(e.getMessage(),CodeRuleException.ERROR_GET_VARIBLE_FAILUE);
        }
    }


    @Override
    public void contextInitialized(ApplicationContext applicationContext) {
        strategyList = new ArrayList<>(applicationContext.getBeansOfType(ICodeRuleResetStrategy.class).values());
    }
}
