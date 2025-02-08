package com.ruoyi.aihuman.task;

import java.util.Date;
import java.util.List;

import com.ruoyi.aihuman.enums.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
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
     * 每2秒执行一次任务处理
     */
//    @Scheduled(fixedRate = 2000)
//    @Async
    public void processTask() {
        AiHumanTask task = null;
        try {
            // 检查当前运行中的任务数量
            int runningTasks = getRunningTaskCount();
            System.out.println("max runningTasks:" + maxConcurrentTasks);
            if (runningTasks >= maxConcurrentTasks) {
                log.warn("当前运行任务数({})已达到最大限制({}), 暂停任务投递", runningTasks, maxConcurrentTasks);
                return;
            }

            // 获取待处理的任务
            task = getNextTask();
            if (task == null) {
                log.debug("没有待处理的任务,休息一会!");
                return;
            }

            log.info("开始处理任务: taskId={}, taskName={}, 当前运行任务数={}",
                    task.getTaskId(), task.getTaskName(), runningTasks + 1);

            // 更新任务状态为处理中
            task.setStatus(TaskStatus.setStatus(TaskStatus.PROCESSING));
            task.setProcessStartTime(new Date());
            taskService.updateAiHumanTask(task);
            log.info("任务状态更新为处理中: taskId={}", task.getTaskId());

            // 根据任务类型进行处理
            doProcessTask(task);

            // 更新任务状态为已完成
            task.setStatus(TaskStatus.setStatus(TaskStatus.COMPLETED));
            task.setProcessEndTime(new Date());
            taskService.updateAiHumanTask(task);
            log.info("任务处理完成: taskId={}, 耗时={}ms", task.getTaskId(),
                    task.getProcessEndTime().getTime() - task.getProcessStartTime().getTime());

        } catch (Exception e) {
            log.error("任务处理失败: error={}", e.getMessage(), e);
            if (task != null) {
                task.setStatus(TaskStatus.setStatus(TaskStatus.FAILED));
                task.setErrorMessage(e.getMessage());
                task.setProcessEndTime(new Date());
                taskService.updateAiHumanTask(task);
                log.error("任务处理失败: taskId={}, error={}", task.getTaskId(), e.getMessage(), e);
            }
        }
    }

    /**
     * 获取下一个待处理任务
     * 按优先级降序、提交时间升序排序
     */
    private AiHumanTask getNextTask() {
        AiHumanTask queryTask = new AiHumanTask();
        queryTask.setStatus(TaskStatus.setStatus(TaskStatus.PENDING)); // 待处理状态
        queryTask.getParams().put("orderBy", "order by priority desc, submit_time asc");
        List<AiHumanTask> tasks = taskService.selectAiHumanTaskList(queryTask);

        if (tasks != null && !tasks.isEmpty()) {
            AiHumanTask nextTask = tasks.get(0);
            log.debug("获取到下一个待处理任务: taskId={}, priority={}", nextTask.getTaskId(), nextTask.getPriority());
            return nextTask;
        }
        return null;
    }


    /**
     * 处理任务，提交成功后更新任务状态为处理中
     */
    private void doProcessTask(AiHumanTask task) throws InterruptedException {
        try {
            // 根据任务类型进行处理
            //todo: 任务处理逻辑
            Thread.sleep(5000);
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * 获取当前运行中的任务数量
     */
    private int getRunningTaskCount() {
        AiHumanTask queryTask = new AiHumanTask();
        queryTask.setStatus(TaskStatus.setStatus(TaskStatus.PROCESSING)); // 处理中状态
        List<AiHumanTask> runningTasks = taskService.selectAiHumanTaskList(queryTask);
        return runningTasks != null ? runningTasks.size() : 0;
    }

}