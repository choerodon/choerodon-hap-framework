package io.choerodon.hap.code.rule;


import java.util.Date;

/**
 * @author xiangyu.qi@hand-china.com on 2017/10/30.
 */
public interface ICodeRuleResetStrategy {

    /**
     * @param resetType 重置周期，每年，每月。。。
     * @param lastRestDate  上次重置日期
     * @param now 当前日期
     * @return 是否满足重置条件
     */
    boolean isResetSequence (String resetType, Date lastRestDate,Date now);

}
