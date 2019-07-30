/*
 * #{copyright}#
 */
package io.choerodon.hap.job.listener;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.Date;

import io.choerodon.hap.job.service.IJobRunningInfoService;
import io.choerodon.hap.job.AbstractJob;
import io.choerodon.hap.job.dto.JobRunningInfoDto;
import org.apache.commons.lang.StringUtils;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.context.ApplicationContext;

/**
 * @author shiliyan
 *
 */
public class JobRunningListener extends DefaultJobListener {

    private static final String VETOED = "Vetoed";

    private static final String FINISH = "Finish";

    private static final String FAILED = "Failed";

    private String name = "JobRunningListener";

    private String jobToBeFiredMessage = "Job {1}.{0} fired (by trigger {4}.{3}) at: {2, date, HH:mm:ss MM/dd/yyyy}";

    private String jobSuccessMessage = "Job {1}.{0} execution complete at {2, date, HH:mm:ss MM/dd/yyyy} "
            + "and reports: {8}";

    private String jobFailedMessage = "Job {1}.{0} execution failed at {2, date, HH:mm:ss MM/dd/yyyy} and reports: {8}";

    private String jobWasVetoedMessage = "Job {1}.{0} was vetoed.  It was to be fired (by trigger {4}.{3}) "
            + "at: {2, date, HH:mm:ss MM/dd/yyyy}";

    private IJobRunningInfoService jobRunningInfoService;

    private ApplicationContext applicationContext;

