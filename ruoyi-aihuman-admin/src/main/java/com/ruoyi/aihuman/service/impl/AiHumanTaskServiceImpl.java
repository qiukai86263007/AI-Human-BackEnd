package com.ruoyi.aihuman.service.impl;

import java.util.Collections;
import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.aihuman.mapper.AiHumanTaskMapper;
import com.ruoyi.aihuman.domain.AiHumanTask;
import com.ruoyi.aihuman.service.IAiHumanTaskService;
import java.util.Map;

/**
 * 任务管理Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-02-07
 */
@Service
public class AiHumanTaskServiceImpl implements IAiHumanTaskService 
{
    @Autowired
    private AiHumanTaskMapper aiHumanTaskMapper;

    /**
     * 查询任务管理
     * 
     * @param taskId 任务管理主键
     * @return 任务管理
     */
    @Override
    public AiHumanTask selectAiHumanTaskByTaskId(Long taskId)
    {
        return aiHumanTaskMapper.selectAiHumanTaskByTaskId(taskId);
    }

    /**
     * 查询任务管理列表
     * 
     * @param aiHumanTask 任务管理
     * @return 任务管理
     */
    @Override
    public List<AiHumanTask> selectAiHumanTaskList(AiHumanTask aiHumanTask)
    {
        return aiHumanTaskMapper.selectAiHumanTaskList(aiHumanTask);
    }

    /**
     * 新增任务管理
     * 
     * @param aiHumanTask 任务管理
     * @return 结果
     */
    @Override
    public int insertAiHumanTask(AiHumanTask aiHumanTask)
    {
        aiHumanTask.setCreateTime(DateUtils.getNowDate());
        return aiHumanTaskMapper.insertAiHumanTask(aiHumanTask);
    }

    /**
     * 修改任务管理
     * 
     * @param aiHumanTask 任务管理
     * @return 结果
     */
    @Override
    public int updateAiHumanTask(AiHumanTask aiHumanTask)
    {
        aiHumanTask.setUpdateTime(DateUtils.getNowDate());
        return aiHumanTaskMapper.updateAiHumanTask(aiHumanTask);
    }

    /**
     * 批量删除任务管理
     * 
     * @param taskIds 需要删除的任务管理主键
     * @return 结果
     */
    @Override
    public int deleteAiHumanTaskByTaskIds(Long[] taskIds)
    {
        return aiHumanTaskMapper.deleteAiHumanTaskByTaskIds(taskIds);
    }

    /**
     * 删除任务管理信息
     * 
     * @param taskId 任务管理主键
     * @return 结果
     */
    @Override
    public int deleteAiHumanTaskByTaskId(Long taskId)
    {
        return aiHumanTaskMapper.deleteAiHumanTaskByTaskId(taskId);
    }

    @Override
    public Map<String, Integer> countTasksByParentTaskIdAndStatus(String parentTaskId) {
        return aiHumanTaskMapper.countTasksByParentTaskIdAndStatus(parentTaskId);
    }

    @Override
    public List<String> getMP4FilesByParentTaskId(String parentTaskId) {
        // 根据 parentTaskId 查询所有子任务的 MP4 文件路径
        // 这里需要根据实际的数据库表结构和业务逻辑实现查询逻辑
        // 示例代码如下，假设使用 MyBatis 进行数据库操作
        return aiHumanTaskMapper.getMP4FilesByParentTaskId(parentTaskId);
    }
}
