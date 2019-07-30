package io.choerodon.hap.job.service.impl;

import com.github.pagehelper.PageHelper;
import io.choerodon.hap.core.exception.FieldRequiredException;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.job.AbstractJob;
import io.choerodon.hap.job.dto.CronTriggerDto;
import io.choerodon.hap.job.dto.JobCreateDto;
import io.choerodon.hap.job.dto.JobData;
import io.choerodon.hap.job.dto.JobDetailDto;
import io.choerodon.hap.job.dto.JobInfoDetailDto;
import io.choerodon.hap.job.dto.SchedulerDto;
import io.choerodon.hap.job.dto.SimpleTriggerDto;
import io.choerodon.hap.job.dto.TriggerDto;
import io.choerodon.hap.job.exception.JobException;
import io.choerodon.hap.job.mapper.CronTriggerMapper;
import io.choerodon.hap.job.mapper.JobDetailMapper;
import io.choerodon.hap.job.mapper.SchedulerMapper;
import io.choerodon.hap.job.mapper.SimpleTriggerMapper;
import io.choerodon.hap.job.mapper.TriggerMapper;
import io.choerodon.hap.job.service.IQuartzService;
import io.choerodon.hap.system.dto.DTOStatus;
import io.choerodon.web.core.IRequest;
import org.apache.commons.beanutils.BeanUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.choerodon.hap.job.exception.JobException.JOB_EXCEPTION;
import static io.choerodon.hap.job.exception.JobException.NOT_SUB_CLASS;

/**
 * @author shengyang.zhou@hand-china.com
 */
@Service
@Dataset("JobInfoDetail")
public class QuartzServiceImpl implements IQuartzService, IDatasetService<JobInfoDetailDto> {

    private final Logger logger = LoggerFactory.getLogger(QuartzServiceImpl.class);
    public static final String PAUSED = "PAUSED";
    public static final String NORMAL = "NORMAL";

    @Autowired
    private JobDetailMapper jobDetailMapper;

    @Autowired
    private TriggerMapper triggerMapper;

    @Autowired
    private CronTriggerMapper cronTriggerMapper;

    @Autowired
    private SimpleTriggerMapper simpleTriggerMapper;

    @Autowired
    private SchedulerMapper schedulerMapper;

    @Autowired
    private Scheduler quartzScheduler;

    @Override
    public List<TriggerDto> getTriggers(IRequest request, TriggerDto example, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return triggerMapper.selectTriggers(example);
    }

    @Override
    public CronTriggerDto getCronTrigger(String triggerName, String triggerGroup) throws SchedulerException {
        CronTriggerDto dto = new CronTriggerDto();
        dto.setSchedName(quartzScheduler.getSchedulerName());
        dto.setTriggerName(triggerName);
        dto.setTriggerGroup(triggerGroup);
        return cronTriggerMapper.selectByPrimaryKey(dto);
    }

    @Override
    public SimpleTriggerDto getSimpleTrigger(String triggerName, String triggerGroup) throws SchedulerException {
        SimpleTriggerDto dto = new SimpleTriggerDto();
        dto.setSchedName(quartzScheduler.getSchedulerName());
        dto.setTriggerName(triggerName);
        dto.setTriggerGroup(triggerGroup);
        return simpleTriggerMapper.selectByPrimaryKey(dto);
    }

    @Override
    public List<JobInfoDetailDto> getJobInfoDetails(IRequest request, JobDetailDto example, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);

        List<JobInfoDetailDto> selectJobInfoDetails = jobDetailMapper.selectJobInfoDetails(example);

