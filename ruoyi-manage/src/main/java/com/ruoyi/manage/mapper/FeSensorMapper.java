package com.ruoyi.manage.mapper;

import java.util.List;
import com.ruoyi.manage.domain.FeSensor;

/**
 * 传感器管理Mapper接口
 * 
 * @author ruoyi
 * @date 2026-02-03
 */
public interface FeSensorMapper 
{
    /**
     * 查询传感器管理
     * 
     * @param sensorId 传感器管理主键
     * @return 传感器管理
     */
    public FeSensor selectFeSensorBySensorId(Long sensorId);

    /**
     * 查询传感器管理列表
     * 
     * @param feSensor 传感器管理
     * @return 传感器管理集合
     */
    public List<FeSensor> selectFeSensorList(FeSensor feSensor);

    /**
     * 新增传感器管理
     * 
     * @param feSensor 传感器管理
     * @return 结果
     */
    public int insertFeSensor(FeSensor feSensor);

    /**
     * 修改传感器管理
     * 
     * @param feSensor 传感器管理
     * @return 结果
     */
    public int updateFeSensor(FeSensor feSensor);

    /**
     * 删除传感器管理
     * 
     * @param sensorId 传感器管理主键
     * @return 结果
     */
    public int deleteFeSensorBySensorId(Long sensorId);

    /**
     * 批量删除传感器管理
     * 
     * @param sensorIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteFeSensorBySensorIds(Long[] sensorIds);
}
