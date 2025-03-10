package com.ruoyi.aihuman.controller;

import java.io.File;
import java.io.IOException;
import java.util.*;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.nio.file.*;

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
     * 按parentTaskId统计不同状态的任务数量
     *
     * @param parentTaskId 父任务ID
     * @return 包含不同状态任务数量的Map
     */
    @ApiOperation(value = "按父任务ID统计不同状态的任务数量", notes = "根据父任务ID统计待处理、处理中、已完成、失败任务的数量")
    @GetMapping("/anonymous/status/{parentTaskId}")
    public AjaxResult countTasksByParentTaskIdAndStatus(@ApiParam(value = "父任务ID", required = true) @PathVariable("parentTaskId") String parentTaskId) {
        Map<String, Integer> result = aiHumanTaskService.countTasksByParentTaskIdAndStatus(parentTaskId);
        return AjaxResult.success(result);
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
    public AjaxResult submit(@ApiParam(value = "音频ID", required = true) @RequestParam("audioIdList") List<String> audioIdList,
                             @ApiParam(value = "形象ID", required = true) @RequestParam("imageIdList") List<String> imageIdList,
                             @ApiParam(value = "父任务ID", required = true) @RequestParam("parentTaskId") String parentTaskId,
                             @ApiParam(value = "用户ID", required = true) @RequestParam("userId") String userId,
                             @ApiParam(value = "客户端ID", required = true) @RequestParam("clientId") String clientId,
                             @ApiParam(value = "音频文件(.wav格式)", required = true) @RequestPart("file") MultipartFile file) {
        // 检查参数合法性
        if (audioIdList.size() != imageIdList.size()) {
            return AjaxResult.error("参数错误");
        }
        String fileName = file.getOriginalFilename();
        if (!fileName.endsWith(".zip")) {
            return AjaxResult.error("文件格式错误");
        }

        String targetPath = uploadPath + File.separator + "source_zip";
        File directory = new File(targetPath);

        try {
            // 创建临时目录
            createDirectoryIfNotExists(directory);
            // 保存上传的文件
            Path filePath = saveUploadedFile(file, targetPath, fileName);
            // 解压文件
            unzipFile(file, targetPath);

            String baseDir = targetPath + File.separator + fileName.replace(".zip", "");
            // 检查文件是否存在
            List<String> missingFiles = checkFilesExist(audioIdList, baseDir);
            if (!missingFiles.isEmpty()) {
                log.error("缺失的音频文件: {}", missingFiles);
                deleteFilesAndFolders(directory);
                return AjaxResult.error("缺失部分文件");
            }

            // 检验完成，准备复制文件和入库
            List<AiHumanTask> taskList = new ArrayList<>();
            for (int i = 0; i < audioIdList.size(); i++) {
                moveFileOrDirectory(baseDir + File.separator + audioIdList.get(i) + ".wav",
                        uploadPath + File.separator + "task");
                // 初始化任务对象
                AiHumanTask aiHumanTask = createTask(audioIdList, imageIdList, parentTaskId, userId, clientId, i);
                taskList.add(aiHumanTask);
                log.info("提交任务: task={}", aiHumanTask);
                aiHumanTaskService.insertAiHumanTask(aiHumanTask);
            }
            deleteFilesAndFolders(directory);
            return AjaxResult.success("全部提交成功");
        } catch (IOException e) {
            log.error("提交任务失败: error={}", e.getMessage(), e);
            deleteFilesAndFolders(directory);
            return AjaxResult.error("提交失败：" + e.getMessage());
        }
    }

    // 创建目录，如果目录不存在
    private void createDirectoryIfNotExists(File directory) throws IOException {
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new IOException("无法创建目录: " + directory.getAbsolutePath());
            }
        }
    }

    // 保存上传的文件
    private Path saveUploadedFile(MultipartFile file, String targetPath, String fileName) throws IOException {
        Path filePath = Paths.get(targetPath, fileName);
        Files.copy(file.getInputStream(), filePath);
        return filePath;
    }

    // 解压文件
    private void unzipFile(MultipartFile file, String targetPath) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(file.getInputStream())) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                String entryName = zipEntry.getName();
                Path entryPath = Paths.get(targetPath, entryName);
                if (zipEntry.isDirectory()) {
                    Files.createDirectories(entryPath);
                } else {
                    Files.createDirectories(entryPath.getParent());
                    Files.copy(zis, entryPath);
                }
                zipEntry = zis.getNextEntry();
            }
        }
    }

    // 检查文件是否存在
    private List<String> checkFilesExist(List<String> audioIdList, String baseDir) {
        List<String> missingFiles = new ArrayList<>();
        for (String singFile : audioIdList) {
            String filePathToCheck = baseDir + File.separator + singFile + ".wav";
            File fileToCheck = new File(filePathToCheck);
            if (!fileToCheck.exists()) {
                missingFiles.add(filePathToCheck);
            }
        }
        return missingFiles;
    }

    // 创建任务对象
    private AiHumanTask createTask(List<String> audioIdList, List<String> imageIdList, String parentTaskId, String userId, String clientId, int index) {
        AiHumanTask aiHumanTask = new AiHumanTask();
        aiHumanTask.setTaskName(audioIdList.get(index) + ".wav");
        aiHumanTask.setStatus(TaskStatus.PENDING.getValue());
        aiHumanTask.setPriority(5L);
        aiHumanTask.setSubmitTime(new Date());
        aiHumanTask.setCreateTime(new Date());
        aiHumanTask.setCreateBy("admin");
        aiHumanTask.setMaterialId(audioIdList.get(index));
        aiHumanTask.setParentTaskId(parentTaskId);
        aiHumanTask.setImageId(imageIdList.get(index));
        aiHumanTask.setClientId(clientId);
        aiHumanTask.setUserId(userId);
        return aiHumanTask;
    }

    private static Path moveFileOrDirectory(String sourcePath, String targetDir) throws IOException {
        // 将路径转换为 Path 对象
        Path source = Paths.get(sourcePath);
        Path target = Paths.get(targetDir);

        // 检查源路径是否存在
        if (!Files.exists(source)) {
            throw new IOException("源路径 '" + sourcePath + "' 不存在");
        }

        // 创建目标目录（如果不存在）
        if (!Files.exists(target)) {
            Files.createDirectories(target);
            log.info("目标目录 '{}' 已创建", targetDir);
        }

        // 构建目标路径
        Path targetPath = target.resolve(source.getFileName());

        // 处理目标路径冲突
        if (Files.exists(targetPath)) {
            log.warn("警告：目标路径 '{}' 已存在，将被覆盖", targetPath);
        }

        // 移动文件或目录
        Files.move(source, targetPath, StandardCopyOption.REPLACE_EXISTING);

        // 打印移动结果
        if (Files.isDirectory(source)) {
            log.info("目录 '{}' 已移动到 '{}'", source, targetPath);
        } else {
            log.info("文件 '{}' 已移动到 '{}'", source, targetPath);
        }
        return targetPath;
    }

    // 递归删除文件夹及其内容
    private static void deleteFilesAndFolders(File directory) {
        // 获取目录下的所有文件和文件夹
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                // 如果是文件，直接删除
                if (file.isFile()) {
                    file.delete();
                } else if (file.isDirectory()) {
                    // 如果是文件夹，递归删除文件夹中的内容
                    deleteFilesAndFolders(file);
                    // 删除空文件夹
                    file.delete();
                }
            }
        }
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
     * 渲染结果下载接口，提供给客户端匿名调用
     */
    @ApiOperation(value = "下载任务结果", notes = "根据任务名称下载对应的处理结果文件")
    @GetMapping("/anonymous/download")
    public void downloadResult(
            HttpServletResponse response,
            @ApiParam(value = "任务名称，为音频素材的{uuid},如818c6df0-3f16-4bcc-b67e-ead92f31e134", required = true) @RequestParam("taskUUID") String taskUUID) {
        try {
            String taskName = taskUUID + ".wav";
            log.info("请求下载任务[{}]的结果文件", taskName);
            // 查询任务是否存在
            AiHumanTask queryTask = new AiHumanTask();
            queryTask.setTaskName(taskName);
            List<AiHumanTask> tasks = aiHumanTaskService.selectAiHumanTaskList(queryTask);
            if (tasks == null || tasks.isEmpty()) {
                log.error("任务[{}]不存在", taskName);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            // 检查任务状态
            AiHumanTask task = tasks.get(0);
            if (!TaskStatus.COMPLETED.getValue().equals(task.getStatus())) {
                log.error("任务[{}]未完成，当前状态: {}", taskName, task.getStatus());
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            String resultFileName = taskUUID + ".mp4";
            // 构建结果文件路径
            String resultPath = uploadPath + File.separator + "result" + File.separator + resultFileName;
            File resultFile = new File(resultPath);
            
            // 检查文件是否存在
            if (!resultFile.exists()) {
                log.error("任务[{}]的结果文件不存在: {}", taskUUID, resultPath);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            
            // 设置响应头
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader("Content-Disposition", "attachment; filename=" + resultFileName);
            
            // 将文件写入响应流
            java.nio.file.Files.copy(resultFile.toPath(), response.getOutputStream());
            response.getOutputStream().flush();
            log.info("任务[{}]的结果文件下载完成", taskName);
            // 删除原始文件
            if (resultFile.delete()) {
                log.info("任务[{}]的结果文件已删除: {}", taskName, resultPath);
            } else {
                log.warn("任务[{}]的结果文件删除失败: {}", taskName, resultPath);
            }

        } catch (Exception e) {
            log.error("下载任务[{}]的结果文件失败: {}", taskUUID, e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            // 异常发生时要不要立即删除原有文件？
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
