package io.choerodon.hap.job.controllers;

import io.choerodon.hap.job.dto.JobRunningInfoDto;
import io.choerodon.hap.job.service.IJobRunningInfoService;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.core.IRequest;
import io.choerodon.web.dto.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author liyan.shi@hand-china.com
 */
@RestController
@RequestMapping(value = {"/job/jobinfo", "/api/job/jobinfo"})
public class JobInfoController extends BaseController {
    @Autowired
    private IJobRunningInfoService jobRunningInfoService;

    /**
     * 查询Job运行记录.
     *
     * @param dto      参数
     * @param page     页码
     * @param pagesize 每页数量
     * @param request  HttpServletRequest
     * @return 运行记录结果
     */
    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/query")
    public ResponseData queryJobRunningInfo(JobRunningInfoDto dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(jobRunningInfoService.queryJobRunningInfo(requestCtx, dto, page, pagesize));
    }

}
