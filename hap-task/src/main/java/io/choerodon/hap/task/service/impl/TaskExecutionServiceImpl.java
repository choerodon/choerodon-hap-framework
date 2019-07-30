package io.choerodon.hap.task.service.impl;

import com.github.pagehelper.PageHelper;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.message.components.TaskCancel;
import io.choerodon.hap.task.ExecuteLogConvertStrategy;
import io.choerodon.hap.task.TaskConstants;
import io.choerodon.hap.task.dto.TaskExecution;
import io.choerodon.hap.task.mapper.TaskExecutionMapper;
import io.choerodon.hap.task.service.ITaskExecutionService;
import io.choerodon.message.IMessagePublisher;
import io.choerodon.mybatis.service.BaseServiceImpl;
import io.choerodon.web.core.IRequest;
import io.choerodon.web.core.impl.RequestHelper;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 任务执行记录service - 实现类.
 *
 * @author lijian.yin@hand-china.com
 */

@Service
@Dataset("TaskExecution")
public class TaskExecutionServiceImpl extends BaseServiceImpl<TaskExecution>
        implements ITaskExecutionService, IDatasetService<TaskExecution> {

    @Autowired
    private TaskExecutionMapper taskExecutionMapper;

    @Autowired
    private IMessagePublisher messagePublisher;

    @Value("${task.execute.logConvert.class:io.choerodon.hap.task.service.impl.DefaultExecuteLogConvert}")
    private String logConvertClass;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public List<TaskExecution> queryExecutions(IRequest iRequest, TaskExecution dto, boolean isAdmin, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<TaskExecution> taskExecutions = taskExecutionMapper.queryExecutions(dto, isAdmin);
        for (TaskExecution taskExecution : taskExecutions) {
            taskExecution.setName(taskExecution.getTaskDetail().getName());
            taskExecution.setType(taskExecution.getTaskDetail().getType());
        }
        return taskExecutions;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public List<TaskExecution> queryExecutionGroup(IRequest iRequest, TaskExecution taskExecution) {
        List<TaskExecution> taskExecutionList = taskExecutionMapper.queryExecutionGroup(taskExecution);
        for (TaskExecution taskExecutionGroup : taskExecutionList) {
            taskExecutionGroup.setName(taskExecutionGroup.getTaskDetail().getName());
            taskExecutionGroup.setType(taskExecutionGroup.getTaskDetail().getType());
        }
        return taskExecutionList;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public List<TaskExecution> queryExecutionDetail(IRequest iRequest, TaskExecution taskExecution) {
        return taskExecutionMapper.queryExecutionDetail(taskExecution);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertExecution(IRequest iRequest, TaskExecution taskExecution) {
        taskExecution.setUserId(iRequest.getUserId());
        taskExecution.setStatus(TaskConstants.EXECUTION_READY);
        taskExecutionMapper.insertSelective(taskExecution);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void updateStatus(Long executionId, String status) {
        taskExecutionMapper.updateStatus(executionId, status);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void batchUpdateStatus(Long executionId, String befStatus, String aftStatus) {
        taskExecutionMapper.batchUpdateStatus(executionId, befStatus, aftStatus);
    }

    @Override
    public boolean cancelExecute(TaskExecution dto) {
        messagePublisher.publish(TaskCancel.TASK_CANCEL, dto);
        return true;
    }

    @Override
    public String generateString(TaskExecution taskExecution) {
        String logString = "";
        try {
            ExecuteLogConvertStrategy executeLogConvertStrategy = (ExecuteLogConvertStrategy) Class.forName(logConvertClass).newInstance();
            logString = executeLogConvertStrategy.convertLog(taskExecution);
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute log download!", e);
        }
        return logString;

    }

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            TaskExecution taskExecution = new TaskExecution();
            IRequest iRequest = RequestHelper.getCurrentRequest();
            BeanUtils.populate(taskExecution, body);
            taskExecution.setSortname(sortname);
            taskExecution.setSortorder(isDesc ? "desc" : "asc");
            return this.queryExecutions(iRequest, taskExecution, false, page, pageSize);
        } catch (Exception e) {
            throw new DatasetException("dataset:error", e);
        }

    }

    @Override
    public List<TaskExecution> mutations(List<TaskExecution> objs) {
        return null;
    }
}