package com.ruoyi.aihuman.controller;

import java.util.List;
import java.util.Date;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    @Autowired
    private IAiHumanTaskService aiHumanTaskService;

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
    @ApiOperation(value = "提交任务", notes = "提交新的任务")
    @Log(title = "任务管理", businessType = BusinessType.INSERT)
    @PostMapping("/submit")
    public AjaxResult submit(@ApiParam(value = "任务信息", required = true) @RequestBody AiHumanTask aiHumanTask)
    {
        // 设置任务初始状态为待处理
        aiHumanTask.setStatus(String.valueOf(TaskStatus.PENDING.getValue()));
        // 设置提交时间为当前时间
        aiHumanTask.setCreateTime(new Date());
        // 设置创建者
        aiHumanTask.setCreateBy(getUsername());
        System.out.println("提交了任务: "+aiHumanTask);
        return toAjax(aiHumanTaskService.insertAiHumanTask(aiHumanTask));
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
