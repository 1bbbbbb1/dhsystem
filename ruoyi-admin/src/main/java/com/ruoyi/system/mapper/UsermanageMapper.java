package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.Usermanage;

/**
 * 用户信息Mapper接口
 * 
 * @author ruoyi
 * @date 2025-10-22
 */
public interface UsermanageMapper 
{
    /**
     * 查询用户信息
     * 
     * @param userId 用户信息主键
     * @return 用户信息
     */
    public Usermanage selectUsermanageByUserId(Long userId);

    /**
     * 查询用户信息列表
     * 
     * @param usermanage 用户信息
     * @return 用户信息集合
     */
    public List<Usermanage> selectUsermanageList(Usermanage usermanage);

    /**
     * 新增用户信息
     * 
     * @param usermanage 用户信息
     * @return 结果
     */
    public int insertUsermanage(Usermanage usermanage);

    /**
     * 修改用户信息
     * 
     * @param usermanage 用户信息
     * @return 结果
     */
    public int updateUsermanage(Usermanage usermanage);

    /**
     * 删除用户信息
     * 
     * @param userId 用户信息主键
     * @return 结果
     */
    public int deleteUsermanageByUserId(Long userId);

    /**
     * 批量删除用户信息
     * 
     * @param userIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteUsermanageByUserIds(Long[] userIds);
}
