package com.ruoyi.aihuman.controller;

import java.util.List;
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

/**
 * 任务管理Controller
 * 
 * @author ruoyi
 * @date 2025-02-06
 */
@RestController
@RequestMapping("/aihuman/task")
public class AiHumanTaskController extends BaseController
{
    @Autowired
    private IAiHumanTaskService aiHumanTaskService;

    /**
     * 查询任务管理列表
     */
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
    @PreAuthorize("@ss.hasPermi('aihuman:task:query')")
    @GetMapping(value = "/{taskId}")
    public AjaxResult getInfo(@PathVariable("taskId") Long taskId)
    {
        return success(aiHumanTaskService.selectAiHumanTaskByTaskId(taskId));
    }

    /**
     * 新增任务管理
     */
    @PreAuthorize("@ss.hasPermi('aihuman:task:add')")
    @Log(title = "任务管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AiHumanTask aiHumanTask)
    {
        return toAjax(aiHumanTaskService.insertAiHumanTask(aiHumanTask));
    }

    /**
     * 修改任务管理
     */
    @PreAuthorize("@ss.hasPermi('aihuman:task:edit')")
    @Log(title = "任务管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AiHumanTask aiHumanTask)
    {
        return toAjax(aiHumanTaskService.updateAiHumanTask(aiHumanTask));
    }

    /**
     * 删除任务管理
     */
    @PreAuthorize("@ss.hasPermi('aihuman:task:remove')")
    @Log(title = "任务管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{taskIds}")
    public AjaxResult remove(@PathVariable Long[] taskIds)
    {
        return toAjax(aiHumanTaskService.deleteAiHumanTaskByTaskIds(taskIds));
    }
}
