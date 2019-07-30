/*
 * #{copyright}#
 */
package io.choerodon.hap.job.listener;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shiliyan
 *
 */
@Deprecated
public class DefaultSchedulerListener implements SchedulerListener {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /* (non-Javadoc)
     * @see org.quartz.SchedulerListener#jobScheduled(org.quartz.Trigger)
     */
    @Override
    public void jobScheduled(Trigger trigger) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.quartz.SchedulerListener#jobUnscheduled(org.quartz.TriggerKey)
     */
    @Override
    public void jobUnscheduled(TriggerKey triggerKey) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.quartz.SchedulerListener#triggerFinalized(org.quartz.Trigger)
     */
    @Override
    public void triggerFinalized(Trigger trigger) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.quartz.SchedulerListener#triggerPaused(org.quartz.TriggerKey)
     */
    @Override
    public void triggerPaused(TriggerKey triggerKey) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.quartz.SchedulerListener#triggersPaused(java.lang.String)
     */
    @Override
    public void triggersPaused(String triggerGroup) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.quartz.SchedulerListener#triggerResumed(org.quartz.TriggerKey)
     */
    @Override
    public void triggerResumed(TriggerKey triggerKey) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.quartz.SchedulerListener#triggersResumed(java.lang.String)
     */
    @Override
    public void triggersResumed(String triggerGroup) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.quartz.SchedulerListener#jobAdded(org.quartz.JobDetail)
     */
    @Override
    public void jobAdded(JobDetail jobDetail) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.quartz.SchedulerListener#jobDeleted(org.quartz.JobKey)
     */
    @Override
    public void jobDeleted(JobKey jobKey) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.quartz.SchedulerListener#jobPaused(org.quartz.JobKey)
     */
    @Override
    public void jobPaused(JobKey jobKey) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.quartz.SchedulerListener#jobsPaused(java.lang.String)
     */
    @Override
    public void jobsPaused(String jobGroup) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.quartz.SchedulerListener#jobResumed(org.quartz.JobKey)
     */
    @Override
    public void jobResumed(JobKey jobKey) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.quartz.SchedulerListener#jobsResumed(java.lang.String)
     */
    @Override
    public void jobsResumed(String jobGroup) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.quartz.SchedulerListener#schedulerError(java.lang.String, org.quartz.SchedulerException)
     */
    @Override
    public void schedulerError(String msg, SchedulerException cause) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.quartz.SchedulerListener#schedulerInStandbyMode()
     */
    @Override
    public void schedulerInStandbyMode() {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.quartz.SchedulerListener#schedulerStarted()
     */
    @Override
    public void schedulerStarted() {

    }

    /* (non-Javadoc)
     * @see org.quartz.SchedulerListener#schedulerStarting()
     */
    @Override
    public void schedulerStarting() {

    }

    /* (non-Javadoc)
     * @see org.quartz.SchedulerListener#schedulerShutdown()
     */
    @Override
    public void schedulerShutdown() {

    }

    /* (non-Javadoc)
     * @see org.quartz.SchedulerListener#schedulerShuttingdown()
     */
    @Override
    public void schedulerShuttingdown() {

    }

    /* (non-Javadoc)
     * @see org.quartz.SchedulerListener#schedulingDataCleared()
     */
    @Override
    public void schedulingDataCleared() {

    }

    public Logger getLog() {
        return logger;
    }
    
    protected void logInfo(String info, Object... para) {
        if (logger.isInfoEnabled()) {
            logger.info(info, para);
        }
    }

    protected void logInfo(String info) {
        if (logger.isInfoEnabled()) {
            logger.info(info);
        }
    }

}
