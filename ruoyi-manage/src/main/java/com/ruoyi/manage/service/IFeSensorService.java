package com.ruoyi.manage.service;

import java.util.List;
import com.ruoyi.manage.domain.FeSensor;

/**
 * 传感器信息Service接口
 * 
 * @author ruoyi
 * @date 2026-02-03
 */
public interface IFeSensorService 
{
    /**
     * 查询传感器信息
     * 
     * @param sensorId 传感器信息主键
     * @return 传感器信息
     */
    public FeSensor selectFeSensorBySensorId(Long sensorId);

    /**
     * 查询传感器信息列表
     * 
     * @param feSensor 传感器信息
     * @return 传感器信息集合
     */
    public List<FeSensor> selectFeSensorList(FeSensor feSensor);

    /**
     * 新增传感器信息
     * 
     * @param feSensor 传感器信息
     * @return 结果
     */
    public int insertFeSensor(FeSensor feSensor);

    /**
     * 修改传感器信息
     * 
     * @param feSensor 传感器信息
     * @return 结果
     */
    public int updateFeSensor(FeSensor feSensor);

    /**
     * 批量删除传感器信息
     * 
     * @param sensorIds 需要删除的传感器信息主键集合
     * @return 结果
     */
    public int deleteFeSensorBySensorIds(Long[] sensorIds);

    /**
     * 删除传感器信息
     * 
     * @param sensorId 传感器信息主键
     * @return 结果
     */
    public int deleteFeSensorBySensorId(Long sensorId);
}
