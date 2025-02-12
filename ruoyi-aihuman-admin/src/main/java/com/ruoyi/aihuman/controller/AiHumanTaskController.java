package com.ruoyi.aihuman.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Date;
import java.util.UUID;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.aihuman.domain.AiHumanTask;
import com.ruoyi.aihuman.service.IAiHumanTaskService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import com.ruoyi.aihuman.enums.TaskStatus;
import org.springframework.web.multipart.MultipartFile;

/**
 * 任务管理Controller
 *
 * @author ruoyi
 * @date 2025-02-06
 */
@Api(tags = "任务管理接口")
@RestController
@RequestMapping("/aihuman/task")
public class AiHumanTaskController extends BaseController
{

    private static final Logger log = LoggerFactory.getLogger(AiHumanTaskController.class);

    @Autowired
    private IAiHumanTaskService aiHumanTaskService;

    @Value("${ai-human.task.upload-path}")
    private String uploadPath;

    /**
     * 查询任务管理列表
     */
    @ApiOperation(value = "获取任务管理列表", notes = "获取所有任务管理信息")
    @PreAuthorize("@ss.hasPermi('aihuman:task:list')")
    @GetMapping("/list")
    public TableDataInfo list(AiHumanTask aiHumanTask)
    {
        startPage();
        List<AiHumanTask> list = aiHumanTaskService.selectAiHumanTaskList(aiHumanTask);
        return getDataTable(list);
    }

