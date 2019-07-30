package io.choerodon.hap.job.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;

/**
 * @author shengyang.zhou@hand-china.com
 */
public class TriggerDto {
    @Id
    private String schedName;
    @Id
    @NotEmpty
    private String triggerName;
    @Id
    @NotEmpty
    private String triggerGroup;
    @NotEmpty
    private String jobName;
    @NotEmpty
    private String jobGroup;

    private String description;

    private Long nextFireTime;

    private Long prevFireTime;

    private Integer priority;

    private String triggerState;
    @NotEmpty
    private String triggerType;

    private Long startTime;

    private Long endTime;

    private String calendarName;

    private Integer misfireInstr;

    @JsonIgnore
    private byte[] jobData;

    public String getSchedName() {
        return schedName;
    }

    public void setSchedName(String schedName) {
        this.schedName = schedName;
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

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = StringUtils.trim(jobGroup);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getNextFireTime() {
        return nextFireTime;
    }

    public void setNextFireTime(Long nextFireTime) {
        this.nextFireTime = nextFireTime;
    }

    public Long getPrevFireTime() {
        return prevFireTime;
    }

    public void setPrevFireTime(Long prevFireTime) {
        this.prevFireTime = prevFireTime;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getTriggerState() {
        return triggerState;
    }

    public void setTriggerState(String triggerState) {
        this.triggerState = StringUtils.trim(triggerState);
    }

    public String getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(String triggerType) {
        this.triggerType = StringUtils.trim(triggerType);
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getCalendarName() {
        return calendarName;
    }

    public void setCalendarName(String calendarName) {
        this.calendarName = StringUtils.trim(calendarName);
    }

    public Integer getMisfireInstr() {
        return misfireInstr;
    }

    public void setMisfireInstr(Integer misfireInstr) {
        this.misfireInstr = misfireInstr;
    }

    public byte[] getJobData() {
        return jobData;
    }

    public void setJobData(byte[] jobData) {
        this.jobData = jobData;
    }
}