package com.ruoyi.aihuman.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.aihuman.mapper.AiHumanUserMapper;
import com.ruoyi.aihuman.domain.AiHumanUser;
import com.ruoyi.aihuman.service.IAiHumanUserService;

/**
 * 用户管理Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-02-06
 */
@Service
public class AiHumanUserServiceImpl implements IAiHumanUserService 
{
    @Autowired
    private AiHumanUserMapper aiHumanUserMapper;

    /**
     * 查询用户管理
     * 
     * @param userId 用户管理主键
     * @return 用户管理
     */
    @Override
    public AiHumanUser selectAiHumanUserByUserId(Long userId)
    {
        return aiHumanUserMapper.selectAiHumanUserByUserId(userId);
    }

    /**
     * 查询用户管理列表
     * 
     * @param aiHumanUser 用户管理
     * @return 用户管理
     */
    @Override
    public List<AiHumanUser> selectAiHumanUserList(AiHumanUser aiHumanUser)
    {
        return aiHumanUserMapper.selectAiHumanUserList(aiHumanUser);
    }

    /**
     * 新增用户管理
     * 
     * @param aiHumanUser 用户管理
     * @return 结果
     */
    @Override
    public int insertAiHumanUser(AiHumanUser aiHumanUser)
    {
        aiHumanUser.setCreateTime(DateUtils.getNowDate());
        return aiHumanUserMapper.insertAiHumanUser(aiHumanUser);
    }

    /**
     * 修改用户管理
     * 
     * @param aiHumanUser 用户管理
     * @return 结果
     */
    @Override
    public int updateAiHumanUser(AiHumanUser aiHumanUser)
    {
        aiHumanUser.setUpdateTime(DateUtils.getNowDate());
        return aiHumanUserMapper.updateAiHumanUser(aiHumanUser);
    }

    /**
     * 批量删除用户管理
     * 
     * @param userIds 需要删除的用户管理主键
     * @return 结果
     */
    @Override
    public int deleteAiHumanUserByUserIds(Long[] userIds)
    {
        return aiHumanUserMapper.deleteAiHumanUserByUserIds(userIds);
    }

    /**
     * 删除用户管理信息
     * 
     * @param userId 用户管理主键
     * @return 结果
     */
    @Override
    public int deleteAiHumanUserByUserId(Long userId)
    {
        return aiHumanUserMapper.deleteAiHumanUserByUserId(userId);
    }
    /**
     * 根据用户名查询用户
     * 
     * @param userName 用户名
     * @return 用户对象信息
     */
    @Override
    public AiHumanUser selectAiHumanUserByUserName(String userName)
    {
        return aiHumanUserMapper.selectAiHumanUserByUserName(userName);
    }
}
