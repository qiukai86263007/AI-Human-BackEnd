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
    @PostMapping(value = "/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AjaxResult submit(@ApiParam(value = "任务信息", required = true) @RequestParam("taskName") String taskName,
                           @ApiParam(value = "音频文件(.wav格式)", required = true) @RequestPart("file") MultipartFile file) {
        AiHumanTask aiHumanTask = new AiHumanTask();
        try {
            String fileName = file.getOriginalFilename();
            saveTaskFile(fileName, file);
            // 初始化任务对象
            aiHumanTask.setTaskName(taskName);                                     // 设置任务名称
            aiHumanTask.setStatus(TaskStatus.setStatus(TaskStatus.PENDING));       // 设置任务状态为待处理
            aiHumanTask.setPriority(5L);                                           // 设置默认优先级为1
            aiHumanTask.setSubmitTime(new Date());                                 // 设置提交时间
            aiHumanTask.setCreateTime(new Date());                                 // 设置创建时间
            aiHumanTask.setCreateBy("admin");                                      // 设置创建者
            aiHumanTask.setMaterialId(UUID.randomUUID().toString());               // 设置文件ID
            aiHumanTask.setParentTaskId(UUID.randomUUID().toString());             // 设置父任务ID
            aiHumanTask.setClientId(UUID.randomUUID().toString());                 // 设置客户端ID
            aiHumanTask.setUserId(UUID.randomUUID().toString());                   // 设置用户ID
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
        // 创建material文件夹（如果不存在）
        String materialDir = uploadPath + File.separator + "material";
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
}
