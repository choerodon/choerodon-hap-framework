/*
 * #{copyright}#
 */
package io.choerodon.hap.job.listener;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shiliyan
 *
 */
public class DefaultJobListener implements JobListener {
    private final Logger log = LoggerFactory.getLogger(getClass());

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * 
     * Constructors.
     * 
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * 
     * Interface.
     * 
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    protected Logger getLog() {
        return log;
    }

    @Override
    public String getName() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.quartz.JobListener#jobToBeExecuted(org.quartz.JobExecutionContext)
     */
    @Override
    public void jobToBeExecuted(JobExecutionContext context) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.quartz.JobListener#jobExecutionVetoed(org.quartz.JobExecutionContext)
     */
    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.quartz.JobListener#jobWasExecuted(org.quartz.JobExecutionContext,
     * org.quartz.JobExecutionException)
     */
    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {

    }

}
