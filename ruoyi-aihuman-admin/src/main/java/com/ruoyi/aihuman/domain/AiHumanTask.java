package com.ruoyi.aihuman.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 任务管理对象 ai_human_task
 * 
 * @author ruoyi
 * @date 2025-02-06
 */
public class AiHumanTask extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 任务ID */
    private Long taskId;

    /** 任务名称 */
    @Excel(name = "任务名称")
    private String taskName;

    /** 任务类型 */
    @Excel(name = "任务类型")
    private String taskType;

    /** 任务状态（0待处理 1处理中 2已完成 3失败） */
    @Excel(name = "任务状态", readConverterExp = "0=待处理,1=处理中,2=已完成,3=失败")
    private String status;

    /** 优先级（1-10，值越大优先级越高） */
    @Excel(name = "优先级", readConverterExp = "1=-10，值越大优先级越高")
    private Long priority;

    /** 提交时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "提交时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date submitTime;

    /** 开始处理时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "开始处理时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date processStartTime;

    /** 处理完成时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "处理完成时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date processEndTime;

    /** 处理结果 */
    @Excel(name = "处理结果")
    private String result;

    /** 失败原因 */
    @Excel(name = "失败原因")
    private String errorMessage;

    /** 用户标识 */
    @Excel(name = "用户标识")
    private String userId;

    /** 客户端标识 */
    @Excel(name = "客户端标识")
    private String clientId;

    public void setTaskId(Long taskId) 
    {
        this.taskId = taskId;
    }

    public Long getTaskId() 
    {
        return taskId;
    }
    public void setTaskName(String taskName) 
    {
        this.taskName = taskName;
    }

    public String getTaskName() 
    {
        return taskName;
    }
    public void setTaskType(String taskType) 
    {
        this.taskType = taskType;
    }

    public String getTaskType() 
    {
        return taskType;
    }
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }
    public void setPriority(Long priority) 
    {
        this.priority = priority;
    }

    public Long getPriority() 
    {
        return priority;
    }
    public void setSubmitTime(Date submitTime) 
    {
        this.submitTime = submitTime;
    }

    public Date getSubmitTime() 
    {
        return submitTime;
    }
    public void setProcessStartTime(Date processStartTime) 
    {
        this.processStartTime = processStartTime;
    }

    public Date getProcessStartTime() 
    {
        return processStartTime;
    }
    public void setProcessEndTime(Date processEndTime) 
    {
        this.processEndTime = processEndTime;
    }

    public Date getProcessEndTime() 
    {
        return processEndTime;
    }
    public void setResult(String result) 
    {
        this.result = result;
    }

    public String getResult() 
    {
        return result;
    }
    public void setErrorMessage(String errorMessage) 
    {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() 
    {
        return errorMessage;
    }
    public void setUserId(String userId) 
    {
        this.userId = userId;
    }

    public String getUserId() 
    {
        return userId;
    }
    public void setClientId(String clientId) 
    {
        this.clientId = clientId;
    }

    public String getClientId() 
    {
        return clientId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("taskId", getTaskId())
            .append("taskName", getTaskName())
            .append("taskType", getTaskType())
            .append("status", getStatus())
            .append("priority", getPriority())
            .append("submitTime", getSubmitTime())
            .append("processStartTime", getProcessStartTime())
            .append("processEndTime", getProcessEndTime())
            .append("result", getResult())
            .append("errorMessage", getErrorMessage())
            .append("userId", getUserId())
            .append("clientId", getClientId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
