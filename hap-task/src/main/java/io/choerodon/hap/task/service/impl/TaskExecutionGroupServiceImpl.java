package io.choerodon.hap.task.service.impl;

import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.task.dto.TaskExecution;
import io.choerodon.mybatis.service.BaseServiceImpl;
import io.choerodon.hap.task.service.ITaskExecutionService;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author jiameng.cao
 * @since 2019/1/9
 */

@Service
@Dataset("ExecutionGroup")
public class TaskExecutionGroupServiceImpl extends BaseServiceImpl<TaskExecution> implements IDatasetService<TaskExecution> {
    @Autowired
    private ITaskExecutionService iTaskExecutionService;

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            TaskExecution taskExecution = new TaskExecution();
            BeanUtils.populate(taskExecution, body);
            return iTaskExecutionService.queryExecutionGroup(null, taskExecution);
        } catch (Exception e) {
            throw new DatasetException("dataset:error", e);
        }
    }

    @Override
    public List<TaskExecution> mutations(List<TaskExecution> objs) {
        return null;
    }
}
