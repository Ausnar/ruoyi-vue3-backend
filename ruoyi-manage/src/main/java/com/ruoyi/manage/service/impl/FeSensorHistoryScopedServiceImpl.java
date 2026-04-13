package com.ruoyi.manage.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.manage.domain.FeSensor;
import com.ruoyi.manage.domain.FeSensorHistory;
import com.ruoyi.manage.mapper.FeSensorHistoryMapper;
import com.ruoyi.manage.mapper.FeSensorMapper;
import com.ruoyi.manage.service.IFeSensorHistoryService;
import com.ruoyi.system.service.ISysDeptService;

@Primary
@Service
public class FeSensorHistoryScopedServiceImpl implements IFeSensorHistoryService
{
    @Autowired
    private FeSensorHistoryMapper feSensorHistoryMapper;

    @Autowired
    private FeSensorMapper feSensorMapper;

    @Autowired
    private ISysDeptService sysDeptService;

    @Override
    public FeSensorHistory selectFeSensorHistoryByHistoryId(Long historyId)
    {
        return feSensorHistoryMapper.selectFeSensorHistoryByHistoryId(historyId);
    }

    @Override
    @DataScope(deptAlias = "s")
    public List<FeSensorHistory> selectFeSensorHistoryList(FeSensorHistory feSensorHistory)
    {
        checkSensorScope(feSensorHistory.getSensorId(), feSensorHistory.getSensorCode());
        return feSensorHistoryMapper.selectFeSensorHistoryList(feSensorHistory);
    }

    @Override
    public List<FeSensorHistory> selectFeSensorHistoryBySensorId(Long sensorId, String startTime, String endTime)
    {
        checkSensorScope(sensorId, null);
        return feSensorHistoryMapper.selectFeSensorHistoryBySensorId(sensorId, startTime, endTime);
    }

    @Override
    public int insertFeSensorHistory(FeSensorHistory feSensorHistory)
    {
        return feSensorHistoryMapper.insertFeSensorHistory(feSensorHistory);
    }

    @Override
    public int deleteFeSensorHistoryByHistoryId(Long historyId)
    {
        return feSensorHistoryMapper.deleteFeSensorHistoryByHistoryId(historyId);
    }

    @Override
    public int deleteFeSensorHistoryByHistoryIds(Long[] historyIds)
    {
        return feSensorHistoryMapper.deleteFeSensorHistoryByHistoryIds(historyIds);
    }

    @Override
    @Transactional
    public int insertFeSensorHistoryAndSync(FeSensorHistory feSensorHistory)
    {
        int result = feSensorHistoryMapper.insertFeSensorHistory(feSensorHistory);

        if (feSensorHistory.getSensorCode() != null)
        {
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

    private void checkSensorScope(Long sensorId, String sensorCode)
    {
        if (SecurityUtils.isAdmin())
        {
            return;
        }

        if (sensorId == null && (sensorCode == null || sensorCode.isEmpty()))
        {
            return;
        }

        FeSensor sensor = sensorId != null ? feSensorMapper.selectFeSensorBySensorId(sensorId) : feSensorMapper.selectBySensorCode(sensorCode);
        if (sensor == null || sensor.getDeptId() == null)
        {
            throw new ServiceException("没有权限访问该数据");
        }
        sysDeptService.checkDeptDataScope(sensor.getDeptId());
    }
}
