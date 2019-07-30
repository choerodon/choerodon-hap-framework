package io.choerodon.hap.code.rule.components;

import io.choerodon.hap.code.rule.CodeRuleConstants;
import io.choerodon.hap.code.rule.ICodeRuleResetStrategy;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author xiangyu.qi@hand-china.com on 2017/10/30.
 */
@Component
public class DefaultCodeRuleRestStategy implements ICodeRuleResetStrategy {

    @Override
    public boolean isResetSequence(String resetType, Date lastRestDate,Date now) {
        if (!CodeRuleConstants.RESET_TYPE_NEVER.equalsIgnoreCase(resetType) && lastRestDate == null) {
            return true;
        } else if (CodeRuleConstants.RESET_TYPE_YEAR.equalsIgnoreCase(resetType)) {
            SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
            if (!yearFormat.format(now).equalsIgnoreCase(yearFormat.format(lastRestDate))) {
                return true;
            }
        } else if (CodeRuleConstants.RESET_TYPE_QUARTER.equalsIgnoreCase(resetType)) {
            Calendar resetDate = Calendar.getInstance();
            resetDate.setTime(lastRestDate);
            Calendar nowDate = Calendar.getInstance();
            nowDate.setTime(now);
            if (resetDate.get(Calendar.YEAR) != nowDate.get(Calendar.YEAR)
                    || (resetDate.get(Calendar.MONTH) / 3 != nowDate.get(Calendar.MONTH) / 3)) {
                return true;
            }

        } else if (CodeRuleConstants.RESET_TYPE_MONTH.equalsIgnoreCase(resetType)) {
            SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
            if(!monthFormat.format(now).equalsIgnoreCase(monthFormat.format(lastRestDate))){
                return true;
            }
        }
        return false;
    }
}
