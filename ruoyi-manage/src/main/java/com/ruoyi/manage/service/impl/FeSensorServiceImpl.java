package com.ruoyi.manage.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.manage.mapper.FeSensorMapper;
import com.ruoyi.manage.domain.FeSensor;
import com.ruoyi.manage.service.IFeSensorService;

/**
 * 传感器管理Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-02-03
 */
@Service
public class FeSensorServiceImpl implements IFeSensorService 
{
    @Autowired
    private FeSensorMapper feSensorMapper;

    /**
     * 查询传感器管理
     * 
     * @param sensorId 传感器管理主键
     * @return 传感器管理
     */
    @Override
    public FeSensor selectFeSensorBySensorId(Long sensorId)
    {
        return feSensorMapper.selectFeSensorBySensorId(sensorId);
    }

    /**
     * 查询传感器管理列表
     * 
     * @param feSensor 传感器管理
     * @return 传感器管理
     */
    @Override
    public List<FeSensor> selectFeSensorList(FeSensor feSensor)
    {
        // 非管理员只能查询自己部门的数据
        if (!SecurityUtils.isAdmin()) {
            feSensor.setDeptId(SecurityUtils.getDeptId());
        }
        return feSensorMapper.selectFeSensorList(feSensor);
    }

    /**
     * 新增传感器管理
     * 
     * @param feSensor 传感器管理
     * @return 结果
     */
    @Override
    public int insertFeSensor(FeSensor feSensor)
    {
        feSensor.setCreateTime(DateUtils.getNowDate());
        return feSensorMapper.insertFeSensor(feSensor);
    }

    /**
     * 修改传感器管理
     * 
     * @param feSensor 传感器管理
     * @return 结果
     */
    @Override
    public int updateFeSensor(FeSensor feSensor)
    {
        feSensor.setUpdateTime(DateUtils.getNowDate());
        return feSensorMapper.updateFeSensor(feSensor);
    }

    /**
     * 批量删除传感器管理
     * 
     * @param sensorIds 需要删除的传感器管理主键
     * @return 结果
     */
    @Override
    public int deleteFeSensorBySensorIds(Long[] sensorIds)
    {
        return feSensorMapper.deleteFeSensorBySensorIds(sensorIds);
    }

    /**
     * 删除传感器管理信息
     * 
     * @param sensorId 传感器管理主键
     * @return 结果
     */
    @Override
    public int deleteFeSensorBySensorId(Long sensorId)
    {
        return feSensorMapper.deleteFeSensorBySensorId(sensorId);
    }
}
