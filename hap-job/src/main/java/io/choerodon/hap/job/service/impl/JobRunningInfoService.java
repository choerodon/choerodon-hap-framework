package io.choerodon.hap.job.service.impl;

import com.github.pagehelper.PageHelper;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.job.dto.JobRunningInfoDto;
import io.choerodon.hap.job.mapper.JobRunningInfoDtoMapper;
import io.choerodon.hap.job.service.IJobRunningInfoService;
import io.choerodon.web.core.IRequest;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * @author shiliyan
 */
@Service
@Transactional(rollbackFor = Exception.class)
@Dataset("JobRunningInfo")
public class JobRunningInfoService implements IJobRunningInfoService, IDatasetService<JobRunningInfoDto> {

    @Autowired
    private JobRunningInfoDtoMapper jobRunningInfoDtoMapper;

    @Override
    public List<JobRunningInfoDto> queryJobRunningInfo(IRequest request, JobRunningInfoDto example, int page,
                                                       int pagesize) {
        PageHelper.startPage(page, pagesize);
        return jobRunningInfoDtoMapper.select(example);
    }

    @Override
    public void createJobRunningInfo(JobRunningInfoDto jobCreateDto) {
        jobRunningInfoDtoMapper.insertSelective(jobCreateDto);
    }

    @Override
    public void delete(JobRunningInfoDto jobCreateDto) {
        jobRunningInfoDtoMapper.deleteByNameGroup(jobCreateDto);
    }

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize,String sortname, boolean isDesc) {
        JobRunningInfoDto runningInfoDto = new JobRunningInfoDto();
        try {
            BeanUtils.populate(runningInfoDto, body);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        String sortorder = isDesc ? "desc" : "asc";
        if (sortname == null) {
            sortname = "scheduledFireTime";
            sortorder = "desc";
        }
        runningInfoDto.setSortname(sortname);
        runningInfoDto.setSortorder(sortorder);
        PageHelper.startPage(page, pageSize);
        return jobRunningInfoDtoMapper.select(runningInfoDto);
    }

    @Override
    public List<JobRunningInfoDto> mutations(List<JobRunningInfoDto> objs) {
        return null;
    }
}
