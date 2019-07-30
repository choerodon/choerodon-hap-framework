package io.choerodon.hap.job.service;

import io.choerodon.hap.job.dto.CronTriggerDto;
import io.choerodon.hap.job.dto.JobCreateDto;
import io.choerodon.hap.job.dto.JobDetailDto;
import io.choerodon.hap.job.dto.JobInfoDetailDto;
import io.choerodon.hap.job.dto.SchedulerDto;
import io.choerodon.hap.job.dto.SimpleTriggerDto;
import io.choerodon.hap.job.dto.TriggerDto;
import io.choerodon.hap.job.exception.JobException;
import io.choerodon.web.core.IRequest;
import org.quartz.SchedulerException;

import java.util.List;
import java.util.Map;

/**
 * @author shengyang.zhou@hand-china.com
 */
public interface IQuartzService {

    /**
     * 查询触发器列表.
     *
     * @param request  session信息
     * @param example  参数
     * @param page     页码
     * @param pagesize 每页数量
     * @return 触发器列表
     */
    List<TriggerDto> getTriggers(IRequest request, TriggerDto example, int page, int pagesize);

    /**
     * 查询Cron触发器列表.
     *
     * @param triggerName  触发器名
     * @param triggerGroup 触发器组
     * @return 触发器列表
     * @throws SchedulerException Base class for exceptions thrown by the Quartz Scheduler.
     */
    CronTriggerDto getCronTrigger(String triggerName, String triggerGroup) throws SchedulerException;

    /**
     * 查询Simple触发器列表.
     *
     * @param triggerName  触发器名
     * @param triggerGroup 触发器组
     * @return 触发器列表
     * @throws SchedulerException Base class for exceptions thrown by the Quartz Scheduler.
     */
    SimpleTriggerDto getSimpleTrigger(String triggerName, String triggerGroup) throws SchedulerException;

    /**
     * 查询job列表.
     *
     * @param request  session信息
     * @param example  查询参数
     * @param page     页码
     * @param pagesize 每页数量
     * @return job结果列表
     */
    List<JobDetailDto> getJobDetails(IRequest request, JobDetailDto example, int page, int pagesize);

    /**
     * .
     *
     * @return scheduler信息
     * @throws SchedulerException Base class for exceptions thrown by the Quartz Scheduler.
     * @deprecated
     */
    Map<String, Object> schedulerInformation() throws SchedulerException;

    /**
     * .
     *
     * @param schedulerDto 查询参数
     * @param page         页码
     * @param pagesize     每页数量
     * @return SchedulerDto
     * @deprecated
     */
    List<SchedulerDto> selectSchedulers(SchedulerDto schedulerDto, int page, int pagesize);

    /**
     * 新建一个JOB.
     *
     * @param jobCreateDto dto
     * @throws SchedulerException     Base class for exceptions thrown by the Quartz Scheduler.
     * @throws JobException           Job异常
     * @throws ClassNotFoundException ClassNotFoundException
     */
    void createJob(JobCreateDto jobCreateDto) throws ClassNotFoundException, SchedulerException, JobException;

    /**
     * 删除job.
     *
     * @param jobName  job名称
     * @param jobGroup job组
     * @throws SchedulerException Base class for exceptions thrown by the Quartz Scheduler.
     */
    void deleteJob(String jobName, String jobGroup) throws SchedulerException;

    /**
     * 启动当前的Scheduler.
     *
     * @return Scheduler信息
     * @throws SchedulerException Scheduler异常
     * @deprecated
     */
    Map<String, Object> start() throws SchedulerException;

    /**
     * standby当前的Scheduler.
     *
     * @return Scheduler信息
     * @throws SchedulerException Scheduler异常
     * @deprecated
     */
    Map<String, Object> standby() throws SchedulerException;

    /**
     * 暂停所有job.
     *
     * @return Scheduler信息
     * @throws SchedulerException Scheduler异常
     * @deprecated
     */
    Map<String, Object> pauseAll() throws SchedulerException;

    /**
     * 继续所有job.
     *
     * @return Scheduler信息
     * @throws SchedulerException Scheduler异常
     * @deprecated
     */
    Map<String, Object> resumeAll() throws SchedulerException;

    /**
     * 暂停job.
     *
     * @param list job列表
     * @throws SchedulerException Base class for exceptions thrown by the Quartz Scheduler.
     */
    void pauseJobs(List<JobDetailDto> list) throws SchedulerException;

    /**
     * 暂停job.
     *
     * @param job job详情
     * @throws SchedulerException
     */
    void pauseJob(JobDetailDto job) throws SchedulerException;

    /**
     * 继续执行job.
     *
     * @param list job 列表
     * @throws SchedulerException Base class for exceptions thrown by the Quartz Scheduler.
     */
    void resumeJobs(List<JobDetailDto> list) throws SchedulerException;

    /**
     * 继续执行job
     *
     * @param job job详情
     * @throws SchedulerException Base class for exceptions thrown by the Quartz Scheduler.
     */
    void resumeJob(JobDetailDto job) throws SchedulerException;

    /**
     * 删除job.
     *
     * @param list job列表
     * @throws SchedulerException Base class for exceptions thrown by the Quartz Scheduler.
     */
    void deleteJobs(List<JobDetailDto> list) throws SchedulerException;

    /**
     * 暂停触发器.
     *
     * @param list 触发器列表
     * @throws SchedulerException Base class for exceptions thrown by the Quartz Scheduler.
     */
    void pauseTriggers(List<TriggerDto> list) throws SchedulerException;

    /**
     * 继续执行触发器.
     *
     * @param list 触发器列表
     * @throws SchedulerException Base class for exceptions thrown by the Quartz Scheduler.
     */
    void resumeTriggers(List<TriggerDto> list) throws SchedulerException;

    /**
     * 查询job列表.
     *
     * @param request  session信息
     * @param example  查询参数
     * @param page     页码
     * @param pagesize 每页数量
     * @return job结果列表
     */
    List<JobInfoDetailDto> getJobInfoDetails(IRequest request, JobDetailDto example, int page, int pagesize);

}
