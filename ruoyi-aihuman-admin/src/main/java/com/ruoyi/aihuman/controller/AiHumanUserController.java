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
import com.ruoyi.aihuman.domain.AiHumanUser;
import com.ruoyi.aihuman.service.IAiHumanUserService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 用户管理Controller
 * 
 * @author ruoyi
 * @date 2025-02-06
 */
@RestController
@RequestMapping("/aihuman/user")
public class AiHumanUserController extends BaseController
{
    @Autowired
    private IAiHumanUserService aiHumanUserService;

    /**
     * 查询用户管理列表
     */
    @PreAuthorize("@ss.hasPermi('aihuman:user:list')")
    @GetMapping("/list")
    public TableDataInfo list(AiHumanUser aiHumanUser)
    {
        startPage();
        List<AiHumanUser> list = aiHumanUserService.selectAiHumanUserList(aiHumanUser);
        return getDataTable(list);
    }

    /**
     * 导出用户管理列表
     */
    @PreAuthorize("@ss.hasPermi('aihuman:user:export')")
    @Log(title = "用户管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AiHumanUser aiHumanUser)
    {
        List<AiHumanUser> list = aiHumanUserService.selectAiHumanUserList(aiHumanUser);
        ExcelUtil<AiHumanUser> util = new ExcelUtil<AiHumanUser>(AiHumanUser.class);
        util.exportExcel(response, list, "用户管理数据");
    }

    /**
     * 获取用户管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('aihuman:user:query')")
    @GetMapping(value = "/{userId}")
    public AjaxResult getInfo(@PathVariable("userId") Long userId)
    {
        return success(aiHumanUserService.selectAiHumanUserByUserId(userId));
    }

    /**
     * 新增用户管理
     */
    @PreAuthorize("@ss.hasPermi('aihuman:user:add')")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AiHumanUser aiHumanUser)
    {
        return toAjax(aiHumanUserService.insertAiHumanUser(aiHumanUser));
    }

    /**
     * 修改用户管理
     */
    @PreAuthorize("@ss.hasPermi('aihuman:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AiHumanUser aiHumanUser)
    {
        return toAjax(aiHumanUserService.updateAiHumanUser(aiHumanUser));
    }

    /**
     * 删除用户管理
     */
    @PreAuthorize("@ss.hasPermi('aihuman:user:remove')")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{userIds}")
    public AjaxResult remove(@PathVariable Long[] userIds)
    {
        return toAjax(aiHumanUserService.deleteAiHumanUserByUserIds(userIds));
    }
}
