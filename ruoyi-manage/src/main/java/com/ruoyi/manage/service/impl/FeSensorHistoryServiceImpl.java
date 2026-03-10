package com.ruoyi.manage.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.manage.mapper.FeSensorHistoryMapper;
import com.ruoyi.manage.mapper.FeSensorMapper;
import com.ruoyi.manage.domain.FeSensorHistory;
import com.ruoyi.manage.domain.FeSensor;
import com.ruoyi.manage.service.IFeSensorHistoryService;

/**
 * 传感器历史数据Service业务层处理
 *
 * @author ruoyi
 * @date 2026-03-09
 */
@Service
public class FeSensorHistoryServiceImpl implements IFeSensorHistoryService
{
    @Autowired
    private FeSensorHistoryMapper feSensorHistoryMapper;

    @Autowired
    private FeSensorMapper feSensorMapper;

    /**
     * 查询传感器历史数据
     */
    @Override
    public FeSensorHistory selectFeSensorHistoryByHistoryId(Long historyId)
    {
        return feSensorHistoryMapper.selectFeSensorHistoryByHistoryId(historyId);
    }

    /**
     * 查询传感器历史数据列表
     */
    @Override
    public List<FeSensorHistory> selectFeSensorHistoryList(FeSensorHistory feSensorHistory)
    {
        return feSensorHistoryMapper.selectFeSensorHistoryList(feSensorHistory);
    }

    /**
     * 根据传感器ID查询历史数据（用于图表）
     */
    @Override
    public List<FeSensorHistory> selectFeSensorHistoryBySensorId(Long sensorId, String startTime, String endTime)
    {
        return feSensorHistoryMapper.selectFeSensorHistoryBySensorId(sensorId, startTime, endTime);
    }

    /**
     * 新增传感器历史数据
     */
    @Override
    public int insertFeSensorHistory(FeSensorHistory feSensorHistory)
    {
        return feSensorHistoryMapper.insertFeSensorHistory(feSensorHistory);
    }

    /**
     * 删除传感器历史数据
     */
    @Override
    public int deleteFeSensorHistoryByHistoryId(Long historyId)
    {
        return feSensorHistoryMapper.deleteFeSensorHistoryByHistoryId(historyId);
    }

    /**
     * 批量删除传感器历史数据
     */
    @Override
    public int deleteFeSensorHistoryByHistoryIds(Long[] historyIds)
    {
        return feSensorHistoryMapper.deleteFeSensorHistoryByHistoryIds(historyIds);
    }

    /**
     * 新增历史数据并同步更新传感器最新数据
     */
    @Override
    @Transactional
    public int insertFeSensorHistoryAndSync(FeSensorHistory feSensorHistory)
    {
        // 1. 新增历史数据
        int result = feSensorHistoryMapper.insertFeSensorHistory(feSensorHistory);

        // 2. 根据传感器编号更新传感器最新数据
        if (feSensorHistory.getSensorCode() != null) {
            FeSensor feSensor = new FeSensor();
            feSensor.setSensorCode(feSensorHistory.getSensorCode());
            feSensor.setPressure(feSensorHistory.getPressure());
            feSensor.setTemperature(feSensorHistory.getTemperature());
            feSensor.setBatteryLevel(feSensorHistory.getBatteryLevel());
            feSensor.setStatus(feSensorHistory.getStatus());
            feSensor.setLastOnlineTime(feSensorHistory.getCreateTime());
            feSensorMapper.updateFeSensorByCode(feSensor);
        }

        return result;
    }
}
