package com.ruoyi.manage.service;

import java.util.List;
import com.ruoyi.manage.domain.FeExtinguisher;

/**
 * 灭火器信息Service接口
 * 
 * @author ruoyi
 * @date 2026-02-03
 */
public interface IFeExtinguisherService 
{
    /**
     * 查询灭火器信息
     * 
     * @param extinguisherId 灭火器信息主键
     * @return 灭火器信息
     */
    public FeExtinguisher selectFeExtinguisherByExtinguisherId(Long extinguisherId);

    /**
     * 查询灭火器信息列表
     * 
     * @param feExtinguisher 灭火器信息
     * @return 灭火器信息集合
     */
    public List<FeExtinguisher> selectFeExtinguisherList(FeExtinguisher feExtinguisher);

    /**
     * 新增灭火器信息
     * 
     * @param feExtinguisher 灭火器信息
     * @return 结果
     */
    public int insertFeExtinguisher(FeExtinguisher feExtinguisher);

    /**
     * 修改灭火器信息
     * 
     * @param feExtinguisher 灭火器信息
     * @return 结果
     */
    public int updateFeExtinguisher(FeExtinguisher feExtinguisher);

    /**
     * 批量删除灭火器信息
     * 
     * @param extinguisherIds 需要删除的灭火器信息主键集合
     * @return 结果
     */
    public int deleteFeExtinguisherByExtinguisherIds(Long[] extinguisherIds);

    /**
     * 删除灭火器信息信息
     * 
     * @param extinguisherId 灭火器信息主键
     * @return 结果
     */
    public int deleteFeExtinguisherByExtinguisherId(Long extinguisherId);
}
