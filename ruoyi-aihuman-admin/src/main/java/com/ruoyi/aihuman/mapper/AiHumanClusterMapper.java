package com.ruoyi.aihuman.mapper;

import java.util.List;
import com.ruoyi.aihuman.domain.AiHumanCluster;

/**
 * GPU集群管理Mapper接口
 * 
 * @author ruoyi
 * @date 2025-02-07
 */
public interface AiHumanClusterMapper 
{
    /**
     * 查询GPU集群管理
     * 
     * @param clusterId GPU集群管理主键
     * @return GPU集群管理
     */
    public AiHumanCluster selectAiHumanClusterByClusterId(Long clusterId);

    /**
     * 查询GPU集群管理列表
     * 
     * @param aiHumanCluster GPU集群管理
     * @return GPU集群管理集合
     */
    public List<AiHumanCluster> selectAiHumanClusterList(AiHumanCluster aiHumanCluster);

    /**
     * 新增GPU集群管理
     * 
     * @param aiHumanCluster GPU集群管理
     * @return 结果
     */
    public int insertAiHumanCluster(AiHumanCluster aiHumanCluster);

    /**
     * 修改GPU集群管理
     * 
     * @param aiHumanCluster GPU集群管理
     * @return 结果
     */
    public int updateAiHumanCluster(AiHumanCluster aiHumanCluster);

    /**
     * 删除GPU集群管理
     * 
     * @param clusterId GPU集群管理主键
     * @return 结果
     */
    public int deleteAiHumanClusterByClusterId(Long clusterId);

    /**
     * 批量删除GPU集群管理
     * 
     * @param clusterIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAiHumanClusterByClusterIds(Long[] clusterIds);
}
