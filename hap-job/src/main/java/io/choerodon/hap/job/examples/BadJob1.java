/*
 * #{copyright}#
 */

package io.choerodon.hap.job.examples;

import java.util.Date;

import io.choerodon.hap.job.AbstractJob;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 有状态的job举例.
 * <p>
 * A job dumb job that will throw a job execution exception.
 * </p>
 * 
 * @author shiliyan
 *
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class BadJob1 extends AbstractJob {

    // Logging
    private static Logger log = LoggerFactory.getLogger(BadJob1.class);
    private int calculation;

    public BadJob1() {
    }

    @Override
    public void safeExecute(JobExecutionContext context) throws Exception {
        JobKey jobKey = context.getJobDetail().getKey();
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();

        int denominator = dataMap.getInt("denominator");
        log.info("---" + jobKey + " executing at " + new Date() + " with denominator " + denominator);

        try {
            calculation = 4815 / denominator;
        } catch (Exception e) {

            dataMap.put("denominator", "1");

            throw e;
        }

        log.info("---" + jobKey + " completed at " + new Date());
    }

    @Override
    public boolean isRefireImmediatelyWhenException() {
        return true;
    }

}
