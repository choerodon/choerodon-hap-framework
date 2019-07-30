package io.choerodon.hap.task.info;

import java.util.List;

/**
 * 数据传输类-任务/任务组执行.
 *
 * @author lijian.yin@hand-china.com
 * @since 2017/11/7.
 **/
public class TaskDataInfo {

    private Long executionId;

    private Long taskId;

    private String taskClass;

    private String description;

    private String type;

    /**
     * 执行人
     */
    private String username;

    private List<TaskDataInfo> taskDatas;

    private List<ParameterInfo> param;

    /**
     * 任务组  当前执行的子任务
     */
    private TaskDataInfo currentTask;

    /**
     * 任务组  当前执行的子任务序号
     */
    private int currentExecution;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getExecutionId() {
        return executionId;
    }

    public void setExecutionId(Long executionId) {
        this.executionId = executionId;
    }

    public List<TaskDataInfo> getTaskDatas() {
        return taskDatas;
    }

    public void setTaskDatas(List<TaskDataInfo> taskDatas) {
        this.taskDatas = taskDatas;
    }

    public TaskDataInfo getCurrentTask() {
        return currentTask;
    }

    public void setCurrentTask(TaskDataInfo currentTask) {
        this.currentTask = currentTask;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTaskClass() {
        return taskClass;
    }

    public void setTaskClass(String taskClass) {
        this.taskClass = taskClass;
    }

    public List<ParameterInfo> getParam() {
        return param;
    }

    public void setParam(List<ParameterInfo> param) {
        this.param = param;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getCurrentExecution() {
        return currentExecution;
    }

    public void setCurrentExecution(int currentExecution) {
        this.currentExecution = currentExecution;
    }
}
