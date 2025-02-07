package com.ruoyi.aihuman.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * GPU集群管理对象 ai_human_cluster
 * 
 * @author ruoyi
 * @date 2025-02-07
 */
public class AiHumanCluster extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 集群ID */
    private Long clusterId;

    /** 集群名称 */
    @Excel(name = "集群名称")
    private String clusterName;

    /** IP地址 */
    @Excel(name = "IP地址")
    private String ipAddress;

    /** 端口号 */
    @Excel(name = "端口号")
    private Long port;

    /** GPU数量 */
    @Excel(name = "GPU数量")
    private Long gpuCount;

    /** GPU型号 */
    @Excel(name = "GPU型号")
    private String gpuType;

    /** GPU显存(GB) */
    @Excel(name = "GPU显存(GB)")
    private Long gpuMemory;

    /** 集群状态（0空闲 1使用中 2离线 3故障） */
    @Excel(name = "集群状态", readConverterExp = "0=空闲,1=使用中,2=离线,3=故障")
    private String status;

    /** 是否启用（0停用 1启用） */
    @Excel(name = "是否启用", readConverterExp = "0=停用,1=启用")
    private String enabled;

    /** CPU核心数 */
    @Excel(name = "CPU核心数")
    private Long cpuCores;

    /** 内存大小(GB) */
    @Excel(name = "内存大小(GB)")
    private Long memorySize;

    /** 磁盘空间(GB) */
    @Excel(name = "磁盘空间(GB)")
    private Long diskSpace;

    /** GPU负载率 */
    @Excel(name = "GPU负载率")
    private BigDecimal loadAverage;

    /** GPU温度(℃) */
    @Excel(name = "GPU温度(℃)")
    private BigDecimal temperature;

    /** 最后心跳时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "最后心跳时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date lastHeartbeat;

    public void setClusterId(Long clusterId) 
    {
        this.clusterId = clusterId;
    }

    public Long getClusterId() 
    {
        return clusterId;
    }
    public void setClusterName(String clusterName) 
    {
        this.clusterName = clusterName;
    }

    public String getClusterName() 
    {
        return clusterName;
    }
    public void setIpAddress(String ipAddress) 
    {
        this.ipAddress = ipAddress;
    }

    public String getIpAddress() 
    {
        return ipAddress;
    }
    public void setPort(Long port) 
    {
        this.port = port;
    }

    public Long getPort() 
    {
        return port;
    }
    public void setGpuCount(Long gpuCount) 
    {
        this.gpuCount = gpuCount;
    }

    public Long getGpuCount() 
    {
        return gpuCount;
    }
    public void setGpuType(String gpuType) 
    {
        this.gpuType = gpuType;
    }

    public String getGpuType() 
    {
        return gpuType;
    }
    public void setGpuMemory(Long gpuMemory) 
    {
        this.gpuMemory = gpuMemory;
    }

    public Long getGpuMemory() 
    {
        return gpuMemory;
    }
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }
    public void setEnabled(String enabled) 
    {
        this.enabled = enabled;
    }

    public String getEnabled() 
    {
        return enabled;
    }
    public void setCpuCores(Long cpuCores) 
    {
        this.cpuCores = cpuCores;
    }

    public Long getCpuCores() 
    {
        return cpuCores;
    }
    public void setMemorySize(Long memorySize) 
    {
        this.memorySize = memorySize;
    }

    public Long getMemorySize() 
    {
        return memorySize;
    }
    public void setDiskSpace(Long diskSpace) 
    {
        this.diskSpace = diskSpace;
    }

    public Long getDiskSpace() 
    {
        return diskSpace;
    }
    public void setLoadAverage(BigDecimal loadAverage) 
    {
        this.loadAverage = loadAverage;
    }

    public BigDecimal getLoadAverage() 
    {
        return loadAverage;
    }
    public void setTemperature(BigDecimal temperature) 
    {
        this.temperature = temperature;
    }

    public BigDecimal getTemperature() 
    {
        return temperature;
    }
    public void setLastHeartbeat(Date lastHeartbeat) 
    {
        this.lastHeartbeat = lastHeartbeat;
    }

    public Date getLastHeartbeat() 
    {
        return lastHeartbeat;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("clusterId", getClusterId())
            .append("clusterName", getClusterName())
            .append("ipAddress", getIpAddress())
            .append("port", getPort())
            .append("gpuCount", getGpuCount())
            .append("gpuType", getGpuType())
            .append("gpuMemory", getGpuMemory())
            .append("status", getStatus())
            .append("enabled", getEnabled())
            .append("cpuCores", getCpuCores())
            .append("memorySize", getMemorySize())
            .append("diskSpace", getDiskSpace())
            .append("loadAverage", getLoadAverage())
            .append("temperature", getTemperature())
            .append("lastHeartbeat", getLastHeartbeat())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
