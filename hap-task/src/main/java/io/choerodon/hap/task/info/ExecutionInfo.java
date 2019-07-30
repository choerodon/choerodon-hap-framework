package io.choerodon.hap.task.info;

import io.choerodon.hap.task.dto.TaskDetail;

import java.util.Map;

/**
 * 数据传输-任务执行记录.
 *
 * @author peng.jiang@hand-china.com
 * @since 2017/11/16.
 **/

public class ExecutionInfo {

    /**
     * 执行任务
     */
    private TaskDetail taskDetail;

    /**
     * 执行人
     */
    private String username;

    /**
     * 执行参数列表
     */
    private Map<String, Object> param;

    /**
     * 执行结果路径
     */
    private String executeResultPath;

    public Map<String, Object> getParam() {
        return param;
    }

    public void setParam(Map<String, Object> param) {
        this.param = param;
    }

    public TaskDetail getTaskDetail() {
        return taskDetail;
    }

    public void setTaskDetail(TaskDetail taskDetail) {
        this.taskDetail = taskDetail;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getExecuteResultPath() {
        return executeResultPath;
    }

    public void setExecuteResultPath(String executeResultPath) {
        this.executeResultPath = executeResultPath;
    }
}