        for (JobInfoDetailDto jobInfoDetailDto : selectJobInfoDetails) {
            try {
                JobKey jobKey = new JobKey(jobInfoDetailDto.getJobName(), jobInfoDetailDto.getJobGroup());
                JobDetail jobDetail = quartzScheduler.getJobDetail(jobKey);
                JobDataMap jobDataMap = jobDetail.getJobDataMap();
                String[] keys = jobDataMap.getKeys();
                List<JobData> jobDatas = new ArrayList<JobData>();
                for (String string : keys) {
                    JobData e = new JobData();
                    e.setName(string);
                    e.setValue(jobDataMap.getString(string));
                    jobDatas.add(e);
                }
                List<Trigger> triggers = (List<Trigger>) quartzScheduler.getTriggersOfJob(jobKey);
                if (triggers == null || triggers.isEmpty()) {
                    logger.error(JobException.NOT_TRIGGER + "--" + jobKey.getGroup() + "." + jobKey.getName());
                    continue;
                }
                Trigger trigger = triggers.get(0);
                if (trigger instanceof SimpleTrigger) {
                    jobInfoDetailDto.setTriggerType("SIMPLE");
                    jobInfoDetailDto.setRepeatCount(((SimpleTrigger) trigger).getRepeatCount());
                    jobInfoDetailDto.setRepeatInterval(((SimpleTrigger) trigger).getRepeatInterval());
                } else if (trigger instanceof CronTrigger) {
                    jobInfoDetailDto.setCronExpression(((CronTrigger) trigger).getCronExpression());
                    jobInfoDetailDto.setTriggerType("CRON");
                }
                jobInfoDetailDto.setTriggerName(trigger.getKey().getName());
                jobInfoDetailDto.setTriggerGroup(trigger.getKey().getGroup());
                jobInfoDetailDto.setTriggerPriority(trigger.getPriority());
                jobInfoDetailDto.setStartTime(trigger.getStartTime());
                jobInfoDetailDto.setPreviousFireTime(trigger.getPreviousFireTime());
                jobInfoDetailDto.setNextFireTime(trigger.getNextFireTime());
                jobInfoDetailDto.setEndTime(trigger.getEndTime());

                jobInfoDetailDto.setJobDatas(jobDatas);

                // get running state of job
                Trigger.TriggerState ts = quartzScheduler.getTriggerState(trigger.getKey());
                jobInfoDetailDto.setRunningState(ts.name());
            } catch (SchedulerException e) {
                jobInfoDetailDto.setRunningState(Trigger.TriggerState.ERROR.name());
                if (logger.isErrorEnabled()) {
                    logger.error(e.getMessage(), e);
                }
            }
        }

