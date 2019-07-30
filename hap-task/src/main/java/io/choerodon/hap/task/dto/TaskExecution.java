package io.choerodon.hap.task.dto;


import io.choerodon.mybatis.entity.BaseDTO;
import org.hibernate.validator.constraints.Length;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * 任务执行记录 DTO.
 *
 * @author peng.jiang@hand-china.com
 * @since 2017/11/16.
 **/
@Table(name = "sys_task_execution")
public class TaskExecution extends BaseDTO {

    public static final String FIELD_EXECUTION_ID = "executionId";
    public static final String FIELD_EXECUTION_NUMBER = "executionNumber";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_PARAMETER = "parameter";
    public static final String FIELD_TASK_ID = "taskId";
    public static final String FIELD_STATUS = "status";
    public static final String FIELD_USER_ID = "userId";
    public static final String FIELD_START_TIME = "startTime";
    public static final String FIELD_END_TIME = "endTime";
    public static final String FIELD_PARENT_ID = "parentId";
    public static final String FIELD_EXECUTION_ORDER = "executionOrder";
    public static final String FIELD_LAST_EXECUTE_DATE = "lastExecuteDate";
    public static final String FIELD_EXECUTE_RESULT_PATH = "executeResultPath";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long executionId;

    // 执行编号
    @Length(max = 200)
    private String executionNumber;

    // 描述
    @Length(max = 255)
    private String executionDescription;

    // 任务ID
    @NotNull
    private Long taskId;

    // 执行状态
    @NotEmpty
    @Length(max = 10)
    private String status;

    // 提交人
    @NotEmpty
    @Length(max = 20)
    private Long userId;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 父id
     */
    private Long parentId;

    /**
     * 执行顺序
     */
    private Long executionOrder;

    @Transient
    private TaskDetail taskDetail;

    @Transient
    private TaskExecutionDetail taskExecutionDetail;

    @Transient
    private List<Long> taskIds;

    @Transient
    private String name;

    @Transient
    private String type;

    /**
     * 上次执行时间
     */
    private Date lastExecuteDate;

    private String executeResultPath;

    public void setExecutionId(Long executionId) {
        this.executionId = executionId;
    }

    public Long getExecutionId() {
        return executionId;
    }

    public String getExecutionNumber() {
        return executionNumber;
    }

    public void setExecutionNumber(String executionNumber) {
        this.executionNumber = executionNumber;
    }

    public String getExecutionDescription() {
        return executionDescription;
    }

    public void setExecutionDescription(String executionDescription) {
        this.executionDescription = executionDescription;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setExecutionOrder(Long executionOrder) {
        this.executionOrder = executionOrder;
    }

    public Long getExecutionOrder() {
        return executionOrder;
    }

    public TaskDetail getTaskDetail() {
        return taskDetail;
    }

    public void setTaskDetail(TaskDetail taskDetail) {
        this.taskDetail = taskDetail;
    }

    public TaskExecutionDetail getTaskExecutionDetail() {
        return taskExecutionDetail;
    }

    public void setTaskExecutionDetail(TaskExecutionDetail taskExecutionDetail) {
        this.taskExecutionDetail = taskExecutionDetail;
    }

    public List<Long> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(List<Long> taskIds) {
        this.taskIds = taskIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getLastExecuteDate() {
        return lastExecuteDate;
    }

    public void setLastExecuteDate(Date lastExecuteDate) {
        this.lastExecuteDate = lastExecuteDate;
    }

    public String getExecuteResultPath() {
        return executeResultPath;
    }

    public void setExecuteResultPath(String executeResultPath) {
        this.executeResultPath = executeResultPath;
    }
}
