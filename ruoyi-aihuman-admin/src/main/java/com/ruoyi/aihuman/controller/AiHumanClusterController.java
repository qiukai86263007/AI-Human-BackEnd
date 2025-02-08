package com.ruoyi.aihuman.controller;

import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.aihuman.enums.ClusterStatus;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
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
@Api(tags = "集群管理接口")
@RestController
@RequestMapping("/aihuman/cluster")
public class AiHumanClusterController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(AiHumanClusterController.class);

    @Autowired
    private IAiHumanClusterService aiHumanClusterService;

    /**
     * 查询GPU集群管理列表
     */
    @PreAuthorize("@ss.hasPermi('aihuman:cluster:list')")
    @GetMapping("/list")
    public TableDataInfo list(AiHumanCluster aiHumanCluster) {
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
    public void export(HttpServletResponse response, AiHumanCluster aiHumanCluster) {
        List<AiHumanCluster> list = aiHumanClusterService.selectAiHumanClusterList(aiHumanCluster);
        ExcelUtil<AiHumanCluster> util = new ExcelUtil<AiHumanCluster>(AiHumanCluster.class);
        util.exportExcel(response, list, "GPU集群管理数据");
    }

    /**
     * 获取GPU集群管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('aihuman:cluster:query')")
    @GetMapping(value = "/{clusterId}")
    public AjaxResult getInfo(@PathVariable("clusterId") Long clusterId) {
        return success(aiHumanClusterService.selectAiHumanClusterByClusterId(clusterId));
    }

    /**
     * 新增GPU集群管理
     */
    @PreAuthorize("@ss.hasPermi('aihuman:cluster:add')")
    @Log(title = "GPU集群管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AiHumanCluster aiHumanCluster) {
        return toAjax(aiHumanClusterService.insertAiHumanCluster(aiHumanCluster));
    }

    /**
     * 修改GPU集群管理
     */
    @PreAuthorize("@ss.hasPermi('aihuman:cluster:edit')")
    @Log(title = "GPU集群管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AiHumanCluster aiHumanCluster) {
        return toAjax(aiHumanClusterService.updateAiHumanCluster(aiHumanCluster));
    }

    /**
     * 删除GPU集群管理
     */
    @PreAuthorize("@ss.hasPermi('aihuman:cluster:remove')")
    @Log(title = "GPU集群管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{clusterIds}")
    public AjaxResult remove(@PathVariable Long[] clusterIds) {
        return toAjax(aiHumanClusterService.deleteAiHumanClusterByClusterIds(clusterIds));
    }

    /**
     * 集群节点心跳接口
     */
    @ApiOperation(value = "集群节点心跳", notes = "接收并处理集群节点的心跳请求，更新节点状态和最后心跳时间")
    @PostMapping("/heartbeat/{clusterId}")
    public AjaxResult heartbeat(@ApiParam(value = "集群节点ID", required = true) @PathVariable("clusterId") Long clusterId) {
        log.info("接收到集群节点[{}]的心跳请求", clusterId);
        AiHumanCluster cluster = aiHumanClusterService.selectAiHumanClusterByClusterId(clusterId);
        if (cluster == null) {
            log.error("集群节点[{}]不存在", clusterId);
            return error("集群节点不存在");
        }

        // 更新节点状态和最后心跳时间
        cluster.setStatus(ClusterStatus.Normal.getValue()); // 1表示正常状态
        cluster.setLastHeartbeat(new Date());
        int result = aiHumanClusterService.updateAiHumanCluster(cluster);
        log.info("更新集群节点[{}]状态成功，当前状态：正常", clusterId);
        return toAjax(result);
    }

    /**
     * 检查集群节点状态
     */
    @Scheduled(fixedRate = 5000) // 每5秒执行一次
    public void checkClusterStatus() {
        log.debug("开始检查集群节点状态");
        AiHumanCluster query = new AiHumanCluster();
        List<AiHumanCluster> clusters = aiHumanClusterService.selectAiHumanClusterList(query);

        Date now = new Date();
        for (AiHumanCluster cluster : clusters) {
            log.info(cluster.toString());
            if (cluster.getLastHeartbeat() != null) {
                // 计算最后心跳时间与当前时间的差值
                long diff = now.getTime() - cluster.getLastHeartbeat().getTime();
                log.debug("集群节点[{}]最后心跳时间：{}，当前时间：{}，时间差：{}", cluster.getClusterId(), cluster.getLastHeartbeat(), now, diff);
                if (diff > 10 * 1000) // 超过10秒未收到心跳
                {
                    // 只有当节点不是异常状态时才更新为异常状态
                    if (!"0".equals(cluster.getStatus())) {
                        log.warn("集群节点[{}]超过10秒未收到心跳，标记为异常状态", cluster.getClusterId());
                        cluster.setStatus(ClusterStatus.AbNormal.getValue()); // 0表示异常状态
                        aiHumanClusterService.updateAiHumanCluster(cluster);
                    }
                } else if ("0".equals(cluster.getStatus())) // 如果当前是异常状态，且心跳恢复正常
                {
                    log.info("集群节点[{}]心跳恢复正常，更新为正常状态", cluster.getClusterId());
                    cluster.setStatus(ClusterStatus.Normal.getValue()); // 1表示正常状态
                    aiHumanClusterService.updateAiHumanCluster(cluster);
                }
            }
        }
        log.debug("集群节点状态检查完成");
    }

}
