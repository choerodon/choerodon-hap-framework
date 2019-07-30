/*
 * #{copyright}#
 */

package io.choerodon.hap.job.examples;

import java.util.Date;

import io.choerodon.hap.job.AbstractJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This is just a simple job that says "Hello" to the world.
 * </p>
 * 
 * @author shiliyan
 *
 */
public class HelloJob extends AbstractJob {

    private Logger logger = LoggerFactory.getLogger(HelloJob.class);

    /**
     * <p>
     * Empty constructor for job initilization
     * </p>
     * <p>
     * Quartz requires a public empty constructor so that the scheduler can
     * instantiate the class whenever it needs.
     * </p>
     */
    public HelloJob() {
    }

    @Override
    public void safeExecute(JobExecutionContext context) {

        // Say Hello to the World and display the date/time
        logger.info("Hello World! - " + new Date());
    }

    @Override
    public boolean isRefireImmediatelyWhenException() {
        return false;
    }

    @Override
    public void jobWasExecuted(JobExecutionContext jobExecutionContext, JobExecutionException e) {
        logger.debug("{} jobWasExecuted, JobRunningInfo Id: {}", getClass().getSimpleName(),
                jobExecutionContext.get(JOB_RUNNING_INFO_ID));
    }
}
