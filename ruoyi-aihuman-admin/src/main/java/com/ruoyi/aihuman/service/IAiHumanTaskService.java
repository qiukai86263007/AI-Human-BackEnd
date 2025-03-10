package com.ruoyi.aihuman.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.aihuman.domain.AiHumanTask;

/**
 * 任务管理Service接口
 * 
 * @author ruoyi
 * @date 2025-02-07
 */
public interface IAiHumanTaskService
{
    /**
     * 查询任务管理
     * 
     * @param taskId 任务管理主键
     * @return 任务管理
     */
    public AiHumanTask selectAiHumanTaskByTaskId(Long taskId);

    /**
     * 查询任务管理列表
     * 
     * @param aiHumanTask 任务管理
     * @return 任务管理集合
     */
    public List<AiHumanTask> selectAiHumanTaskList(AiHumanTask aiHumanTask);

    /**
     * 新增任务管理
     * 
     * @param aiHumanTask 任务管理
     * @return 结果
     */
    public int insertAiHumanTask(AiHumanTask aiHumanTask);

    /**
     * 修改任务管理
     * 
     * @param aiHumanTask 任务管理
     * @return 结果
     */
    public int updateAiHumanTask(AiHumanTask aiHumanTask);

    /**
     * 批量删除任务管理
     * 
     * @param taskIds 需要删除的任务管理主键集合
     * @return 结果
     */
    public int deleteAiHumanTaskByTaskIds(Long[] taskIds);

    /**
     * 删除任务管理信息
     * 
     * @param taskId 任务管理主键
     * @return 结果
     */
    public int deleteAiHumanTaskByTaskId(Long taskId);

    /**
     * 按parentTaskId统计不同状态的任务数量
     *
     * @param parentTaskId 父任务ID
     * @return 包含不同状态任务数量的Map
     */
    Map<String, Integer> countTasksByParentTaskIdAndStatus(String parentTaskId);
}
