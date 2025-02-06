package com.ruoyi.aihuman.task;

import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.ruoyi.aihuman.domain.AiHumanTask;
import com.ruoyi.aihuman.service.IAiHumanTaskService;
import org.springframework.beans.factory.annotation.Value;

/**
 * AI任务处理器
 * 
 * @author ruoyi
 */
@Component
public class TaskProcessor {
    private static final Logger log = LoggerFactory.getLogger(TaskProcessor.class);

    @Autowired
    private IAiHumanTaskService taskService;

    /**
     * 最大并发任务数
     */
    @Value("${ai-human.task.max-concurrent-tasks:5}")
    private int maxConcurrentTasks;

    /**
     * 每5秒执行一次任务处理
     */
    @Scheduled(fixedRate = 5000)
    public void processTask() {
        try {
            // 检查当前运行中的任务数量
            int runningTasks = getRunningTaskCount();
            System.out.println("max runningTasks:" + maxConcurrentTasks);
            if (runningTasks >= maxConcurrentTasks) {
                log.warn("当前运行任务数({})已达到最大限制({}), 暂停任务投递", runningTasks, maxConcurrentTasks);
                return;
            }

            // 获取待处理的任务
            AiHumanTask task = getNextTask();
            if (task == null) {
                log.debug("没有待处理的任务,休息一会!");
                return;
            }

            log.info("开始处理任务: taskId={}, taskName={}, taskType={}, 当前运行任务数={}",
                    task.getTaskId(), task.getTaskName(), task.getTaskType(), runningTasks + 1);

            // 更新任务状态为处理中
            task.setStatus("1");
            task.setProcessStartTime(new Date());
            taskService.updateAiHumanTask(task);
            log.info("任务状态更新为处理中: taskId={}", task.getTaskId());

            // 根据任务类型进行处理
            processTaskByType(task);

            // 更新任务状态为已完成
            task.setStatus("2");
            task.setProcessEndTime(new Date());
            taskService.updateAiHumanTask(task);
            log.info("任务处理完成: taskId={}, 耗时={}ms", task.getTaskId(),
                    task.getProcessEndTime().getTime() - task.getProcessStartTime().getTime());

        } catch (Exception e) {
            log.error("任务处理异常: {}", e.getMessage(), e);
        }
    }

    /**
     * 获取下一个待处理任务
     * 按优先级降序、提交时间升序排序
     */
    private AiHumanTask getNextTask() {
        AiHumanTask queryTask = new AiHumanTask();
        queryTask.setStatus("0"); // 待处理状态
        List<AiHumanTask> tasks = taskService.selectAiHumanTaskList(queryTask);

        if (tasks != null && !tasks.isEmpty()) {
            AiHumanTask nextTask = tasks.get(0);
            log.debug("获取到下一个待处理任务: taskId={}, priority={}", nextTask.getTaskId(), nextTask.getPriority());
            return nextTask;
        }
        return null;
    }

    /**
     * 根据任务类型处理任务
     */
    private void processTaskByType(AiHumanTask task) {
        try {
            String taskType = task.getTaskType();
            log.info("开始处理任务类型: taskId={}, taskType={}", task.getTaskId(), taskType);

            switch (taskType) {
                case "type1":
                    // 处理类型1的任务
                    processType1Task(task);
                    break;
                case "type2":
                    // 处理类型2的任务
                    processType2Task(task);
                    break;
                default:
                    log.warn("未知的任务类型: taskId={}, taskType={}", task.getTaskId(), taskType);
                    task.setStatus("3"); // 设置为失败状态
                    task.setErrorMessage("未知的任务类型");
                    taskService.updateAiHumanTask(task);
            }
        } catch (Exception e) {
            log.error("任务处理失败: taskId={}, error={}", task.getTaskId(), e.getMessage(), e);
            task.setStatus("3"); // 设置为失败状态
            task.setErrorMessage(e.getMessage());
            taskService.updateAiHumanTask(task);
        }
    }

    /**
     * 处理类型1任务
     */
    private void processType1Task(AiHumanTask task) {
        log.info("处理类型1任务: taskId={}", task.getTaskId());
        // 实现类型1任务的具体处理逻辑
    }

    /**
     * 处理类型2任务
     */
    private void processType2Task(AiHumanTask task) {
        log.info("处理类型2任务: taskId={}", task.getTaskId());
        // 实现类型2任务的具体处理逻辑
    }

    /**
     * 获取当前运行中的任务数量
     */
    private int getRunningTaskCount() {
        AiHumanTask queryTask = new AiHumanTask();
        queryTask.setStatus("1"); // 处理中状态
        List<AiHumanTask> runningTasks = taskService.selectAiHumanTaskList(queryTask);
        return runningTasks != null ? runningTasks.size() : 0;
    }

}