    public JobRunningListener(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Get the message that is logged when a Job successfully completes its
     * execution.
     * 
     * @return jobSuccessMessage
     */
    public String getJobSuccessMessage() {
        return jobSuccessMessage;
    }

    /**
     * Get the message that is logged when a Job fails its execution.
     * 
     * @return jobFailedMessage
     */
    public String getJobFailedMessage() {
        return jobFailedMessage;
    }

    /**
     * Get the message that is logged when a Job is about to execute.
     * 
     * @return jobToBeFiredMessage
     */
    public String getJobToBeFiredMessage() {
        return jobToBeFiredMessage;
    }

    /**
     * Set the message that is logged when a Job successfully completes its
     * execution.
     * 
     * @param jobSuccessMessage
     *            String in java.text.MessageFormat syntax.
     */
    public void setJobSuccessMessage(String jobSuccessMessage) {
        this.jobSuccessMessage = jobSuccessMessage;
    }

    /**
     * Set the message that is logged when a Job fails its execution.
     * 
     * @param jobFailedMessage
     *            String in java.text.MessageFormat syntax.
     */
    public void setJobFailedMessage(String jobFailedMessage) {
        this.jobFailedMessage = jobFailedMessage;
    }

    /**
     * Set the message that is logged when a Job is about to execute.
     * 
     * @param jobToBeFiredMessage
     *            String in java.text.MessageFormat syntax.
     */
    public void setJobToBeFiredMessage(String jobToBeFiredMessage) {
        this.jobToBeFiredMessage = jobToBeFiredMessage;
    }

    /**
     * Get the message that is logged when a Job execution is vetoed by a
     * trigger listener.
     * 
     * @return jobWasVetoedMessage
     */
    public String getJobWasVetoedMessage() {
        return jobWasVetoedMessage;
    }

    /**
     * Set the message that is logged when a Job execution is vetoed by a
     * trigger listener.
     * 
     * @param jobWasVetoedMessage
     *            String in java.text.MessageFormat syntax.
     */
    public void setJobWasVetoedMessage(String jobWasVetoedMessage) {
        this.jobWasVetoedMessage = jobWasVetoedMessage;
    }
    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * 
     * JobListener Interface.
     * 
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    /*
     * Object[] arguments = { new Integer(7), new
     * Date(System.currentTimeMillis()), "a disturbance in the Force" };
     * 
     * String result = MessageFormat.format( "At {1,time} on {1,date}, there was
     * {2} on planet {0,number,integer}.", arguments);
     */

    public String getName() {
        return name;
    }

    /**
     * @see org.quartz.JobListener#jobToBeExecuted(JobExecutionContext)
     */
    public void jobToBeExecuted(JobExecutionContext context) {
        Job job = context.getJobInstance();
        if (job instanceof JobListener) {
            ((JobListener) job).jobToBeExecuted(context);
        }
        if (!getLog().isInfoEnabled()) {
            return;
        }

        Trigger trigger = context.getTrigger();

        Object[] args = { context.getJobDetail().getKey().getName(), context.getJobDetail().getKey().getGroup(),
                new java.util.Date(), trigger.getKey().getName(), trigger.getKey().getGroup(),
                trigger.getPreviousFireTime(), trigger.getNextFireTime(), context.getRefireCount() };

        if (getLog().isInfoEnabled()) {
            getLog().info(MessageFormat.format(getJobToBeFiredMessage(), args));
        }
    }

    /**
     * @see org.quartz.JobListener#jobWasExecuted(JobExecutionContext,
     *      JobExecutionException)
     */
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {

        Trigger trigger = context.getTrigger();

        Object[] args = null;
        JobRunningInfoDto dto = dto(context);

        if (jobException != null) {
            String errMsg = jobException.getMessage();
            args = new Object[] { context.getJobDetail().getKey().getName(), context.getJobDetail().getKey().getGroup(),
                    new java.util.Date(), trigger.getKey().getName(), trigger.getKey().getGroup(),
                    trigger.getPreviousFireTime(), trigger.getNextFireTime(), Integer.valueOf(context.getRefireCount()),
                    errMsg };
            String message = MessageFormat.format(getJobFailedMessage(), args);
            message = StringUtils.abbreviate(message, 225);
            if (getLog().isWarnEnabled()) {
                getLog().warn(message, jobException);
            }
            dto.setJobStatusMessage(message);
            dto.setJobStatus(FAILED);

        } else {
            String result = String.valueOf(context.getResult());
            args = new Object[] { context.getJobDetail().getKey().getName(), context.getJobDetail().getKey().getGroup(),
                    new java.util.Date(), trigger.getKey().getName(), trigger.getKey().getGroup(),
                    trigger.getPreviousFireTime(), trigger.getNextFireTime(), Integer.valueOf(context.getRefireCount()),
                    result };
            String message = MessageFormat.format(getJobSuccessMessage(), args);
            if (getLog().isInfoEnabled()) {
                getLog().info(message);
            }
            dto.setJobStatusMessage(message);
            dto.setJobStatus(FINISH);
        }
        saveDto(dto);
        Job job = context.getJobInstance();
        if (job instanceof JobListener) {
            context.put("JOB_RUNNING_INFO_ID", dto.getJobRunningInfoId());
            ((JobListener) job).jobWasExecuted(context, jobException);
        }

    }

    private JobRunningInfoDto dto(JobExecutionContext context) {
        JobRunningInfoDto record = new JobRunningInfoDto();
        JobDetail jobDetail = context.getJobDetail();
        String jobName = jobDetail.getKey().getName();
        String jobGroup = jobDetail.getKey().getGroup();
        Trigger trigger = context.getTrigger();
        String triggerName = trigger.getKey().getName();
        String triggerGroup = trigger.getKey().getGroup();
        // Date preFireTime = trigger.getPreviousFireTime();
        Date nextFireTime = trigger.getNextFireTime();
        int refireCount = context.getRefireCount();
        String fireInstanceId = context.getFireInstanceId();
        Date fireTime = context.getFireTime();
        // Job jobInstance = context.getJobInstance();
        // long jobRunTime = context.getJobRunTime();
        // Date nextFireTime2 = context.getNextFireTime();
        Date previousFireTime = context.getPreviousFireTime();
        Object result = context.getResult();
        Date scheduledFireTime = context.getScheduledFireTime();

        Job jobInstance = context.getJobInstance();
        if (jobInstance instanceof AbstractJob) {
            String executionSummary = ((AbstractJob) jobInstance).getExecutionSummary();
            record.setExecutionSummary(executionSummary);
        }

        String schedulerInstanceId = "";
        try {
            schedulerInstanceId = context.getScheduler().getSchedulerInstanceId();
        } catch (SchedulerException e) {
            if (getLog().isErrorEnabled()) {
                getLog().error(e.getMessage(), e);
            }
        }
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        record.setIpAddress(inetAddress.getHostAddress());
        record.setCreatedBy(1L);
        record.setFireInstanceId(fireInstanceId);
        record.setFireTime(fireTime);
        record.setJobGroup(jobGroup);
        record.setJobName(jobName);
        record.setJobResult(result == null ? "" : String.valueOf(context.getResult()));
        record.setLastUpdatedBy(1L);
        record.setNextFireTime(nextFireTime);
        record.setPreviousFireTime(previousFireTime);
        record.setRefireCount((long) refireCount);
        record.setScheduledFireTime(scheduledFireTime);
        record.setSchedulerInstanceId(schedulerInstanceId);
        record.setTriggerGroup(triggerGroup);
        record.setTriggerName(triggerName);

        return record;
    }

    /**
     * @see org.quartz.JobListener#jobExecutionVetoed(org.quartz.JobExecutionContext)
     */
    public void jobExecutionVetoed(JobExecutionContext context) {

        Trigger trigger = context.getTrigger();
        Object[] args = { context.getJobDetail().getKey().getName(), context.getJobDetail().getKey().getGroup(),
                new java.util.Date(), trigger.getKey().getName(), trigger.getKey().getGroup(),
                trigger.getPreviousFireTime(), trigger.getNextFireTime(), context.getRefireCount() };

        String message = MessageFormat.format(getJobWasVetoedMessage(), args);
        if (getLog().isInfoEnabled()) {

            getLog().info(message);
        }
        JobRunningInfoDto dto = dto(context);
        if (message.length() > 2000) {
            message = message.substring(0, 2000);
        }
        dto.setJobStatusMessage(message);
        dto.setJobStatus(VETOED);
        saveDto(dto);
    }

    private void saveDto(JobRunningInfoDto jobCreateDto) {
        jobRunningInfoService = applicationContext.getBean(IJobRunningInfoService.class);
        jobRunningInfoService.createJobRunningInfo(jobCreateDto);
    }
}