        return selectJobInfoDetails;
    }

    @Override
    public List<JobDetailDto> getJobDetails(IRequest request, JobDetailDto example, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return jobDetailMapper.selectJobDetails(example);
    }

    @Override
    public Map<String, Object> schedulerInformation() throws SchedulerException {
        Map<String, Object> infoMap = new HashMap<>();
        SchedulerMetaData metaData = quartzScheduler.getMetaData();
        if (metaData.getRunningSince() != null) {
            infoMap.put("runningSince", metaData.getRunningSince().getTime());
        }
        infoMap.put("numberOfJobsExecuted", metaData.getNumberOfJobsExecuted());
        infoMap.put("schedulerName", metaData.getSchedulerName());
        infoMap.put("schedulerInstanceId", metaData.getSchedulerInstanceId());
        infoMap.put("threadPoolSize", metaData.getThreadPoolSize());
        infoMap.put("version", metaData.getVersion());
        infoMap.put("inStandbyMode", metaData.isInStandbyMode());
        infoMap.put("jobStoreClustered", metaData.isJobStoreClustered());
        infoMap.put("jobStoreClass", metaData.getJobStoreClass());
        infoMap.put("jobStoreSupportsPersistence", metaData.isJobStoreSupportsPersistence());
        infoMap.put("started", metaData.isStarted());
        infoMap.put("shutdown", metaData.isShutdown());
        infoMap.put("schedulerRemote", metaData.isSchedulerRemote());
        return infoMap;
    }

    @Override
    public List<SchedulerDto> selectSchedulers(SchedulerDto schedulerDto, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return schedulerMapper.selectSchedulers(schedulerDto);
    }

    @Override
    public void createJob(JobCreateDto jobCreateDto)
            throws SchedulerException, JobException {
        if (StringUtils.isEmpty(jobCreateDto.getJobClassName())) {
            throw new RuntimeException(new FieldRequiredException("hap.validation.notempty", new Object[]{"jobClassName"}));
        } else if (StringUtils.isEmpty(jobCreateDto.getJobName())) {
            throw new RuntimeException(new FieldRequiredException("hap.validation.notempty", new Object[]{"jobName"}));
        } else if (StringUtils.isEmpty(jobCreateDto.getJobGroup())) {
            throw new RuntimeException(new FieldRequiredException("hap.validation.notempty", new Object[]{"jobGroup"}));
        } else if (StringUtils.isEmpty(jobCreateDto.getTriggerName())) {
            throw new RuntimeException(new FieldRequiredException("hap.validation.notempty", new Object[]{"triggerName"}));
        } else if (StringUtils.isEmpty(jobCreateDto.getTriggerGroup())) {
            throw new RuntimeException(new FieldRequiredException("hap.validation.notempty", new Object[]{"triggerGroup"}));
        } else if (StringUtils.isEmpty(jobCreateDto.getTriggerType())) {
            throw new RuntimeException(new FieldRequiredException("hap.validation.notempty", new Object[]{"triggerType"}));
        }

        String jobClassName = jobCreateDto.getJobClassName();

        boolean assignableFrom = false;
        Class forName = null;
        try {
            forName = Class.forName(jobClassName);
            assignableFrom = AbstractJob.class.isAssignableFrom(forName);
        } catch (ClassNotFoundException e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
            }
        }
        if (!assignableFrom || forName == null) {
            String name = AbstractJob.class.getName();
            throw new JobException(JOB_EXCEPTION, NOT_SUB_CLASS, new Object[]{jobClassName, name});
        }

        JobBuilder jb = JobBuilder.newJob(forName).withIdentity(jobCreateDto.getJobName(), jobCreateDto.getJobGroup())
                .withDescription(jobCreateDto.getDescription());

        if (hasJobData(jobCreateDto)) {
            JobDataMap data = new JobDataMap();
            List<JobData> jobDatas = jobCreateDto.getJobDatas();
            for (JobData jobData : jobDatas) {
                data.put(jobData.getName(), jobData.getValue());
            }
            jb = jb.usingJobData(data);
        }
        JobDetail jobDetail = jb.build();

        int triggerPriority = jobCreateDto.getPriority() == null ? 5 : jobCreateDto.getPriority();
        TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger()
                .withIdentity(jobCreateDto.getTriggerName(), jobCreateDto.getTriggerGroup())
                .withPriority(triggerPriority)
                .forJob(jobDetail);
        if (jobCreateDto.getStartTime() != null && jobCreateDto.getStartTime() > 0) {
            triggerBuilder.startAt(new Date(jobCreateDto.getStartTime()));
        }
        if (jobCreateDto.getEndTime() != null && jobCreateDto.getEndTime() > 0) {
            triggerBuilder.endAt(new Date(jobCreateDto.getEndTime()));
        }
        ScheduleBuilder sche = null;
        if ("CRON".equalsIgnoreCase(jobCreateDto.getTriggerType())) {
            if (org.apache.commons.lang.StringUtils.isEmpty(jobCreateDto.getCronExpression())) {
                throw new RuntimeException(new FieldRequiredException("hap.validation.notempty", new Object[]{"cronExpression"}));
            }
            sche = CronScheduleBuilder.cronSchedule(jobCreateDto.getCronExpression());

        } else if ("SIMPLE".equalsIgnoreCase(jobCreateDto.getTriggerType())) {
            if (StringUtils.isEmpty(jobCreateDto.getRepeatInterval())) {
                throw new RuntimeException(new FieldRequiredException("hap.validation.notempty", new Object[]{"repeatInterval"}));
            }
            int interval = Integer.parseInt(jobCreateDto.getRepeatInterval());
            int count = 0;
            try {
                count = Integer.parseInt(jobCreateDto.getRepeatCount());
            } catch (Throwable thr) {
            }
            if (count < 1) {
                sche = SimpleScheduleBuilder.repeatSecondlyForever(interval);
            } else {
                sche = SimpleScheduleBuilder.repeatSecondlyForTotalCount(count, interval);
            }

        }

        Trigger trigger = triggerBuilder.withSchedule(sche).build();
        quartzScheduler.scheduleJob(jobDetail, trigger);
    }

    private boolean hasJobData(JobCreateDto jobCreateDto) {
        List<JobData> jobDatas = jobCreateDto.getJobDatas();
        return jobDatas != null && !jobDatas.isEmpty();
    }

    /**
     * 删除定时任务.
     *
     * @see IQuartzService#deleteJob(java.lang.String, java.lang.String)
     */
    @Override
    public void deleteJob(String jobName, String jobGroup) throws SchedulerException {
        quartzScheduler.deleteJob(new JobKey(jobName, jobGroup));
    }

    @Override
    public Map<String, Object> start() throws SchedulerException {
        quartzScheduler.start();
        return schedulerInformation();
    }

    @Override
    public Map<String, Object> standby() throws SchedulerException {
        quartzScheduler.standby();
        return schedulerInformation();
    }

    @Override
    public Map<String, Object> pauseAll() throws SchedulerException {
        quartzScheduler.pauseAll();
        return schedulerInformation();
    }

    @Override
    public Map<String, Object> resumeAll() throws SchedulerException {
        quartzScheduler.resumeAll();
        return schedulerInformation();
    }

    @Override
    public void pauseJobs(List<JobDetailDto> list) throws SchedulerException {
        for (JobDetailDto job : list) {
            quartzScheduler.pauseJob(JobKey.jobKey(job.getJobName(), job.getJobGroup()));
        }
    }

    @Override
    public void pauseJob(JobDetailDto job) throws SchedulerException {
        quartzScheduler.pauseJob(JobKey.jobKey(job.getJobName(), job.getJobGroup()));
    }

    @Override
    public void resumeJobs(List<JobDetailDto> list) throws SchedulerException {
        for (JobDetailDto job : list) {
            quartzScheduler.resumeJob(JobKey.jobKey(job.getJobName(), job.getJobGroup()));
        }
    }

    @Override
    public void resumeJob(JobDetailDto job) throws SchedulerException {
        quartzScheduler.resumeJob(JobKey.jobKey(job.getJobName(), job.getJobGroup()));
    }

    @Override
    public void deleteJobs(List<JobDetailDto> list) throws SchedulerException {
        for (JobDetailDto job : list) {
            quartzScheduler.deleteJob(JobKey.jobKey(job.getJobName(), job.getJobGroup()));
        }
    }

    @Override
    public void pauseTriggers(List<TriggerDto> list) throws SchedulerException {
        for (TriggerDto trigger : list) {
            quartzScheduler.pauseTrigger(TriggerKey.triggerKey(trigger.getTriggerName(), trigger.getTriggerGroup()));
        }
    }

    @Override
    public void resumeTriggers(List<TriggerDto> list) throws SchedulerException {
        for (TriggerDto trigger : list) {
            quartzScheduler.resumeTrigger(TriggerKey.triggerKey(trigger.getTriggerName(), trigger.getTriggerGroup()));
        }
    }

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        JobDetailDto jobDetailDto = new JobDetailDto();
        try {
            BeanUtils.populate(jobDetailDto, body);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        // 处理 重复次数 和 重复间隔
        List<JobInfoDetailDto> results = getJobInfoDetails(null, jobDetailDto, page, pageSize);
        for (JobInfoDetailDto jobInfoDetailDto : results) {
            jobInfoDetailDto.setRepeatCount(jobInfoDetailDto.getRepeatCount() + 1);
            jobInfoDetailDto.setRepeatInterval(jobInfoDetailDto.getRepeatInterval() / 1000);
        }
        return results;
    }

    @Override
    public List<JobInfoDetailDto> mutations(List<JobInfoDetailDto> objs) {
        try {
            for (JobInfoDetailDto job : objs) {
                switch (job.get__status()) {
                    case DTOStatus.UPDATE: {
                        if (job.getRunningState().equals(PAUSED)) {
                            pauseJob(job);
                        }
                        if (job.getRunningState().equals(NORMAL)) {
                            resumeJob(job);
                        }
                        break;
                    }
                    case DTOStatus.DELETE: {
                        deleteJob(job.getJobName(), job.getJobGroup());
                        break;
                    }
                    case DTOStatus.ADD: {
                        JobCreateDto jobCreateDto = new JobCreateDto(job);
                        jobCreateDto.setTriggerGroup(jobCreateDto.getJobGroup());
                        jobCreateDto.setTriggerName(jobCreateDto.getJobName() + "_trigger");
                        try {
                            createJob(jobCreateDto);
                        } catch (SchedulerException | JobException e) {
                            throw new DatasetException("dataset.error", e);
                        }
                    }
                    default:
                        break;
                }
            }
        } catch (SchedulerException e) {
            throw new DatasetException("dataset.error", e);
        }
        return objs;
    }
}