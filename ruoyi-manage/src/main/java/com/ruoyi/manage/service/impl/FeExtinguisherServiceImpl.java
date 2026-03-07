package com.ruoyi.manage.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.manage.mapper.FeExtinguisherMapper;
import com.ruoyi.manage.domain.FeExtinguisher;
import com.ruoyi.manage.service.IFeExtinguisherService;

/**
 * 灭火器信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-02-03
 */
@Service
public class FeExtinguisherServiceImpl implements IFeExtinguisherService 
{
    @Autowired
    private FeExtinguisherMapper feExtinguisherMapper;

    /**
     * 查询灭火器信息
     * 
     * @param extinguisherId 灭火器信息主键
     * @return 灭火器信息
     */
    @Override
    public FeExtinguisher selectFeExtinguisherByExtinguisherId(Long extinguisherId)
    {
        return feExtinguisherMapper.selectFeExtinguisherByExtinguisherId(extinguisherId);
    }

    /**
     * 查询灭火器信息列表
     * 
     * @param feExtinguisher 灭火器信息
     * @return 灭火器信息
     */
    @Override
    public List<FeExtinguisher> selectFeExtinguisherList(FeExtinguisher feExtinguisher)
    {
        // 非管理员只能查询自己部门的数据
        if (!SecurityUtils.isAdmin()) {
            feExtinguisher.setDeptId(SecurityUtils.getDeptId());
        }
        return feExtinguisherMapper.selectFeExtinguisherList(feExtinguisher);
    }

    /**
     * 新增灭火器信息
     * 
     * @param feExtinguisher 灭火器信息
     * @return 结果
     */
    @Override
    public int insertFeExtinguisher(FeExtinguisher feExtinguisher)
    {
        feExtinguisher.setCreateTime(DateUtils.getNowDate());
        return feExtinguisherMapper.insertFeExtinguisher(feExtinguisher);
    }

    /**
     * 修改灭火器信息
     * 
     * @param feExtinguisher 灭火器信息
     * @return 结果
     */
    @Override
    public int updateFeExtinguisher(FeExtinguisher feExtinguisher)
    {
        feExtinguisher.setUpdateTime(DateUtils.getNowDate());
        return feExtinguisherMapper.updateFeExtinguisher(feExtinguisher);
    }

    /**
     * 批量删除灭火器信息
     * 
     * @param extinguisherIds 需要删除的灭火器信息主键
     * @return 结果
     */
    @Override
    public int deleteFeExtinguisherByExtinguisherIds(Long[] extinguisherIds)
    {
        return feExtinguisherMapper.deleteFeExtinguisherByExtinguisherIds(extinguisherIds);
    }

    /**
     * 删除灭火器信息信息
     * 
     * @param extinguisherId 灭火器信息主键
     * @return 结果
     */
    @Override
    public int deleteFeExtinguisherByExtinguisherId(Long extinguisherId)
    {
        return feExtinguisherMapper.deleteFeExtinguisherByExtinguisherId(extinguisherId);
    }
}
