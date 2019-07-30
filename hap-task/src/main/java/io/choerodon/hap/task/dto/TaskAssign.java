package io.choerodon.hap.task.dto;

import io.choerodon.hap.iam.infra.dto.RoleDTO;
import io.choerodon.mybatis.common.query.Comparison;
import io.choerodon.mybatis.common.query.Where;
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

/**
 * 任务DTO.
 *
 * @author lijian.yin@hand-china.com
 * @since 2017/11/15.
 **/
@Table(name = "sys_task_assign")
public class TaskAssign extends BaseDTO {

    public static final String FIELD_TASK_ASSIGN_ID = "taskAssignId";
    public static final String FIELD_TASK_ID = "taskId";
    public static final String FIELD_ASSIGN_ID = "assignId";
    public static final String FIELD_ASSIGN_TYPE = "assignType";
    public static final String FIELD_START_DATE = "startDate";
    public static final String FIELD_END_DATE = "endDate";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskAssignId;

    // 任务ID
    @NotNull
    private Long taskId;

    // 目标ID
    @NotNull
    private Long assignId;

    // 类型
    @NotEmpty
    @Length(max = 10)
    private String assignType;

    @Where(comparison = Comparison.GREATER_THAN_OR_EQUALTO)
    private Date startDate;

    @Where(comparison = Comparison.LESS_THAN_OR_EQUALTO)
    private Date endDate;

    //角色信息
    @Transient
    private RoleDTO role;

    public void setTaskAssignId(Long taskAssignId) {
        this.taskAssignId = taskAssignId;
    }

    public Long getTaskAssignId() {
        return taskAssignId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setAssignId(Long assignId) {
        this.assignId = assignId;
    }

    public Long getAssignId() {
        return assignId;
    }

    public void setAssignType(String assignType) {
        this.assignType = assignType;
    }

    public String getAssignType() {
        return assignType;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public RoleDTO getRole() {
        return role;
    }

    public void setRole(RoleDTO role) {
        this.role = role;
    }
}
