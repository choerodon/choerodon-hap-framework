package io.choerodon.hap.job.controllers;

import io.choerodon.hap.core.exception.FieldRequiredException;
import io.choerodon.hap.job.dto.JobCreateDto;
import io.choerodon.hap.job.dto.JobDetailDto;
import io.choerodon.hap.job.dto.SchedulerDto;
import io.choerodon.hap.job.dto.TriggerDto;
import io.choerodon.hap.job.exception.JobException;
import io.choerodon.hap.job.service.IQuartzService;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.core.IRequest;
import io.choerodon.web.dto.ResponseData;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author shengyang.zhou@hand-china.com
 */
@Controller
@RequestMapping(value = {"/job", "/api/job"})
public class JobController extends BaseController {

    @Autowired
    private IQuartzService quartzService;

    /**
     * 新建一个JOB.
     *
     * @param jobCreateDto dto
     * @param result       result
     * @param request      HttpServletRequest
     * @return 新建结果
     * @throws SchedulerException     Base class for exceptions thrown by the Quartz Scheduler.
     * @throws JobException           JobException
     * @throws ClassNotFoundException ClassNotFoundException
     */
    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/create")
    public ResponseData createJob(@RequestBody JobCreateDto jobCreateDto, BindingResult result,
                                  HttpServletRequest request) throws SchedulerException, JobException, ClassNotFoundException, FieldRequiredException {
        jobCreateDto.setTriggerGroup(jobCreateDto.getJobGroup());
        jobCreateDto.setTriggerName(jobCreateDto.getJobName() + "_trigger");
        getValidator().validate(jobCreateDto, result);
        if (result.hasErrors()) {
            ResponseData rd = new ResponseData(false);
            rd.setMessage(getErrorMessage(result, request));
            return rd;
        }
        quartzService.createJob(jobCreateDto);
        return new ResponseData();
    }

    /**
     * 暂停job.
     *
     * @param list job列表
     * @return 结果
     * @throws SchedulerException Base class for exceptions thrown by the Quartz Scheduler.
     */
    @Permission(type = ResourceType.SITE)
    @PostMapping("/pause")
    public ResponseData pauseJobs(@RequestBody List<JobDetailDto> list) throws SchedulerException {
        quartzService.pauseJobs(list);
        return new ResponseData();
    }

    /**
     * 继续执行job.
     *
     * @param list job 列表
     * @return 结果
     * @throws SchedulerException Base class for exceptions thrown by the Quartz Scheduler.
     */
    @Permission(type = ResourceType.SITE)
    @PostMapping("/resume")
    public ResponseData resumeJobs(@RequestBody List<JobDetailDto> list) throws SchedulerException {
        quartzService.resumeJobs(list);
        return new ResponseData();
    }

    /**
     * 删除job.
     *
     * @param list job列表
     * @return 结果
     * @throws SchedulerException Base class for exceptions thrown by the Quartz Scheduler.
     */
    @Permission(type = ResourceType.SITE)
    @PostMapping("/deletejob")
    public ResponseData deleteJobs(@RequestBody List<JobDetailDto> list) throws SchedulerException {
        quartzService.deleteJobs(list);
        return new ResponseData();
    }

    /**
     * 暂停触发器
     *
     * @param list 触发器列表
     * @return 结果
     * @throws SchedulerException Base class for exceptions thrown by the Quartz Scheduler.
     */
    @Permission(type = ResourceType.SITE)
    @PostMapping("/pausetrigger")
    public ResponseData pauseTrigger(@RequestBody List<TriggerDto> list) throws SchedulerException {
        quartzService.pauseTriggers(list);
        return new ResponseData();
    }

    /**
     * 继续执行触发器.
     *
     * @param list 触发器列表
     * @return 结果
     * @throws SchedulerException Base class for exceptions thrown by the Quartz Scheduler.
     */
    @Permission(type = ResourceType.SITE)
    @PostMapping("/resumetrigger")
    public ResponseData resumeTrigger(@RequestBody List<TriggerDto> list) throws SchedulerException {
        quartzService.resumeTriggers(list);
        return new ResponseData();
    }

    /**
     * 查询job列表.
     *
     * @param example  查询参数
     * @param page     页码
     * @param pagesize 每页数量
     * @param request  HttpServletRequest
     * @return job结果列表
     * @throws SchedulerException Base class for exceptions thrown by the Quartz Scheduler.
     */
    @Permission(type = ResourceType.SITE)
    @PostMapping("/query")
    public ResponseData queryJobs(@ModelAttribute JobDetailDto example,
                                  @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                  @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request)
            throws SchedulerException {
        return qj(example, page, pagesize, request);
    }