    /**
     * 导出任务管理列表
     */
    @ApiOperation(value = "导出任务管理", notes = "导出所有任务管理信息")
    @PreAuthorize("@ss.hasPermi('aihuman:task:export')")
    @Log(title = "任务管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AiHumanTask aiHumanTask)
    {
        List<AiHumanTask> list = aiHumanTaskService.selectAiHumanTaskList(aiHumanTask);
        ExcelUtil<AiHumanTask> util = new ExcelUtil<AiHumanTask>(AiHumanTask.class);
        util.exportExcel(response, list, "任务管理数据");
    }

    /**
     * 获取任务管理详细信息
     */
    @ApiOperation(value = "获取任务详细信息", notes = "根据任务ID获取任务详细信息")
    @PreAuthorize("@ss.hasPermi('aihuman:task:query')")
    @GetMapping(value = "/{taskId}")
    public AjaxResult getInfo(@ApiParam(value = "任务ID", required = true) @PathVariable("taskId") Long taskId)
    {
        return success(aiHumanTaskService.selectAiHumanTaskByTaskId(taskId));
    }

    /**
     * 新增任务管理
     */
    @ApiOperation(value = "新增任务", notes = "新增任务管理信息")
    @PreAuthorize("@ss.hasPermi('aihuman:task:add')")
    @Log(title = "任务管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@ApiParam(value = "任务信息", required = true) @RequestBody AiHumanTask aiHumanTask)
    {
        return toAjax(aiHumanTaskService.insertAiHumanTask(aiHumanTask));
    }

    /**
     * 提交任务,绕过web鉴权，单独鉴权
     */

    // todo: 将AiHumanTask所需字段放在Header中吧
    @ApiOperation(value = "提交任务", notes = "提交新的任务，包含任务信息和音频文件")
    @Log(title = "任务管理", businessType = BusinessType.INSERT)
    @PostMapping(value = "/anonymous/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AjaxResult submit(@ApiParam(value = "任务素材ID", required = true) @RequestParam("materialId") String materialId,
                             @ApiParam(value = "父任务ID", required = true) @RequestParam("parentTaskId") String parentTaskId,
                             @ApiParam(value = "用户ID", required = true) @RequestParam("userId") String userId,
                             @ApiParam(value = "客户端ID", required = true) @RequestParam("clientId") String clientId,
                           @ApiParam(value = "音频文件(.wav格式)", required = true) @RequestPart("file") MultipartFile file) {
        AiHumanTask aiHumanTask = new AiHumanTask();
        try {
            String fileName = file.getOriginalFilename();
            saveTaskFile(fileName, file);
            // 初始化任务对象
            aiHumanTask.setTaskName(fileName);                                     // 设置任务名称
            aiHumanTask.setStatus(TaskStatus.PENDING.getValue());       // 设置任务状态为待处理
            aiHumanTask.setPriority(5L);                                           // 设置默认优先级为5
            aiHumanTask.setSubmitTime(new Date());                                 // 设置提交时间
            aiHumanTask.setCreateTime(new Date());                                 // 设置创建时间
            aiHumanTask.setCreateBy("admin");                                      // 设置创建者
            aiHumanTask.setMaterialId(materialId);               // 设置文件ID
            aiHumanTask.setParentTaskId(parentTaskId);             // 设置父任务ID
            aiHumanTask.setClientId(clientId);                 // 设置客户端ID
            aiHumanTask.setUserId(userId);                   // 设置用户ID
            log.info("提交任务: task={}", aiHumanTask);
            return toAjax(aiHumanTaskService.insertAiHumanTask(aiHumanTask));
        } catch (IOException e) {
            log.error("提交任务失败: error={}", e.getMessage(), e);
            log.error("task={}", aiHumanTask);
            return AjaxResult.error("提交失败：" + e.getMessage());
        }
    }

    /**
     * 保存任务相关的文件
     *
     * @param fileName 文件名
     * @param file 上传的文件
     * @return 保存后的文件路径
     * @throws IOException 如果文件保存失败
     */
    private String saveTaskFile(String fileName, MultipartFile file) throws IOException {
        // 创建task文件夹（如果不存在）
        String materialDir = uploadPath + File.separator + "task";
        File directory = new File(materialDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        String filePath = materialDir + File.separator + fileName;
        // 保存文件
        File dest = new File(filePath);
        file.transferTo(dest);
        return filePath;
    }

    /**
     * 修改任务管理
     */
    @ApiOperation(value = "修改任务", notes = "修改任务管理信息")
    @PreAuthorize("@ss.hasPermi('aihuman:task:edit')")
    @Log(title = "任务管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@ApiParam(value = "任务信息", required = true) @RequestBody AiHumanTask aiHumanTask)
    {
        return toAjax(aiHumanTaskService.updateAiHumanTask(aiHumanTask));
    }

    /**
     * 删除任务管理
     */
    @ApiOperation(value = "删除任务", notes = "删除任务管理信息")
    @PreAuthorize("@ss.hasPermi('aihuman:task:remove')")
    @Log(title = "任务管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{taskIds}")
    public AjaxResult remove(@ApiParam(value = "任务ID数组", required = true) @PathVariable Long[] taskIds)
    {
        return toAjax(aiHumanTaskService.deleteAiHumanTaskByTaskIds(taskIds));
    }

    /**
     * 任务分发接口
     */
    @ApiOperation(value = "任务分发", notes = "为子节点分配待处理的任务，返回任务信息和素材文件")
    @GetMapping("/anonymous/dispatch")
    public void dispatch(HttpServletResponse response,@RequestParam("cluster_id") String clusterId) {
        try {
            log.info("节点[{}]，请求任务分发接口....", clusterId);
            // 获取优先级最高且提交时间最早的待处理任务，使用行级锁确保并发安全
            AiHumanTask targetTask = null;
            synchronized (this) {
                AiHumanTask queryTask = new AiHumanTask();
                queryTask.setStatus(TaskStatus.PENDING.getValue());
                List<AiHumanTask> tasks = aiHumanTaskService.selectAiHumanTaskList(queryTask);
                
                if (tasks == null || tasks.isEmpty()) {
                    log.info("无任务可分发给{}....", clusterId);
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                    return;
                }
                
                // 按优先级降序和提交时间升序排序
                targetTask = tasks.stream()
                    .sorted((t1, t2) -> {
                        int priorityCompare = t2.getPriority().compareTo(t1.getPriority());
                        if (priorityCompare != 0) {
                            return priorityCompare;
                        }
                        return t1.getSubmitTime().compareTo(t2.getSubmitTime());
                    })
                    .findFirst()
                    .orElse(null);

                if (targetTask == null) {
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                    log.info("无任务可分发给{}....", clusterId);
                    return;
                }

                // 获取任务文件
                String taskPath = uploadPath + File.separator + "task" + File.separator + targetTask.getTaskName();
                File taskFile = new File(taskPath);
                if (!taskFile.exists()) {
                    log.error("任务文件不存在: {}", taskPath);
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }

                // 文件存在，更新任务状态为处理中
                targetTask.setStatus(TaskStatus.PROCESSING.getValue());
                targetTask.setProcessStartTime(new Date());
                aiHumanTaskService.updateAiHumanTask(targetTask);

                // 设置响应头
                response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
                response.setHeader("Content-Disposition", "attachment; filename=" + targetTask.getTaskName());
                response.setHeader("Task-Id", targetTask.getTaskId().toString());
                response.setHeader("Task-Name", targetTask.getTaskName());
                response.setHeader("Material-ID", targetTask.getMaterialId());
                
                // 将文件写入响应流
                java.nio.file.Files.copy(taskFile.toPath(), response.getOutputStream());
                response.getOutputStream().flush();
            }
        } catch (Exception e) {
            log.error("任务分发失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 任务结果收集接口
     */
    @ApiOperation(value = "任务结果收集", notes = "接收子节点提交的任务处理结果")
    @PostMapping("/anonymous/result")
    public AjaxResult collectResult(
            @ApiParam(value = "任务ID", required = true) @RequestParam("task_id") String taskId,
            @ApiParam(value = "处理结果文件", required = true) @RequestPart("file") MultipartFile file,
            @ApiParam(value = "处理状态(success/failed)", required = true) @RequestParam("status") String status,
            @ApiParam(value = "失败原因", required = true) @RequestParam("err_msg") String errMsg,
            @ApiParam(value = "集群节点ID", required = true) @RequestParam("cluster_id") String clusterId) {
        log.info("接收任务[{}]处理结果，状态：{}，处理节点：{}", taskId, status, clusterId);
        
        try {
            // 检查任务是否存在
            AiHumanTask task = aiHumanTaskService.selectAiHumanTaskByTaskId(Long.valueOf(taskId));
            if (task == null) {
                log.error("任务[{}]不存在", taskId);
                return AjaxResult.error("任务不存在");
            }

            // 检查任务状态是否为处理中
            if (!TaskStatus.PROCESSING.getValue().equals(task.getStatus())) {
                log.error("任务[{}]状态异常：{}", taskId, task.getStatus());
                return AjaxResult.error("任务状态异常");
            }

            // 验证文件格式和大小
            if (file.isEmpty()) {
                String errorMsg = "结果文件为空";
                updateTaskError(task, errorMsg);
                return AjaxResult.error(errorMsg);
            }

            String resultDir = uploadPath + File.separator + "result";
            File directory = new File(resultDir);
            if (!directory.exists() && !directory.mkdirs()) {
                String errorMsg = "创建结果文件目录失败";
                updateTaskError(task, errorMsg);
                return AjaxResult.error(errorMsg);
            }

            String resultPath = resultDir + File.separator + file.getOriginalFilename();;
            File resultFile = new File(resultPath);

            try {
                file.transferTo(resultFile);
            } catch (IOException e) {
                String errorMsg = "保存结果文件失败：" + e.getMessage();
                updateTaskError(task, errorMsg);
                return AjaxResult.error(errorMsg);
            }

            // 更新任务状态和结果信息
            task.setProcessEndTime(new Date());
//            task.setResultPath(resultFileName);
            if ("success".equals(status)) {
                task.setStatus(TaskStatus.COMPLETED.getValue());
                task.setErrorMessage(""); // 清空错误信息
            } else {
                task.setStatus(TaskStatus.FAILED.getValue());
                task.setErrorMessage(errMsg);
            }

            aiHumanTaskService.updateAiHumanTask(task);
            log.info("任务[{}]结果收集完成，状态：{}", taskId, status);
            return AjaxResult.success();

        } catch (Exception e) {
            log.error("任务[{}]结果收集失败: {}", taskId, e.getMessage(), e);
            try {
                // 发生异常时，尝试回滚任务状态
                AiHumanTask task = aiHumanTaskService.selectAiHumanTaskByTaskId(Long.valueOf(taskId));
                if (task != null && TaskStatus.PROCESSING.getValue().equals(task.getStatus())) {
                    task.setErrorMessage("处理失败：" + e.getMessage());
                    task.setStatus(TaskStatus.FAILED.getValue());
                    aiHumanTaskService.updateAiHumanTask(task);
                }
            } catch (Exception ex) {
                log.error("回滚任务[{}]状态失败: {}", taskId, ex.getMessage(), ex);
            }
            return AjaxResult.error("结果收集失败：" + e.getMessage());
        }
    }

    /**
     * 更新任务错误状态
     *
     * @param task 任务对象
     * @param errorMsg 错误信息
     */
    private void updateTaskError(AiHumanTask task, String errorMsg) {
        task.setStatus(TaskStatus.FAILED.getValue());
        task.setErrorMessage(errorMsg);
        task.setProcessEndTime(new Date());
        try {
            aiHumanTaskService.updateAiHumanTask(task);
        } catch (Exception e) {
            log.error("更新任务[{}]错误状态失败: {}", task.getTaskId(), e.getMessage(), e);
        }
    }
}
