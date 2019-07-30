/*
 * #{copyright}#
 */

package io.choerodon.hap.job;

import java.util.Date;

import org.quartz.JobExecutionContext;

/**
 * @author liyan.shi@hand-china.com
 */
public class SummaryTestJob extends AbstractJob {

    @Override
    public void safeExecute(JobExecutionContext context) {

        this.setExecutionSummary("Finish at " + new Date());
    }

    @Override
    public boolean isRefireImmediatelyWhenException() {
        return false;
    }

}