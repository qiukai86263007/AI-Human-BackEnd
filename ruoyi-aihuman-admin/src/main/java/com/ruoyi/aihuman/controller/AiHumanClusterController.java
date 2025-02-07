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
import com.ruoyi.aihuman.domain.AiHumanCluster;
import com.ruoyi.aihuman.service.IAiHumanClusterService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * GPU集群管理Controller
 * 
 * @author ruoyi
 * @date 2025-02-07
 */
@RestController
@RequestMapping("/aihuman/cluster")
public class AiHumanClusterController extends BaseController
{
    @Autowired
    private IAiHumanClusterService aiHumanClusterService;

    /**
     * 查询GPU集群管理列表
     */
    @PreAuthorize("@ss.hasPermi('aihuman:cluster:list')")
    @GetMapping("/list")
    public TableDataInfo list(AiHumanCluster aiHumanCluster)
    {
        startPage();
        List<AiHumanCluster> list = aiHumanClusterService.selectAiHumanClusterList(aiHumanCluster);
        return getDataTable(list);
    }

    /**
     * 导出GPU集群管理列表
     */
    @PreAuthorize("@ss.hasPermi('aihuman:cluster:export')")
    @Log(title = "GPU集群管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AiHumanCluster aiHumanCluster)
    {
        List<AiHumanCluster> list = aiHumanClusterService.selectAiHumanClusterList(aiHumanCluster);
        ExcelUtil<AiHumanCluster> util = new ExcelUtil<AiHumanCluster>(AiHumanCluster.class);
        util.exportExcel(response, list, "GPU集群管理数据");
    }

    /**
     * 获取GPU集群管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('aihuman:cluster:query')")
    @GetMapping(value = "/{clusterId}")
    public AjaxResult getInfo(@PathVariable("clusterId") Long clusterId)
    {
        return success(aiHumanClusterService.selectAiHumanClusterByClusterId(clusterId));
    }

    /**
     * 新增GPU集群管理
     */
    @PreAuthorize("@ss.hasPermi('aihuman:cluster:add')")
    @Log(title = "GPU集群管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AiHumanCluster aiHumanCluster)
    {
        return toAjax(aiHumanClusterService.insertAiHumanCluster(aiHumanCluster));
    }

    /**
     * 修改GPU集群管理
     */
    @PreAuthorize("@ss.hasPermi('aihuman:cluster:edit')")
    @Log(title = "GPU集群管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AiHumanCluster aiHumanCluster)
    {
        return toAjax(aiHumanClusterService.updateAiHumanCluster(aiHumanCluster));
    }

    /**
     * 删除GPU集群管理
     */
    @PreAuthorize("@ss.hasPermi('aihuman:cluster:remove')")
    @Log(title = "GPU集群管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{clusterIds}")
    public AjaxResult remove(@PathVariable Long[] clusterIds)
    {
        return toAjax(aiHumanClusterService.deleteAiHumanClusterByClusterIds(clusterIds));
    }
}
