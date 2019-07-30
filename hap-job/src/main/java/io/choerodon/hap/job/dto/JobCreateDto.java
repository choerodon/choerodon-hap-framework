package io.choerodon.hap.job.dto;

import io.choerodon.base.annotation.Children;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

/**
 * @author shengyang.zhou@hand-china.com
 */
public class JobCreateDto extends TriggerDto {
    public JobCreateDto() {
    }

    public JobCreateDto(JobInfoDetailDto jobInfoDetailDto) {
        this.setJobClassName(jobInfoDetailDto.getJobClassName());
        this.setJobGroup(jobInfoDetailDto.getJobGroup());
        this.setJobName(jobInfoDetailDto.getJobName());
        this.setTriggerType(jobInfoDetailDto.getTriggerType());
        this.setPriority(jobInfoDetailDto.getTriggerPriority());
        if (jobInfoDetailDto.getDescription() != null) {
            this.setDescription(jobInfoDetailDto.getDescription());
        }
        if (jobInfoDetailDto.getJobDatas() != null && !jobInfoDetailDto.getJobDatas().isEmpty()) {
            this.setJobDatas(jobInfoDetailDto.getJobDatas());
        }
        if (jobInfoDetailDto.getStartTime() != null) {
            this.setStartTime(jobInfoDetailDto.getStartTime().getTime());
        }
        if (jobInfoDetailDto.getEndTime() != null) {
            this.setEndTime(jobInfoDetailDto.getEndTime().getTime());
        }
        if ("SIMPLE".equals(this.getTriggerType())) {
            this.setRepeatCount(String.valueOf(jobInfoDetailDto.getRepeatCount()));
            this.setRepeatInterval(String.valueOf(jobInfoDetailDto.getRepeatInterval()));
        } else if ("CRON".equals(this.getTriggerType())) {
            this.setCronExpression(jobInfoDetailDto.getCronExpression());
        }
    }

    //    @Size(min = 20, max = 40)
    @NotEmpty
    private String jobClassName;

    private String cronExpression;

    private Date start;

    private Date end;

    private String repeatCount;

    private String repeatInterval;
    @Children
    private List<JobData> jobDatas;

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getJobClassName() {
        return jobClassName;
    }

    public void setJobClassName(String jobClassName) {
        this.jobClassName = StringUtils.trim(jobClassName);
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(String repeatCount) {
        this.repeatCount = repeatCount;
    }

    public String getRepeatInterval() {
        return repeatInterval;
    }

    public void setRepeatInterval(String repeatInterval) {
        this.repeatInterval = repeatInterval;
    }

    public List<JobData> getJobDatas() {
        return jobDatas;
    }

    public void setJobDatas(List<JobData> jobDatas) {
        this.jobDatas = jobDatas;
    }
}
