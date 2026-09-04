package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.UsermanageMapper;
import com.ruoyi.system.domain.Usermanage;
import com.ruoyi.system.service.IUsermanageService;

/**
 * 用户信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-10-22
 */
@Service
public class UsermanageServiceImpl implements IUsermanageService 
{
    @Autowired
    private UsermanageMapper usermanageMapper;

    /**
     * 查询用户信息
     * 
     * @param userId 用户信息主键
     * @return 用户信息
     */
    @Override
    public Usermanage selectUsermanageByUserId(Long userId)
    {
        return usermanageMapper.selectUsermanageByUserId(userId);
    }

    /**
     * 查询用户信息列表
     * 
     * @param usermanage 用户信息
     * @return 用户信息
     */
    @Override
    public List<Usermanage> selectUsermanageList(Usermanage usermanage)
    {
        return usermanageMapper.selectUsermanageList(usermanage);
    }

    /**
     * 新增用户信息
     * 
     * @param usermanage 用户信息
     * @return 结果
     */
    @Override
    public int insertUsermanage(Usermanage usermanage)
    {
        usermanage.setCreateTime(DateUtils.getNowDate());
        return usermanageMapper.insertUsermanage(usermanage);
    }

    /**
     * 修改用户信息
     * 
     * @param usermanage 用户信息
     * @return 结果
     */
    @Override
    public int updateUsermanage(Usermanage usermanage)
    {
        usermanage.setUpdateTime(DateUtils.getNowDate());
        return usermanageMapper.updateUsermanage(usermanage);
    }

    /**
     * 批量删除用户信息
     * 
     * @param userIds 需要删除的用户信息主键
     * @return 结果
     */
    @Override
    public int deleteUsermanageByUserIds(Long[] userIds)
    {
        return usermanageMapper.deleteUsermanageByUserIds(userIds);
    }

    /**
     * 删除用户信息信息
     * 
     * @param userId 用户信息主键
     * @return 结果
     */
    @Override
    public int deleteUsermanageByUserId(Long userId)
    {
        return usermanageMapper.deleteUsermanageByUserId(userId);
    }
}