    /**
     * 查询job列表.
     *
     * @param example  查询参数
     * @param page     页码
     * @param pagesize 每页数量
     * @param request  HttpServletRequest
     * @return job结果列表
     * @throws SchedulerException Base class for exceptions thrown by the Quartz Scheduler.
     */
    @Permission(type = ResourceType.SITE)
    @PostMapping("/queryInfo")
    public ResponseData query(@RequestBody JobDetailDto example, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request)
            throws SchedulerException {
        return qj(example, page, pagesize, request);
    }

    private ResponseData qj(JobDetailDto example, int page, int pagesize, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(quartzService.getJobInfoDetails(requestCtx, example, page, pagesize));
    }

    /**
     * 查询触发器.
     *
     * @param triggerName  name
     * @param triggerGroup group
     * @param triggerType  触发器类型
     * @return 触发器列表
     * @throws SchedulerException SchedulerException
     */
    @Permission(type = ResourceType.SITE)
    @PostMapping("/trigger")
    public ResponseData queryTrigger(@RequestParam(required = true) String triggerName,
                                     @RequestParam(required = true) String triggerGroup, @RequestParam(required = true) String triggerType)
            throws SchedulerException {
        if ("CRON".equalsIgnoreCase(triggerType)) {
            return new ResponseData(Collections.singletonList(quartzService.getCronTrigger(triggerName, triggerGroup)));
        }
        if ("SIMPLE".equalsIgnoreCase(triggerType)) {
            return new ResponseData(Collections.singletonList(quartzService.getSimpleTrigger(triggerName, triggerGroup)));
        }
        return new ResponseData();
    }

    /**
     * 查询触发器.
     *
     * @param example  参数
     * @param page     页码
     * @param pagesize 每页数量
     * @param request  HttpServletRequest
     * @return 触发器列表
     * @throws SchedulerException SchedulerException
     */
    @Permission(type = ResourceType.SITE)
    @PostMapping("/trigger/query")
    public ResponseData queryTriggers(@ModelAttribute TriggerDto example,
                                      @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request)
            throws SchedulerException {
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(quartzService.getTriggers(requestCtx, example, page, pagesize));
    }

    /**
     * 查询Scheduler.
     *
     * @param example  查询参数
     * @param page     页码
     * @param pagesize 每页数量
     * @return Scheduler信息
     * @throws SchedulerException SchedulerException
     * @deprecated
     */
    @Permission(type = ResourceType.SITE)
    @PostMapping("/scheduler/query")
    public ResponseData querySchedulers(@ModelAttribute SchedulerDto example,
                                        @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) throws SchedulerException {
        return new ResponseData(quartzService.selectSchedulers(example, page, pagesize));
    }

    /**
     * .
     *
     * @return scheduler信息
     * @throws SchedulerException Base class for exceptions thrown by the Quartz Scheduler.
     * @deprecated
     */
    @Permission(type = ResourceType.SITE)
    @GetMapping("/scheduler/info")
    public ResponseData schedulerInformation() throws SchedulerException {
        Map<String, Object> infoMap = quartzService.schedulerInformation();
        ResponseData responseData = new ResponseData();
        responseData.setRows(Collections.singletonList(infoMap));
        return responseData;
    }

    /**
     * 启动当前的Scheduler.
     *
     * @return Scheduler信息
     * @throws SchedulerException Scheduler异常
     * @deprecated
     */

    @Permission(type = ResourceType.SITE)
    @GetMapping("/scheduler/start")
    public ResponseData startScheduler() throws SchedulerException {
        return new ResponseData(Collections.singletonList(quartzService.start()));
    }

    /**
     * standby当前的Scheduler.
     *
     * @return Scheduler信息
     * @throws SchedulerException Scheduler异常
     * @deprecated
     */

    @Permission(type = ResourceType.SITE)
    @GetMapping("/scheduler/standby")
    public ResponseData standbyScheduler() throws SchedulerException {
        return new ResponseData(Collections.singletonList(quartzService.standby()));
    }

    /**
     * 暂停所有job.
     *
     * @return Scheduler信息
     * @throws SchedulerException Scheduler异常
     * @deprecated
     */

    @Permission(type = ResourceType.SITE)
    @GetMapping("/scheduler/pauseall")
    public ResponseData schedulerPauseAll() throws SchedulerException {
        return new ResponseData(Collections.singletonList(quartzService.pauseAll()));
    }

    /**
     * 继续所有job.
     *
     * @return Scheduler信息
     * @throws SchedulerException Scheduler异常
     * @deprecated
     */

    @Permission(type = ResourceType.SITE)
    @GetMapping("/scheduler/resumeall")
    public ResponseData schedulerResumeAll() throws SchedulerException {
        return new ResponseData(Collections.singletonList(quartzService.resumeAll()));
    }
}
