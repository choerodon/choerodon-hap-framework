/*
 * #{copyright}#
 */

package io.choerodon.hap.job.examples;

import java.util.Date;

import io.choerodon.hap.job.AbstractJob;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * A dumb implementation of Job, for unit testing purposes.
 * </p>
 * 
 * @author shiliyan
 *
 */
public class SimpleRecoveryJob extends AbstractJob {

    private static Logger log = LoggerFactory.getLogger(SimpleRecoveryJob.class);

    private static final String COUNT = "count";

    /**
     * Quartz requires a public empty constructor so that the scheduler can
     * instantiate the class whenever it needs.
     */
    public SimpleRecoveryJob() {
    }

    @Override
    public void safeExecute(JobExecutionContext context) throws Exception {

        JobKey jobKey = context.getJobDetail().getKey();

        // if the job is recovering print a message
        if (context.isRecovering()) {
            log.info("SimpleRecoveryJob: " + jobKey + " RECOVERING at " + new Date());
        } else {
            log.info("SimpleRecoveryJob: " + jobKey + " starting at " + new Date());
        }

        // delay for ten seconds
        long delay = 10L * 1000L;
        try {
            Thread.sleep(delay);
        } catch (Exception e) {
            //
        }

        JobDataMap data = context.getJobDetail().getJobDataMap();
        int count;
        if (data.containsKey(COUNT)) {
            count = data.getInt(COUNT);
        } else {
            count = 0;
        }
        count++;
        data.put(COUNT, count);

        log.info("SimpleRecoveryJob: " + jobKey + " done at " + new Date() + "\n Execution #" + count);

    }

    @Override
    public boolean isRefireImmediatelyWhenException() {
        return false;
    }

}
