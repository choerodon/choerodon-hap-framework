/*
 * #{copyright}#
 */
package io.choerodon.hap.job.dto;

import javax.persistence.Id;

/**
 *
 * @author shengyang.zhou@hand-china.com
 */
public class SimpleTriggerDto {
    @Id
    private String schedName;
    @Id
    private String triggerName;
    @Id
    private String triggerGroup;

    private Long repeatCount;

    private Long repeatInterval;

    private Long timesTriggered;

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

    public Long getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(Long repeatCount) {
        this.repeatCount = repeatCount;
    }

    public Long getRepeatInterval() {
        return repeatInterval;
    }

    public void setRepeatInterval(Long repeatInterval) {
        this.repeatInterval = repeatInterval;
    }

    public Long getTimesTriggered() {
        return timesTriggered;
    }

    public void setTimesTriggered(Long timesTriggered) {
        this.timesTriggered = timesTriggered;
    }
}