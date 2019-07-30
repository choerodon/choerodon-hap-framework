package io.choerodon.hap.task.service.impl;

import io.choerodon.hap.iam.infra.dto.RoleDTO;
import io.choerodon.hap.task.dto.TaskAssign;
import io.choerodon.hap.task.mapper.TaskAssignMapper;
import io.choerodon.hap.task.service.ITaskAssignService;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.mybatis.service.BaseServiceImpl;
import io.choerodon.web.core.IRequest;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static io.choerodon.hap.system.dto.DTOStatus.ADD;
import static io.choerodon.hap.system.dto.DTOStatus.DELETE;
import static io.choerodon.hap.system.dto.DTOStatus.UPDATE;

/**
 * 任务权限Service-实现类.
 *
 * @author lijian.yin@hand-china.com
 * @since 2017/11/15.
 **/

@Service
@Dataset("TaskAssign")
public class TaskAssignServiceImpl extends BaseServiceImpl<TaskAssign> implements ITaskAssignService, IDatasetService<TaskAssign> {

    @Autowired
    private TaskAssignMapper taskAssignMapper;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public List<TaskAssign> query(IRequest request, TaskAssign condition) {
        return taskAssignMapper.query(condition);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public List<RoleDTO> queryUnbound(IRequest request, List<String> idList) {
        return taskAssignMapper.queryUnbound(idList);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public List<Long> queryTaskId(IRequest iRequest, boolean isAdmin) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(date);
        try {
            date = sdf.parse(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return taskAssignMapper.queryTaskId(iRequest.getAllRoleId(), date, isAdmin);
    }

    @Override
    protected boolean useSelectiveUpdate() {
        return false;
    }

    @Override
    public List<TaskAssign> batchUpdate(List<TaskAssign> list) {
        list.forEach(assign -> {
            if (assign.getTaskAssignId() != null) {
                updateByPrimaryKey(assign);
            } else {
                insertSelective(assign);
            }
        });
        return list;
    }

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        TaskAssign taskAssign = new TaskAssign();
        try {
            BeanUtils.populate(taskAssign, body);
            return self().query(null, taskAssign);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new DatasetException("dataset.error", e);
        }
    }

    @Override
    public List<TaskAssign> mutations(List<TaskAssign> objs) {
        for (TaskAssign taskAssign : objs) {
            switch (taskAssign.get__status()) {
                case ADD:
                    self().insertSelective(taskAssign);
                    break;
                case UPDATE:
                    self().updateByPrimaryKey(taskAssign);
                    break;
                case DELETE:
                    self().deleteByPrimaryKey(taskAssign);
                    break;
                default:
                    break;
            }
        }
        return objs;
    }

}