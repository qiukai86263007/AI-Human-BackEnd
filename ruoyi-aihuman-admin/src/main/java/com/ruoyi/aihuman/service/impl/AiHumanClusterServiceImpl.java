package com.ruoyi.aihuman.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.aihuman.mapper.AiHumanClusterMapper;
import com.ruoyi.aihuman.domain.AiHumanCluster;
import com.ruoyi.aihuman.service.IAiHumanClusterService;

/**
 * GPU集群管理Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-02-07
 */
@Service
public class AiHumanClusterServiceImpl implements IAiHumanClusterService 
{
    @Autowired
    private AiHumanClusterMapper aiHumanClusterMapper;

    /**
     * 查询GPU集群管理
     * 
     * @param clusterId GPU集群管理主键
     * @return GPU集群管理
     */
    @Override
    public AiHumanCluster selectAiHumanClusterByClusterId(Long clusterId)
    {
        return aiHumanClusterMapper.selectAiHumanClusterByClusterId(clusterId);
    }

    /**
     * 查询GPU集群管理列表
     * 
     * @param aiHumanCluster GPU集群管理
     * @return GPU集群管理
     */
    @Override
    public List<AiHumanCluster> selectAiHumanClusterList(AiHumanCluster aiHumanCluster)
    {
        return aiHumanClusterMapper.selectAiHumanClusterList(aiHumanCluster);
    }

    /**
     * 新增GPU集群管理
     * 
     * @param aiHumanCluster GPU集群管理
     * @return 结果
     */
    @Override
    public int insertAiHumanCluster(AiHumanCluster aiHumanCluster)
    {
        aiHumanCluster.setCreateTime(DateUtils.getNowDate());
        return aiHumanClusterMapper.insertAiHumanCluster(aiHumanCluster);
    }

    /**
     * 修改GPU集群管理
     * 
     * @param aiHumanCluster GPU集群管理
     * @return 结果
     */
    @Override
    public int updateAiHumanCluster(AiHumanCluster aiHumanCluster)
    {
        aiHumanCluster.setUpdateTime(DateUtils.getNowDate());
        return aiHumanClusterMapper.updateAiHumanCluster(aiHumanCluster);
    }

    /**
     * 批量删除GPU集群管理
     * 
     * @param clusterIds 需要删除的GPU集群管理主键
     * @return 结果
     */
    @Override
    public int deleteAiHumanClusterByClusterIds(Long[] clusterIds)
    {
        return aiHumanClusterMapper.deleteAiHumanClusterByClusterIds(clusterIds);
    }

    /**
     * 删除GPU集群管理信息
     * 
     * @param clusterId GPU集群管理主键
     * @return 结果
     */
    @Override
    public int deleteAiHumanClusterByClusterId(Long clusterId)
    {
        return aiHumanClusterMapper.deleteAiHumanClusterByClusterId(clusterId);
    }
}
