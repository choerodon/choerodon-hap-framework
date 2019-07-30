package io.choerodon.hap.task.service.impl;

import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.task.dto.TaskExecution;
import io.choerodon.mybatis.service.BaseServiceImpl;
import io.choerodon.hap.task.service.ITaskExecutionService;
import io.choerodon.web.core.IRequest;
import io.choerodon.web.core.impl.RequestHelper;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author jiameng.cao
 * @since 2019/1/3
 */
@Service
@Dataset("ExecutionAdmin")
public class TaskExecutionAdminServiceImpl extends BaseServiceImpl<TaskExecution> implements IDatasetService<TaskExecution> {

    @Autowired
    private ITaskExecutionService iTaskExecutionService;

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            TaskExecution taskExecution = new TaskExecution();
            IRequest iRequest = RequestHelper.getCurrentRequest();
            BeanUtils.populate(taskExecution, body);
            taskExecution.setSortname(sortname);
            taskExecution.setSortorder(isDesc ? "desc" : "asc");
            return iTaskExecutionService.queryExecutions(iRequest, taskExecution, true, page, pageSize);
        } catch (Exception e) {
            throw new DatasetException("dataset:error", e);
        }
    }

    @Override
    public List<TaskExecution> mutations(List<TaskExecution> objs) {
        return null;
    }

}
