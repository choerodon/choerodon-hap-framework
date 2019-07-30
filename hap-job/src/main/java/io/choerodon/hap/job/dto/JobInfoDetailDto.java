package io.choerodon.hap.job.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.choerodon.base.util.BaseConstants;

import java.util.Date;

/**
 * @author liyan.shi@hand-china.com
 */
public class JobInfoDetailDto extends JobDetailDto {

    @JsonFormat(pattern = BaseConstants.DATE_TIME_FORMAT)
    private Date startTime;

    private Date endTime;
    private String triggerType;

    private String cronExpression;

    private int repeatCount;
    private long repeatInterval;

    private String jobResult;

    private String jobStatus;

    private String jobStatusMessage;

    private String triggerName;

    private String triggerGroup;

    private int triggerPriority;

    @JsonFormat(pattern = BaseConstants.DATE_TIME_FORMAT)
    private Date previousFireTime;

    @JsonFormat(pattern = BaseConstants.DATE_TIME_FORMAT)
    private Date fireTime;

    @JsonFormat(pattern = BaseConstants.DATE_TIME_FORMAT)
    private Date nextFireTime;

    private Long refireCount;

    private String fireInstanceId;

    private String schedulerInstanceId;

    @JsonFormat(pattern = BaseConstants.DATE_TIME_FORMAT)
    private Date scheduledFireTime;

    private String runningState;

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @JsonFormat(pattern = BaseConstants.DATE_TIME_FORMAT)
    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    public long getRepeatInterval() {
        return repeatInterval;
    }

    public void setRepeatInterval(long repeatInterval) {
        this.repeatInterval = repeatInterval;
    }

    public String getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(String triggerType) {
        this.triggerType = triggerType;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getJobResult() {
        return jobResult;
    }

    public void setJobResult(String jobResult) {
        this.jobResult = jobResult;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getJobStatusMessage() {
        return jobStatusMessage;
    }

    public void setJobStatusMessage(String jobStatusMessage) {
        this.jobStatusMessage = jobStatusMessage;
    }

    public String getTriggerName() {
        return triggerName;
    }

    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }

    public String getTriggerGroup() {
        return triggerGroup;
    }

    public void setTriggerGroup(String triggerGroup) {
        this.triggerGroup = triggerGroup;
    }

    public Date getPreviousFireTime() {
        return previousFireTime;
    }

    public void setPreviousFireTime(Date previousFireTime) {
        this.previousFireTime = previousFireTime;
    }

    public Date getFireTime() {
        return fireTime;
    }

    public void setFireTime(Date fireTime) {
        this.fireTime = fireTime;
    }

    public Date getNextFireTime() {
        return nextFireTime;
    }

    public void setNextFireTime(Date nextFireTime) {
        this.nextFireTime = nextFireTime;
    }

    public Long getRefireCount() {
        return refireCount;
    }

    public void setRefireCount(Long refireCount) {
        this.refireCount = refireCount;
    }

    public String getFireInstanceId() {
        return fireInstanceId;
    }

    public void setFireInstanceId(String fireInstanceId) {
        this.fireInstanceId = fireInstanceId;
    }

    public String getSchedulerInstanceId() {
        return schedulerInstanceId;
    }

    public void setSchedulerInstanceId(String schedulerInstanceId) {
        this.schedulerInstanceId = schedulerInstanceId;
    }

    public Date getScheduledFireTime() {
        return scheduledFireTime;
    }

    public void setScheduledFireTime(Date scheduledFireTime) {
        this.scheduledFireTime = scheduledFireTime;
    }

    public String getRunningState() {
        return runningState;
    }

    public void setRunningState(String runningState) {
        this.runningState = runningState;
    }

    public int getTriggerPriority() {
        return triggerPriority;
    }

    public void setTriggerPriority(int triggerPriority) {
        this.triggerPriority = triggerPriority;
    }
}
