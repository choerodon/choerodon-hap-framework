package io.choerodon.hap.task.service.impl;

import io.choerodon.hap.core.annotation.StdWho;
import io.choerodon.hap.task.dto.TaskExecutionDetail;
import io.choerodon.hap.task.mapper.TaskExecutionDetailMapper;
import io.choerodon.mybatis.service.BaseServiceImpl;
import io.choerodon.hap.task.service.ITaskExecutionDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 任务/任务组执行记录详情 Service - 实现类.
 *
 * @author peng.jiang@hand-china.com
 * @since 2017/11/16.
 **/

@Service
public class TaskExecutionDetailServiceImpl extends BaseServiceImpl<TaskExecutionDetail> implements ITaskExecutionDetailService {

    @Autowired
    private TaskExecutionDetailMapper taskExecutionDetailMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void updateStacktrace(@StdWho TaskExecutionDetail taskExecutionDetail) {
        taskExecutionDetailMapper.updateStacktrace(taskExecutionDetail);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateExecuteLog(@StdWho TaskExecutionDetail taskExecutionDetail) {
        taskExecutionDetailMapper.updateExecuteLog(taskExecutionDetail);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public TaskExecutionDetail getExecutionLog(TaskExecutionDetail taskExecutionDetail) {
        return taskExecutionDetailMapper.getExecutionLog(taskExecutionDetail);
    }

    @Override
    public TaskExecutionDetail selectByExecutionId(Long executionId) {
        return taskExecutionDetailMapper.selectByExecutionId(executionId);
    }
}