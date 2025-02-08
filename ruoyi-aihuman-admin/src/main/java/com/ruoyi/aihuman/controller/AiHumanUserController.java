package com.ruoyi.aihuman.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.aihuman.task.TaskProcessor;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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
@Api(tags = "用户管理接口")
@RestController
@RequestMapping("/aihuman/user")
public class AiHumanUserController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(AiHumanUserController.class);

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

    /**
     * 用户登录
     */
    @ApiOperation(value = "用户登录", notes = "通过用户名和密码进行登录验证")
    @PostMapping("/login")
    public AjaxResult login(
        @ApiParam(value = "用户名", required = true) @RequestParam("userName") String userName,
        @ApiParam(value = "密码", required = true) @RequestParam("password") String password)
    {
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password))
        {
            log.info("用户登录失败，用户名或密码不能为空");
            return error("用户名或密码不能为空");
        }
        AiHumanUser user = aiHumanUserService.selectAiHumanUserByUserName(userName);
        if (user == null)
        {
            log.info("用户[{}]登录失败，用户不存在", userName);
            return error("用户不存在");
        }
        if (!password.equals(user.getPassword()))
        {
            log.info("用户[{}]登录失败，密码错误", userName);
            return error("密码错误");
        }
        log.info("用户[{}]登录成功", userName);
        return success("登录成功");
    }
}
