package com.ruoyi.aihuman.mapper;

import java.util.List;
import com.ruoyi.aihuman.domain.AiHumanUser;

/**
 * 用户管理Mapper接口
 * 
 * @author ruoyi
 * @date 2025-02-06
 */
public interface AiHumanUserMapper 
{
    /**
     * 查询用户管理
     * 
     * @param userId 用户管理主键
     * @return 用户管理
     */
    public AiHumanUser selectAiHumanUserByUserId(Long userId);

    /**
     * 查询用户管理列表
     * 
     * @param aiHumanUser 用户管理
     * @return 用户管理集合
     */
    public List<AiHumanUser> selectAiHumanUserList(AiHumanUser aiHumanUser);

    /**
     * 新增用户管理
     * 
     * @param aiHumanUser 用户管理
     * @return 结果
     */
    public int insertAiHumanUser(AiHumanUser aiHumanUser);

    /**
     * 修改用户管理
     * 
     * @param aiHumanUser 用户管理
     * @return 结果
     */
    public int updateAiHumanUser(AiHumanUser aiHumanUser);

    /**
     * 删除用户管理
     * 
     * @param userId 用户管理主键
     * @return 结果
     */
    public int deleteAiHumanUserByUserId(Long userId);

    /**
     * 批量删除用户管理
     * 
     * @param userIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAiHumanUserByUserIds(Long[] userIds);
    /**
     * 根据用户名查询用户
     * 
     * @param userName 用户名
     * @return 用户对象信息
     */
    public AiHumanUser selectAiHumanUserByUserName(String userName);

}
